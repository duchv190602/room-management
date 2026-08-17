package com.vietsoftware.roommanagement.repository;

import com.vietsoftware.roommanagement.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

/**
 * Spring Data JPA Repository for persistence operations on {@link Role} entities.
 */
@Repository
public interface IRoleRepository extends JpaRepository<Role, UUID> {

    /**
     * Finds a role by its unique logical name identifier.
     *
     * @param name role name (e.g. "ADMIN", "USER")
     * @return {@link Optional} containing the role if found, or empty if not found
     */
    Optional<Role> findByName(String name);
}
