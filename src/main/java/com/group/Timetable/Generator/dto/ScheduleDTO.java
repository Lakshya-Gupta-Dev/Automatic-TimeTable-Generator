package com.group.Timetable.Generator.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ScheduleDTO {

    private String instituteName;
    private String departmentName;
    private String courseName;
    private String branch; 
    private Integer semester;
    private String section;
    private String subjectName;
    private String teacherName;
    private String roomNumber;
    private String dayOfWeek;
    private String startTime;
    private String endTime;
    private Integer credit;
}
