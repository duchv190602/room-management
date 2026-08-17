package com.vietsoftware.roommanagement.mapper;

import com.vietsoftware.roommanagement.dto.response.UserResponse;
import com.vietsoftware.roommanagement.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

/**
 * MapStruct mapper interface for converting between {@link User} entity and DTO objects.
 */
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface IUserMapper {

    /**
     * Maps a {@link User} entity to a {@link UserResponse} DTO.
     *
     * @param user the user entity
     * @return mapped {@link UserResponse} DTO
     */
    UserResponse toResponse(User user);
}
