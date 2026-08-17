package com.vietsoftware.roommanagement.configuration;

import com.vietsoftware.roommanagement.entity.Permission;
import com.vietsoftware.roommanagement.entity.Role;
import com.vietsoftware.roommanagement.entity.User;
import com.vietsoftware.roommanagement.entity.UserGroup;
import com.vietsoftware.roommanagement.enums.ApiPermission;
import com.vietsoftware.roommanagement.enums.RoleType;
import com.vietsoftware.roommanagement.enums.UserGroupType;
import com.vietsoftware.roommanagement.enums.UserStatus;
import com.vietsoftware.roommanagement.repository.IPermissionRepository;
import com.vietsoftware.roommanagement.repository.IRoleRepository;
import com.vietsoftware.roommanagement.repository.IUserGroupRepository;
import com.vietsoftware.roommanagement.repository.IUserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * Startup application runner component that synchronizes {@link ApiPermission} enum definitions to DB,
 * seeds default roles from {@link RoleType}, user groups from {@link UserGroupType}, and default admin account on every application start.
 *
 * <p>Idempotent: uses findByName / findByUsername with upsert semantics so repeated restarts and enum changes are safely synchronized.</p>
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer implements ApplicationRunner {

    private final IPermissionRepository permissionRepository;
    private final IRoleRepository roleRepository;
    private final IUserGroupRepository userGroupRepository;
    private final IUserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AdminInitProperties adminInitProperties;

    /**
     * Entry point called once when the application context has fully started.
     *
     * @param args application startup arguments
     */
    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        Map<String, Permission> permissionMap = syncPermissions();
        Map<String, Role> roleMap = seedRoles(permissionMap);
        Map<String, UserGroup> groupMap = seedUserGroups(roleMap);
        seedDefaultAdmin(groupMap.get(UserGroupType.ADMIN_GROUP.name()));
        log.info("DataInitializer completed successfully.");
    }

    /**
     * Synchronizes {@link ApiPermission} enum entries to the DB {@code permissions} table.
     * Existing records are updated in-place; new records are inserted.
     *
     * @return map of permission name → saved {@link Permission} entity for downstream use
     */
    private Map<String, Permission> syncPermissions() {
        Map<String, Permission> result = new LinkedHashMap<>();

        for (ApiPermission apiPermission : ApiPermission.values()) {
            Permission permission = permissionRepository.findByName(apiPermission.name())
                    .orElseGet(() -> Permission.builder().name(apiPermission.name()).build());

            permission.setUri(apiPermission.getUriPattern());
            permission.setHttpMethod(apiPermission.getHttpMethod());

            result.put(permission.getName(), permissionRepository.save(permission));
        }

        log.info("Synchronized {} API permissions to database.", result.size());
        return result;
    }

    /**
     * Seeds roles from {@link RoleType} and assigns permissions from the synced permission map.
     * Role membership is re-evaluated on each startup from {@link ApiPermission} to ensure changes in Enum are safely synced to DB.
     *
     * @param permissionMap name → permission entity map from {@link #syncPermissions()}
     * @return map of role name → saved {@link Role} entity for downstream use
     */
    private Map<String, Role> seedRoles(Map<String, Permission> permissionMap) {
        Map<String, Role> roleMap = new LinkedHashMap<>();

        // Collect which permissions each RoleType should have
        Map<RoleType, Set<Permission>> roleToPermissions = new LinkedHashMap<>();
        for (RoleType roleType : RoleType.values()) {
            roleToPermissions.put(roleType, new LinkedHashSet<>());
        }

        for (ApiPermission apiPermission : ApiPermission.values()) {
            Permission permission = permissionMap.get(apiPermission.name());
            for (RoleType roleType : apiPermission.getAllowedRoles()) {
                roleToPermissions.computeIfAbsent(roleType, k -> new LinkedHashSet<>()).add(permission);
            }
        }

        roleToPermissions.forEach((roleType, permissions) -> {
            String roleName = roleType.name();
            Role role = roleRepository.findByName(roleName).orElseGet(() ->
                    roleRepository.save(Role.builder()
                            .name(roleName)
                            .description(roleType.getDescription())
                            .build())
            );
            role.setDescription(roleType.getDescription());
            role.getPermissions().clear();
            role.getPermissions().addAll(permissions);
            roleMap.put(roleName, roleRepository.save(role));
            log.info("Role [{}] synchronized with {} permission(s).", roleName, permissions.size());
        });

        return roleMap;
    }

    /**
     * Seeds default user groups from {@link UserGroupType} and links each group to its default role.
     *
     * @param roleMap name → role entity map from {@link #seedRoles(Map)}
     * @return map of group name → saved {@link UserGroup} entity
     */
    private Map<String, UserGroup> seedUserGroups(Map<String, Role> roleMap) {
        Map<String, UserGroup> groupMap = new LinkedHashMap<>();

        for (UserGroupType groupType : UserGroupType.values()) {
            String groupName = groupType.name();
            String defaultRoleName = groupType.getDefaultRole().name();
            UserGroup group = seedGroup(groupName, groupType.getDescription(), defaultRoleName, roleMap);
            groupMap.put(groupName, group);
        }

        return groupMap;
    }

    /**
     * Upserts a single {@link UserGroup} and associates it with the given role.
     *
     * @param groupName   group logical name
     * @param description group description
     * @param roleName    role name to associate
     * @param roleMap     name → role entity map
     * @return saved {@link UserGroup} entity
     */
    private UserGroup seedGroup(String groupName, String description, String roleName, Map<String, Role> roleMap) {
        UserGroup group = userGroupRepository.findByName(groupName).orElseGet(() ->
                userGroupRepository.save(UserGroup.builder()
                        .name(groupName)
                        .description(description)
                        .build())
        );

        group.setDescription(description);
        Role role = roleMap.get(roleName);
        if (role != null) {
            group.getRoles().add(role);
            group = userGroupRepository.save(group);
        }

        log.info("UserGroup [{}] initialized and linked with role [{}].", groupName, roleName);
        return group;
    }

    /**
     * Seeds a default Administrator user account if one does not already exist.
     *
     * @param adminGroup Administrator group entity to attach to the default admin user
     */
    private void seedDefaultAdmin(UserGroup adminGroup) {
        String adminUsername = adminInitProperties.getUsername();
        if (!userRepository.existsByUsername(adminUsername)) {
            User defaultAdmin = User.builder()
                    .username(adminUsername)
                    .email(adminInitProperties.getEmail())
                    .password(passwordEncoder.encode(adminInitProperties.getPassword()))
                    .fullName(adminInitProperties.getFullName())
                    .status(UserStatus.ACTIVE)
                    .groups(Set.of(adminGroup))
                    .build();

            userRepository.save(defaultAdmin);
            log.info("Default admin account created successfully with username: {}", adminUsername);
        } else {
            log.info("Default admin account [{}] already exists.", adminUsername);
        }
    }
}
