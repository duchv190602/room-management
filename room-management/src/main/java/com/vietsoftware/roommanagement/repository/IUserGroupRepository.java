package com.vietsoftware.roommanagement.repository;

import com.vietsoftware.roommanagement.entity.UserGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

/**
 * Spring Data JPA Repository for persistence operations on {@link UserGroup} entities.
 */
@Repository
public interface IUserGroupRepository extends JpaRepository<UserGroup, UUID> {

    /**
     * Finds a user group by its unique logical name identifier.
     *
     * @param name group name (e.g. "DEFAULT_USER_GROUP", "ADMIN_GROUP")
     * @return {@link Optional} containing the user group if found, or empty if not found
     */
    Optional<UserGroup> findByName(String name);
}
