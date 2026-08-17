package com.vietsoftware.roommanagement.mapper;
import com.vietsoftware.roommanagement.dto.request.CreateRoomRequest;
import com.vietsoftware.roommanagement.dto.request.UpdateRoomRequest;
import com.vietsoftware.roommanagement.dto.response.RoomResponse;
import com.vietsoftware.roommanagement.entity.Room;
import org.mapstruct.*;

/**
 * MapStruct mapper interface for converting between Room entity and DTO objects.
 */
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface IRoomMapper {

    /**
     * Maps a {@link CreateRoomRequest} DTO to a {@link Room} entity.
     *
     * @param createRoomRequest the create room request DTO
     * @return mapped {@link Room} entity
     */
    @Mapping(target = "status", ignore = true)
    Room toEntity(CreateRoomRequest createRoomRequest);


    /**
     * Maps a {@link Room} entity to a {@link RoomResponse} DTO.
     *
     * @param room the room entity
     * @return mapped {@link RoomResponse} DTO
     */
    RoomResponse toResponse(Room room);

    /**
     * Updates an existing {@link Room} entity in-place from an {@link UpdateRoomRequest} DTO, ignoring null properties.
     *
     * @param updateRoomRequest the update room request DTO
     * @param room              target {@link Room} entity to update
     */
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateEntity(UpdateRoomRequest updateRoomRequest, @MappingTarget Room room);
}


