package com.group.Timetable.Generator.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.group.Timetable.Generator.Repository.RoomRepository;
import com.group.Timetable.Generator.Repository.UserRepository;
import com.group.Timetable.Generator.dto.RoomDTO;
import com.group.Timetable.Generator.entities.Room;
import com.group.Timetable.Generator.entities.User;
import com.group.Timetable.Generator.mapper.RoomMapper;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RoomService {

    private final RoomRepository roomRepo;
    private final UserRepository userRepo;
    private final RoomMapper mapper;

    private User getUser(Long userId) {
        return userRepo.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    // CREATE
    public RoomDTO saveRoom(RoomDTO dto, Long userId) {
        User user = getUser(userId);

        Room room = mapper.toEntity(dto);
        room.setUser(user);

        return mapper.toDTO(roomRepo.save(room));
    }

    // GET ALL
    public List<RoomDTO> getAllRooms(Long userId) {
        User user = getUser(userId);

        return roomRepo.findByUser(user)
                .stream()
                .map(mapper::toDTO)
                .collect(Collectors.toList());
    }

    // SEARCH
    public List<RoomDTO> searchRooms(String name, Long userId) {
        User user = getUser(userId);

        return roomRepo.findByRoomNameContainingIgnoreCaseAndUser(name, user)
                .stream()
                .map(mapper::toDTO)
                .collect(Collectors.toList());
    }

    // GET ONE
    public RoomDTO getRoom(Long roomId, Long userId) {
        User user = getUser(userId);

        Room room = roomRepo.findByRoomIdAndUser(roomId, user)
                .orElseThrow(() -> new RuntimeException("Room not found"));

        return mapper.toDTO(room);
    }

    // UPDATE
    public RoomDTO updateRoom(Long roomId, RoomDTO dto, Long userId) {
        User user = getUser(userId);

        Room room = roomRepo.findByRoomIdAndUser(roomId, user)
                .orElseThrow(() -> new RuntimeException("Room not found"));

        room.setRoomName(dto.getRoomName());
        room.setRoomType(dto.getRoomType());
        room.setCapacity(dto.getCapacity());

        return mapper.toDTO(roomRepo.save(room));
    }

    // DELETE
    public void deleteRoom(Long roomId, Long userId) {
        User user = getUser(userId);

        Room room = roomRepo.findByRoomIdAndUser(roomId, user)
                .orElseThrow(() -> new RuntimeException("Room not found"));

        roomRepo.delete(room);
    }
}
