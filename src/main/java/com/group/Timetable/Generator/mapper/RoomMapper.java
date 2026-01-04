package com.group.Timetable.Generator.mapper;

import org.springframework.stereotype.Component;

import com.group.Timetable.Generator.dto.RoomDTO;
import com.group.Timetable.Generator.entities.Room;

@Component
public class RoomMapper {

    public RoomDTO toDTO(Room room) {
        return RoomDTO.builder()
                .roomId(room.getRoomId())
                .roomName(room.getRoomName())
                .roomType(room.getRoomType())
                .capacity(room.getCapacity())
                .build();
    }

    public Room toEntity(RoomDTO dto) {
        return Room.builder()
                .roomId(dto.getRoomId())
                .roomName(dto.getRoomName())
                .roomType(dto.getRoomType())
                .capacity(dto.getCapacity())
                .build();
    }
}
