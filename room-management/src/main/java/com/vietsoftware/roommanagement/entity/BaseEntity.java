package com.vietsoftware.roommanagement.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;
import java.util.UUID;

/**
 * Mapped superclass providing common identifier and auditing timestamp attributes for all entities.
 */
@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class BaseEntity {

    /**
     * Unique identifier (UUID) of the entity.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    UUID id;

    /**
     * Timestamp when the entity record was created. Automatically managed by JPA Auditing.
     */
    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    Instant createdAt;

    /**
     * Timestamp when the entity record was last updated. Automatically managed by JPA Auditing.
     */
    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    Instant updatedAt;
}
