package com.group.Timetable.Generator.controller1;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.group.Timetable.Generator.Repository.UserRepository;
import com.group.Timetable.Generator.Repository.ScheduleRepository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = "*")
public class AdminStatsController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ScheduleRepository scheduleRepository;

    @GetMapping("/stats")
    public ResponseEntity<?> getStats() {

        Map<String, Long> stats = new HashMap<>();

       
        stats.put("totalUsers", userRepository.count());

        
        stats.put("totalTimetable", scheduleRepository.countDistinctByGenerationId());

       
        stats.put("totalPayment", 0L);

        return ResponseEntity.ok(stats);
    }
    

    // FINAL API: GET TIMETABLES PER USER
    @GetMapping("/timetables/details")
    public ResponseEntity<?> getTimetableDetails() {

        List<Object[]> rows = scheduleRepository.getTimetableCountsByUsers();

        List<Map<String, Object>> result = new ArrayList<>();

        for(Object[] r : rows){
            Map<String,Object> m = new HashMap<>();
            m.put("userId", r[0]);
            m.put("username", r[1]);
            m.put("instituteName", r[2]);
            m.put("totalGenerated", r[3]);
            result.add(m);
        }

        return ResponseEntity.ok(result);
    }
}
