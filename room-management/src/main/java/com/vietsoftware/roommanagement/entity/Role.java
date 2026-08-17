package com.vietsoftware.roommanagement.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.HashSet;
import java.util.Set;

/**
 * Entity representing a security role (e.g. "ADMIN", "USER") that aggregates a set of permissions.
 *
 * <p>Uses UUID as primary key and enforces {@code name} uniqueness via a separate unique index.
 * Extends {@link BaseEntity} for auditing timestamps.</p>
 */
@Entity
@Table(name = "roles",
        uniqueConstraints = @UniqueConstraint(name = "uk_role_name", columnNames = "name"))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Role extends BaseEntity {

    /**
     * Logical role name identifier (e.g. "ADMIN", "USER").
     * Unique business key, not the primary key.
     */
    @Column(name = "name", nullable = false, unique = true, length = 50)
    String name;

    /**
     * Human-readable description of the role's scope.
     */
    @Column(name = "description", length = 255)
    String description;

    /**
     * Set of permissions associated with this role.
     */
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "role_permissions",
            joinColumns = @JoinColumn(name = "role_id"),
            inverseJoinColumns = @JoinColumn(name = "permission_id")
    )
    @Builder.Default
    Set<Permission> permissions = new HashSet<>();
}
