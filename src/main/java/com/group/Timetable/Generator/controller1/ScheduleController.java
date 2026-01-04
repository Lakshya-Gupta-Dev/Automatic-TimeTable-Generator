package com.group.Timetable.Generator.controller1;

import com.group.Timetable.Generator.dto.ScheduleDTO;
import com.group.Timetable.Generator.ga.GAChromosome;
import com.group.Timetable.Generator.ga.GAEngine;
import com.group.Timetable.Generator.service.ExportService;
import com.group.Timetable.Generator.service.GAInputService;
import com.group.Timetable.Generator.service.ScheduleService;
import com.group.Timetable.Generator.entities.Schedule;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/user/schedule")
@CrossOrigin(origins = "*")
public class ScheduleController {

    private final GAInputService inputBuilder;
    private final ScheduleService scheduleService;
    private final ExportService exportService;

    public ScheduleController(GAInputService inputBuilder,
                              ScheduleService scheduleService,
                              ExportService exportService) {
        this.inputBuilder = inputBuilder;
        this.scheduleService = scheduleService;
        this.exportService = exportService;
    }

    @PostMapping("/generate")
    public ResponseEntity<?> generate() {

        var input = inputBuilder.buildInput();
        GAEngine engine = new GAEngine(input, 60, 150, 0.03);
        GAChromosome best = engine.run();

        scheduleService.saveGeneratedSchedule(best);

        List<Schedule> schedules = scheduleService.getUserSchedule();

        List<ScheduleDTO> dtoList = schedules.stream().map(s -> {
            ScheduleDTO dto = new ScheduleDTO();
            dto.setInstituteName(s.getUser().getInstituteName());
            dto.setDepartmentName(s.getSection().getCourse().getDepartment().getDeptName());
            dto.setCourseName(s.getSection().getCourse().getCourseName());
            dto.setBranch(s.getSection().getBranch());
            dto.setSemester(s.getSection().getSemester());
            dto.setSection(s.getSection().getSectionName());
            dto.setSubjectName(s.getSubject().getSubName());
            dto.setTeacherName(s.getTeacher().getTeacherName());
            dto.setRoomNumber(s.getRoom().getRoomName());
            dto.setDayOfWeek(s.getTimeSlot().getDay());
            dto.setStartTime(s.getTimeSlot().getStartTime().toString());
            dto.setEndTime(s.getTimeSlot().getEndTime().toString());
            dto.setCredit(s.getSubject().getSubCredit());
            return dto;
        }).collect(Collectors.toList());

        return ResponseEntity.ok(Map.of(
                "message", "Timetable generated successfully!",
                "schedules", dtoList
        ));
    }

    @GetMapping
    public ResponseEntity<?> getSchedule() {
        List<Schedule> schedules = scheduleService.getUserSchedule();

        List<ScheduleDTO> dtoList = schedules.stream().map(s -> {
            ScheduleDTO dto = new ScheduleDTO();
            dto.setInstituteName(s.getUser().getInstituteName());
            dto.setDepartmentName(s.getSection().getCourse().getDepartment().getDeptName());
            dto.setCourseName(s.getSection().getCourse().getCourseName());
            dto.setBranch(s.getSection().getBranch());
            dto.setSemester(s.getSection().getSemester());
            dto.setSection(s.getSection().getSectionName());
            dto.setSubjectName(s.getSubject().getSubName());
            dto.setTeacherName(s.getTeacher().getTeacherName());
            dto.setRoomNumber(s.getRoom().getRoomName());
            dto.setDayOfWeek(s.getTimeSlot().getDay());
            dto.setStartTime(s.getTimeSlot().getStartTime().toString());
            dto.setEndTime(s.getTimeSlot().getEndTime().toString());
            dto.setCredit(s.getSubject().getSubCredit());
            return dto;
        }).collect(Collectors.toList());

        return ResponseEntity.ok(dtoList);
    }

    @GetMapping("/export/pdf")
    public ResponseEntity<byte[]> exportPdf(org.springframework.security.core.Authentication auth) throws Exception {
        return exportService.exportTimetable("pdf", auth);
    }
    
    
    @GetMapping("/all")
    public ResponseEntity<?> getAllSchedules() {
        List<Schedule> schedules = scheduleService.getUserSchedule();
        List<ScheduleDTO> dtoList = schedules.stream().map(s -> {
            ScheduleDTO dto = new ScheduleDTO();
            dto.setInstituteName(s.getUser().getInstituteName());
            dto.setDepartmentName(s.getSection().getCourse().getDepartment().getDeptName());
            dto.setCourseName(s.getSection().getCourse().getCourseName());
            dto.setSemester(s.getSection().getSemester());
            dto.setSection(s.getSection().getSectionName());
            dto.setSubjectName(s.getSubject().getSubName());
            dto.setTeacherName(s.getTeacher().getTeacherName());
            dto.setRoomNumber(s.getRoom().getRoomName());
            dto.setDayOfWeek(s.getTimeSlot().getDay());
            dto.setStartTime(s.getTimeSlot().getStartTime().toString());
            dto.setEndTime(s.getTimeSlot().getEndTime().toString());
            dto.setCredit(s.getSubject().getSubCredit());
            return dto;
        }).collect(Collectors.toList());

        return ResponseEntity.ok(dtoList);
    }
    
    
    @GetMapping("/search/{text}")
    public ResponseEntity<?> searchUserSchedules(@PathVariable String text) {

        List<Schedule> schedules = scheduleService.searchSchedules(text);

        List<ScheduleDTO> dtoList = schedules.stream().map(s -> {
            ScheduleDTO dto = new ScheduleDTO();
            dto.setInstituteName(s.getUser().getInstituteName());
            dto.setCourseName(s.getSection().getCourse().getCourseName());
            dto.setBranch(s.getSection().getBranch());
            dto.setSemester(s.getSection().getSemester());
            dto.setSection(s.getSection().getSectionName());
            dto.setSubjectName(s.getSubject().getSubName());
            dto.setTeacherName(s.getTeacher().getTeacherName());
            dto.setRoomNumber(s.getRoom().getRoomName());
            dto.setDayOfWeek(s.getTimeSlot().getDay());
            dto.setStartTime(s.getTimeSlot().getStartTime().toString());
            dto.setEndTime(s.getTimeSlot().getEndTime().toString());
            return dto;
        }).collect(Collectors.toList());

        return ResponseEntity.ok(dtoList);
    }

}
