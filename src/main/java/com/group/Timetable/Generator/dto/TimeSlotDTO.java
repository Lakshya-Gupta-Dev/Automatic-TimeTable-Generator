package com.group.Timetable.Generator.dto;

import lombok.*;
import java.time.LocalTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TimeSlotDTO {

    private Long slotId;
    private String day;
    private LocalTime startTime;
    private LocalTime endTime;
    private Integer slotNumber;

    private Long userId; 
}

