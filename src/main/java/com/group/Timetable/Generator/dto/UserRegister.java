package com.group.Timetable.Generator.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
@Data
@AllArgsConstructor
@Builder
public class UserRegister {
    private String username;
    private String instituteName;
    private Long mobile;
    private String confirmPassword;
    private String email;
    private String password;
    public UserRegister(){}

}
