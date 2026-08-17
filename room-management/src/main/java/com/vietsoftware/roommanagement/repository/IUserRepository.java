package com.vietsoftware.roommanagement.repository;

import com.vietsoftware.roommanagement.entity.User;
import com.vietsoftware.roommanagement.enums.UserStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

/**
 * Spring Data JPA Repository interface for persistence operations on {@link User} entities.
 */
@Repository
public interface IUserRepository extends JpaRepository<User, UUID> {

    /**
     * Finds an active user by username in a single optimized query.
     *
     * @param username login username
     * @param status   required user status
     * @return an {@link Optional} containing the user if found and matching status
     */
    Optional<User> findByUsernameAndStatus(String username, UserStatus status);

    /**
     * Finds an active user by username eagerly fetching groups and roles in a single JOIN query to prevent N+1 queries.
     *
     * @param username login username
     * @return an {@link Optional} containing the user with eagerly fetched groups and roles
     */
    @Query("""
            SELECT DISTINCT u FROM User u
            LEFT JOIN FETCH u.groups g
            LEFT JOIN FETCH g.roles
            WHERE u.username = :username
            AND u.status = 'ACTIVE'
            """)
    Optional<User> findActiveUserWithRolesByUsername(@Param("username") String username);

    /**
     * Checks if a user record exists with the given username.
     *
     * @param username username to check
     * @return {@code true} if username exists, {@code false} otherwise
     */
    boolean existsByUsername(String username);

    /**
     * Checks if a user record exists with the given email address.
     *
     * @param email email address to check
     * @return {@code true} if email exists, {@code false} otherwise
     */
    boolean existsByEmail(String email);
}
