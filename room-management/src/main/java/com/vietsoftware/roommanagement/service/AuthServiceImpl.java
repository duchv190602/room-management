package com.vietsoftware.roommanagement.service;

import com.vietsoftware.roommanagement.configuration.JwtProperties;
import com.vietsoftware.roommanagement.dto.JwtPayload;
import com.vietsoftware.roommanagement.dto.request.LoginRequest;
import com.vietsoftware.roommanagement.dto.request.RefreshTokenRequest;
import com.vietsoftware.roommanagement.dto.request.RegisterRequest;
import com.vietsoftware.roommanagement.dto.response.AuthResponse;
import com.vietsoftware.roommanagement.dto.response.UserResponse;
import com.vietsoftware.roommanagement.entity.RefreshToken;
import com.vietsoftware.roommanagement.entity.User;
import com.vietsoftware.roommanagement.entity.UserGroup;
import com.vietsoftware.roommanagement.enums.UserGroupType;
import com.vietsoftware.roommanagement.enums.UserStatus;
import com.vietsoftware.roommanagement.exception.AppException;
import com.vietsoftware.roommanagement.exception.ErrorCode;
import com.vietsoftware.roommanagement.mapper.IUserMapper;
import com.vietsoftware.roommanagement.repository.IRefreshTokenRepository;
import com.vietsoftware.roommanagement.repository.IUserGroupRepository;
import com.vietsoftware.roommanagement.repository.IUserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.Instant;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Service implementation of {@link IAuthService} managing user registration, login, token refresh, and logout.
 *
 * <p><b>Token lifecycle:</b>
 * <ul>
 *   <li><b>Login</b>: Issues a short-lived JWT access token (default 15 min) and a long-lived refresh token (default 7 days).
 *       Refresh token is stored directly in DB.</li>
 *   <li><b>Refresh</b>: Client sends raw refresh token → looked up in DB → if valid and not expired,
 *       a new access token is issued (refresh token is rotated).</li>
 *   <li><b>Logout</b>: The access token's {@code jti} is added to the {@code invalidated_tokens} blacklist for its remaining
 *       validity window. The refresh token is deleted from DB.</li>
 * </ul>
 * </p>
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements IAuthService {

    private final IUserRepository userRepository;
    private final IUserGroupRepository userGroupRepository;
    private final IRefreshTokenRepository refreshTokenRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final IUserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtProperties jwtProperties;

    /**
     * Registers a new user account with default USER role group.
     *
     * @param userRegisterRequest user registration payload containing username, email, password, and fullName
     * @return {@link UserResponse} containing the created user details
     * @throws AppException with {@code USERNAME_ALREADY_EXISTS} if username is taken
     * @throws AppException with {@code EMAIL_ALREADY_EXISTS} if email is already registered
     */
    @Override
    @Transactional
    public UserResponse register(RegisterRequest userRegisterRequest) {
        log.info("Processing registration for username: {}", userRegisterRequest.getUsername());

        if (userRepository.existsByUsername(userRegisterRequest.getUsername())) {
            throw new AppException(ErrorCode.USERNAME_ALREADY_EXISTS);
        }
        if (userRepository.existsByEmail(userRegisterRequest.getEmail())) {
            throw new AppException(ErrorCode.EMAIL_ALREADY_EXISTS);
        }

        UserGroup defaultGroup = userGroupRepository.findByName(UserGroupType.DEFAULT_USER_GROUP.name())
                .orElseThrow(() -> new AppException(ErrorCode.INTERNAL_SERVER_ERROR));

        User user = User.builder()
                .username(userRegisterRequest.getUsername())
                .email(userRegisterRequest.getEmail())
                .password(passwordEncoder.encode(userRegisterRequest.getPassword()))
                .fullName(userRegisterRequest.getFullName())
                .status(UserStatus.ACTIVE)
                .groups(Set.of(defaultGroup))
                .build();

        User savedUser = userRepository.save(user);
        log.info("User [{}] registered with ID: {}", savedUser.getUsername(), savedUser.getId());

        return userMapper.toResponse(savedUser);
    }

    /**
     * Authenticates user credentials and issues both a JWT access token and a refresh token.
     *
     * @param userLoginRequest login payload containing username and password
     * @return {@link AuthResponse} containing access token, refresh token, type, and expiry
     * @throws AppException with {@code INVALID_CREDENTIALS} if username or password is incorrect
     */
    @Override
    @Transactional
    public AuthResponse login(LoginRequest userLoginRequest) {
        log.info("Processing login for username: {}", userLoginRequest.getUsername());

        // Single query fetching user + groups + roles (JOIN FETCH to avoid N+1)
        User user = userRepository.findActiveUserWithRolesByUsername(userLoginRequest.getUsername())
                .orElseThrow(() -> new AppException(ErrorCode.INVALID_CREDENTIALS));

        if (!passwordEncoder.matches(userLoginRequest.getPassword(), user.getPassword())) {
            throw new AppException(ErrorCode.INVALID_CREDENTIALS);
        }

        Set<String> roles = extractRoleNames(user);
        String accessToken = jwtTokenProvider.generateAccessToken(user.getId(), user.getUsername(), roles);

        // Generate and persist refresh token directly
        String rawRefreshToken = generateRawRefreshToken();
        persistRefreshToken(user, rawRefreshToken);

        log.info("User [{}] authenticated successfully.", user.getUsername());

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(rawRefreshToken)
                .expiresIn(jwtProperties.getAccessTokenExpiry())
                .build();
    }

    /**
     * Issues a new access token using a valid, non-expired refresh token.
     * Rotates the refresh token on each use (old token deleted, new one issued).
     *
     * @param refreshTokenRequest payload containing the raw refresh token string
     * @return {@link AuthResponse} containing a new access token and new refresh token
     * @throws AppException with {@code INVALID_TOKEN} if refresh token is not found or expired
     */
    @Override
    @Transactional
    public AuthResponse refreshToken(RefreshTokenRequest refreshTokenRequest) {
        log.info("Processing refresh token request.");

        RefreshToken stored = refreshTokenRepository.findByToken(refreshTokenRequest.getRefreshToken())
                .orElseThrow(() -> new AppException(ErrorCode.INVALID_TOKEN));

        if (stored.isExpired()) {
            refreshTokenRepository.delete(stored);
            throw new AppException(ErrorCode.TOKEN_EXPIRED);
        }

        // Fetch user with roles (JOIN FETCH) by ID to construct new access token claims
        User user = userRepository.findActiveUserWithRolesById(stored.getUser().getId())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        Set<String> roles = extractRoleNames(user);
        String newAccessToken = jwtTokenProvider.generateAccessToken(user.getId(), user.getUsername(), roles);

        // Rotate refresh token
        refreshTokenRepository.delete(stored);
        String newRawRefreshToken = generateRawRefreshToken();
        persistRefreshToken(user, newRawRefreshToken);

        log.info("Refresh token rotated for user [{}].", user.getUsername());

        return AuthResponse.builder()
                .accessToken(newAccessToken)
                .refreshToken(newRawRefreshToken)
                .expiresIn(jwtProperties.getAccessTokenExpiry())
                .build();
    }

    /**
     * Deletes the provided refresh token from DB.
     *
     * @param refreshToken raw refresh token string to revoke
     * @throws AppException with {@code INVALID_TOKEN} if refresh token is not found
     */
    @Override
    @Transactional
    public void logout(String refreshToken) {
        log.info("Processing logout request.");

        // Delete the refresh token from DB
        if (StringUtils.hasText(refreshToken)) {
            refreshTokenRepository.findByToken(refreshToken).ifPresent(refreshTokenRepository::delete);
        }

        log.info("Logout completed successfully.");
    }

    // ─── Private Helper Methods ───────────────────────────────────────────

    /**
     * Extracts role name strings from a user's group memberships.
     *
     * @param user user entity (groups + roles must be eagerly loaded)
     * @return set of role name strings
     */
    private Set<String> extractRoleNames(User user) {
        return user.getGroups().stream()
                .flatMap(group -> group.getRoles().stream())
                .map(role -> role.getName())
                .collect(Collectors.toSet());
    }

    /**
     * Generates a cryptographically random refresh token string (UUID-based, URL-safe).
     *
     * @return raw refresh token string
     */
    private String generateRawRefreshToken() {
        return UUID.randomUUID().toString().replace("-", "") + UUID.randomUUID().toString().replace("-", "");
    }

    /**
     * Persists a new refresh token record for the given user.
     *
     * @param user          owner user entity
     * @param rawRefreshToken raw refresh token string
     */
    private void persistRefreshToken(User user, String rawRefreshToken) {
        Instant expiresAt = Instant.now().plusSeconds(jwtProperties.getRefreshTokenExpiry());

        RefreshToken refreshToken = RefreshToken.builder()
                .user(user)
                .token(rawRefreshToken)
                .expiresAt(expiresAt)
                .build();

        refreshTokenRepository.save(refreshToken);
    }

}
