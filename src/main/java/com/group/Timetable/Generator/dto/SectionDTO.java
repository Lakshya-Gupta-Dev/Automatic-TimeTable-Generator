package com.group.Timetable.Generator.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SectionDTO {

    private Long sectionId;
    private Integer semester;
    private String sectionName;
    private String branch;

    private Long courseId;
    private String courseName;
}

