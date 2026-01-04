package com.group.Timetable.Generator.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RoomDTO {

    private Long roomId;
    private String roomName;
    private String roomType;
    private int capacity;
}

