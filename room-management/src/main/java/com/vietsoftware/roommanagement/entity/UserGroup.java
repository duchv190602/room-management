package com.vietsoftware.roommanagement.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.HashSet;
import java.util.Set;

/**
 * Entity representing a user group (e.g. "ADMIN_GROUP", "DEFAULT_USER_GROUP") aggregating security roles.
 *
 * <p>Uses UUID as primary key and enforces {@code name} uniqueness via a separate unique index.
 * Extends {@link BaseEntity} for auditing timestamps.</p>
 */
@Entity
@Table(name = "user_groups",
        uniqueConstraints = @UniqueConstraint(name = "uk_user_group_name", columnNames = "name"))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserGroup extends BaseEntity {

    /**
     * Logical group name identifier (e.g. "ADMIN_GROUP", "DEFAULT_USER_GROUP").
     * Unique business key, not the primary key.
     */
    @Column(name = "name", nullable = false, unique = true, length = 50)
    String name;

    /**
     * Human-readable description of the group's purpose.
     */
    @Column(name = "description", length = 255)
    String description;

    /**
     * Set of security roles assigned to this group.
     */
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "user_group_roles",
            joinColumns = @JoinColumn(name = "group_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    @Builder.Default
    Set<Role> roles = new HashSet<>();
}
