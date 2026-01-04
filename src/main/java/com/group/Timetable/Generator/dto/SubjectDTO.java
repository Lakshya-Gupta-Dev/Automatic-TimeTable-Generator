package com.group.Timetable.Generator.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SubjectDTO {
    private Long subId;
    private String subName;
    private int semester;
    private String subType;
    private int subCredit;  
    private String branch;  
    private String courseName;   
    private String username;
}
