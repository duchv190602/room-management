package com.vietsoftware.roommanagement.service;

import com.vietsoftware.roommanagement.constant.ApiConstants;
import com.vietsoftware.roommanagement.dto.request.CreateRoomRequest;
import com.vietsoftware.roommanagement.dto.request.SearchRoomRequest;
import com.vietsoftware.roommanagement.dto.request.UpdateRoomRequest;
import com.vietsoftware.roommanagement.dto.response.PageResponse;
import com.vietsoftware.roommanagement.dto.response.RoomResponse;
import com.vietsoftware.roommanagement.entity.Room;
import com.vietsoftware.roommanagement.enums.RoomStatus;
import com.vietsoftware.roommanagement.exception.AppException;
import com.vietsoftware.roommanagement.exception.ErrorCode;
import com.vietsoftware.roommanagement.mapper.IRoomMapper;
import com.vietsoftware.roommanagement.repository.IRoomRepository;
import com.vietsoftware.roommanagement.repository.specification.RoomSpecification;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

/**
 * Service implementation of {@link IRoomService} managing room lifecycle, search, pagination, and status updates.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RoomServiceImpl implements IRoomService {

    private final IRoomRepository roomRepository;
    private final IRoomMapper roomMapper;

    /**
     * Searches and paginates active rooms matching filter criteria.
     * Force filters status to {@link RoomStatus#ACTIVE} regardless of request payload.
     *
     * @param searchRoomRequest filter and pagination parameters payload
     * @return {@link PageResponse} containing active {@link RoomResponse} page content
     */
    @Override
    @Transactional(readOnly = true)
    public PageResponse<RoomResponse> searchActiveRooms(SearchRoomRequest searchRoomRequest) {
        log.info("Searching active rooms with request parameters: {}", searchRoomRequest);

        Specification<Room> spec = RoomSpecification.hasStatus(RoomStatus.ACTIVE)
                .and(RoomSpecification.hasRoomCode(searchRoomRequest.getRoomCode()))
                .and(RoomSpecification.hasNameLike(searchRoomRequest.getName()))
                .and(RoomSpecification.hasCapacity(searchRoomRequest.getCapacity()));

        Pageable pageable = createPageable(searchRoomRequest);
        Page<Room> pageResult = roomRepository.findAll(spec, pageable);

        return toPageResponse(pageResult);
    }

    /**
     * Retrieves an active room by its UUID identifier.
     * Uses single-query findByIdAndStatus for optimal database interaction.
     *
     * @param roomId unique room UUID identifier
     * @return {@link RoomResponse} containing details of the active room
     * @throws AppException if room is not found or status is not ACTIVE
     */
    @Override
    @Transactional(readOnly = true)
    public RoomResponse getActiveRoomById(UUID roomId) {
        log.info("Fetching active room by ID: {}", roomId);

        Room room = roomRepository.findByIdAndStatus(roomId, RoomStatus.ACTIVE)
                .orElseThrow(() -> new AppException(ErrorCode.ROOM_NOT_FOUND));

        return roomMapper.toResponse(room);
    }

    /**
     * Searches and paginates all rooms across all operational statuses (ADMIN view).
     *
     * @param searchRoomRequest filter and pagination parameters payload (optional status filter)
     * @return {@link PageResponse} containing matching {@link RoomResponse} page content
     */
    @Override
    @Transactional(readOnly = true)
    public PageResponse<RoomResponse> searchAllRooms(SearchRoomRequest searchRoomRequest) {
        log.info("Admin searching all rooms with request parameters: {}", searchRoomRequest);

        Specification<Room> spec = RoomSpecification.hasStatus(searchRoomRequest.getStatus())
                .and(RoomSpecification.hasRoomCode(searchRoomRequest.getRoomCode()))
                .and(RoomSpecification.hasNameLike(searchRoomRequest.getName()))
                .and(RoomSpecification.hasCapacity(searchRoomRequest.getCapacity()));

        Pageable pageable = createPageable(searchRoomRequest);
        Page<Room> pageResult = roomRepository.findAll(spec, pageable);

        return toPageResponse(pageResult);
    }

    /**
     * Retrieves a room by its UUID identifier regardless of status (ADMIN view).
     *
     * @param roomId unique room UUID identifier
     * @return {@link RoomResponse} containing details of the room
     * @throws AppException if room is not found
     */
    @Override
    @Transactional(readOnly = true)
    public RoomResponse getRoomById(UUID roomId) {
        log.info("Admin fetching room by ID: {}", roomId);

        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new AppException(ErrorCode.ROOM_NOT_FOUND));

        return roomMapper.toResponse(room);
    }

    /**
     * Creates a new room entry with default ACTIVE operational status.
     *
     * @param createRoomRequest new room payload (roomCode, name, capacity)
     * @return {@link RoomResponse} containing details of the created room
     * @throws AppException if roomCode already exists
     */
    @Override
    @Transactional
    public RoomResponse createRoom(CreateRoomRequest createRoomRequest) {
        log.info("Creating new room with code: {}", createRoomRequest.getRoomCode());

        if (roomRepository.existsByRoomCode(createRoomRequest.getRoomCode())) {
            throw new AppException(ErrorCode.ROOM_CODE_ALREADY_EXISTS);
        }

        Room room = roomMapper.toEntity(createRoomRequest);
        room.setStatus(RoomStatus.ACTIVE);

        Room savedRoom = roomRepository.save(room);
        log.info("Room created successfully with ID: {}", savedRoom.getId());

        return roomMapper.toResponse(savedRoom);
    }

    /**
     * Updates details of an existing room entry by its UUID identifier.
     *
     * @param roomId            unique room UUID identifier
     * @param updateRoomRequest update room payload
     * @return {@link RoomResponse} containing updated room details
     * @throws AppException if room is not found or new roomCode conflicts with another room
     */
    @Override
    @Transactional
    public RoomResponse updateRoom(UUID roomId, UpdateRoomRequest updateRoomRequest) {
        log.info("Updating room with ID: {}", roomId);

        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new AppException(ErrorCode.ROOM_NOT_FOUND));

        if (updateRoomRequest.getRoomCode() != null && !updateRoomRequest.getRoomCode().equals(room.getRoomCode())) {
            if (roomRepository.existsByRoomCodeAndIdNot(updateRoomRequest.getRoomCode(), roomId)) {
                throw new AppException(ErrorCode.ROOM_CODE_ALREADY_EXISTS);
            }
        }

        roomMapper.updateEntity(updateRoomRequest, room);
        Room updatedRoom = roomRepository.save(room);
        log.info("Room with ID [{}] updated successfully.", updatedRoom.getId());

        return roomMapper.toResponse(updatedRoom);
    }

    /**
     * Updates the operational status of an existing room entry.
     *
     * @param roomId unique room UUID identifier
     * @param status new target {@link RoomStatus} (ACTIVE or INACTIVE)
     * @return {@link RoomResponse} containing updated room details
     * @throws AppException if room is not found
     */
    @Override
    @Transactional
    public RoomResponse updateRoomStatus(UUID roomId, RoomStatus status) {
        log.info("Updating status of room [{}] to {}", roomId, status);

        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new AppException(ErrorCode.ROOM_NOT_FOUND));

        room.setStatus(status);
        Room savedRoom = roomRepository.save(room);
        log.info("Room status updated successfully for ID: {}", savedRoom.getId());

        return roomMapper.toResponse(savedRoom);
    }

    /**
     * Soft deletes an existing room entry by updating its status to INACTIVE.
     *
     * @param roomId unique room UUID identifier
     * @throws AppException if room is not found
     */
    @Override
    @Transactional
    public void deleteRoom(UUID roomId) {
        log.info("Soft deleting room with ID: {}", roomId);

        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new AppException(ErrorCode.ROOM_NOT_FOUND));

        room.setStatus(RoomStatus.INACTIVE);
        roomRepository.save(room);
        log.info("Room with ID [{}] soft deleted (status set to INACTIVE).", roomId);
    }

    /**
     * Creates a {@link Pageable} instance from request parameters with sorting validation.
     *
     * @param searchRoomRequest search room request payload
     * @return constructed {@link Pageable} instance
     */
    private Pageable createPageable(SearchRoomRequest searchRoomRequest) {
        int pageNo = (searchRoomRequest.getPageNo() != null && searchRoomRequest.getPageNo() >= 0) ? searchRoomRequest.getPageNo() : 0;
        int pageSize = (searchRoomRequest.getPageSize() != null && searchRoomRequest.getPageSize() > 0) ? searchRoomRequest.getPageSize() : 10;

        return PageRequest.of(pageNo, pageSize);
    }

    /**
     * Converts a Spring Data {@link Page} of {@link Room} entities into a standardized {@link PageResponse} DTO.
     *
     * @param pageResult Spring Data Page result
     * @return mapped {@link PageResponse} of {@link RoomResponse}
     */
    private PageResponse<RoomResponse> toPageResponse(Page<Room> pageResult) {
        List<RoomResponse> content = pageResult.getContent().stream()
                .map(roomMapper::toResponse)
                .toList();

        return PageResponse.<RoomResponse>builder()
                .content(content)
                .pageNo(pageResult.getNumber())
                .pageSize(pageResult.getSize())
                .totalElements(pageResult.getTotalElements())
                .totalPages(pageResult.getTotalPages())
                .last(pageResult.isLast())
                .build();
    }
}
