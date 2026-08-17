package com.vietsoftware.roommanagement.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.HashSet;
import java.util.Set;

/**
 * Entity representing a security permission entry, mapped from {@link com.vietsoftware.roommanagement.enums.ApiPermission}.
 *
 * <p>Uses UUID as primary key and enforces {@code name} uniqueness via a separate unique index.
 * Extends {@link BaseEntity} for auditing timestamps.</p>
 */
@Entity
@Table(name = "permissions",
        uniqueConstraints = @UniqueConstraint(name = "uk_permission_name", columnNames = "name"))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Permission extends BaseEntity {

    /**
     * Logical permission identifier name (e.g. "ROOM_SEARCH_ACTIVE").
     * Unique business key, not the primary key.
     */
    @Column(name = "name", nullable = false, unique = true, length = 100)
    String name;

    /**
     * Target API URI path pattern (e.g. "/api/v1/rooms/active").
     */
    @Column(name = "uri", nullable = false, length = 255)
    String uri;

    /**
     * Associated HTTP request method (e.g. "GET", "POST", "PUT").
     */
    @Column(name = "http_method", nullable = false, length = 10)
    String httpMethod;

    /**
     * Roles that are granted this permission.
     */
    @ManyToMany(mappedBy = "permissions", fetch = FetchType.LAZY)
    @Builder.Default
    Set<Role> roles = new HashSet<>();
}
