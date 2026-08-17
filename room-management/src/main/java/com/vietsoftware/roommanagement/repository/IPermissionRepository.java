package com.vietsoftware.roommanagement.repository;

import com.vietsoftware.roommanagement.entity.Permission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

/**
 * Spring Data JPA Repository for persistence operations on {@link Permission} entities.
 */
@Repository
public interface IPermissionRepository extends JpaRepository<Permission, UUID> {

    /**
     * Finds a permission by its unique logical name identifier.
     *
     * @param name permission name (e.g. "ROOM_SEARCH_ACTIVE")
     * @return {@link Optional} containing the permission if found, or empty if not found
     */
    Optional<Permission> findByName(String name);
}
