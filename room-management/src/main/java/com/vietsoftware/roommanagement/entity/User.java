package com.vietsoftware.roommanagement.entity;

import com.vietsoftware.roommanagement.constant.ApiConstants;
//import com.vietsoftware.roommanagement.converter.CryptoAttributeConverter;
import com.vietsoftware.roommanagement.enums.UserStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.experimental.FieldNameConstants;

import java.util.HashSet;
import java.util.Set;

/**
 * Entity representing a user record in the database, holding credentials, profile info, and group memberships.
 *
 * <p>Extends {@link BaseEntity} to inherit UUID primary key and auditing timestamps ({@code createdAt}, {@code updatedAt}).</p>
 */
@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@FieldNameConstants
public class User extends BaseEntity {

    /**
     * Unique login username.
     */
    @NotBlank
    @Size(min = ApiConstants.USERNAME_MIN_LENGTH, max = ApiConstants.USERNAME_MAX_LENGTH)
    @Column(name = "username", nullable = false, unique = true, length = 50)
    String username;

    /**
     * Unique email address.
     */
    @NotBlank
    @Email
    @Size(max = ApiConstants.EMAIL_MAX_LENGTH)
//    @Convert(converter = CryptoAttributeConverter.class)
    @Column(name = "email", nullable = false, unique = true, length = 512)
    String email;

    /**
     * BCrypt-hashed password. Never returned in responses.
     */
    @NotBlank
    @Column(name = "password", nullable = false, length = 255)
    String password;

    /**
     * Full display name of the user.
     */
    @NotBlank
    @Size(max = 100)
    @Column(name = "full_name", nullable = false, length = 100)
    String fullName;

    /**
     * Account lifecycle status (ACTIVE or INACTIVE).
     */
    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    @Builder.Default
    UserStatus status = UserStatus.ACTIVE;

    /**
     * User groups that this user belongs to. Resolves roles and permissions transitively.
     */
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "user_group_members",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "group_id")
    )
    @Builder.Default
    Set<UserGroup> groups = new HashSet<>();
}
