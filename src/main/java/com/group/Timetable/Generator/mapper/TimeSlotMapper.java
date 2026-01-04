package com.group.Timetable.Generator.mapper;

import com.group.Timetable.Generator.dto.TimeSlotDTO;
import com.group.Timetable.Generator.entities.TimeSlot;
import com.group.Timetable.Generator.entities.User;

public class TimeSlotMapper {

    public static TimeSlotDTO toDTO(TimeSlot e) {
        if (e == null) return null;

        return TimeSlotDTO.builder()
                .slotId(e.getSlotId())
                .day(e.getDay())
                .startTime(e.getStartTime())
                .endTime(e.getEndTime())
                .slotNumber(e.getSlotNumber())
                .userId(e.getUser() != null ? e.getUser().getId() : null)
                .build();
    }

    public static TimeSlot toEntity(TimeSlotDTO dto, User user) {
        if (dto == null) return null;

        return TimeSlot.builder()
                .slotId(dto.getSlotId())
                .day(dto.getDay())
                .startTime(dto.getStartTime())
                .endTime(dto.getEndTime())
                .slotNumber(dto.getSlotNumber())
                .user(user)
                .build();
    }

    public static void update(TimeSlotDTO dto, TimeSlot entity, User user) {
        entity.setDay(dto.getDay());
        entity.setStartTime(dto.getStartTime());
        entity.setEndTime(dto.getEndTime());
        entity.setSlotNumber(dto.getSlotNumber());
        entity.setUser(user);
    }
}

