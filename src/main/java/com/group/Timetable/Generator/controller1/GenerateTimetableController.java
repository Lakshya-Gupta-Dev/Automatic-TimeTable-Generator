package com.group.Timetable.Generator.controller1;

import com.group.Timetable.Generator.service.GATimetableService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/user/ga")
@CrossOrigin(origins = "*")
public class GenerateTimetableController {

    private final GATimetableService gaTimetableService;

    public GenerateTimetableController(GATimetableService gaTimetableService) {
        this.gaTimetableService = gaTimetableService;
    }

    @PostMapping("/generate")
    public ResponseEntity<Map<String, Object>> generate(@RequestBody(required = false) Map<String, Object> opts) {
        int population = opts != null && opts.get("population") instanceof Number ? ((Number)opts.get("population")).intValue() : 60;
        int generations = opts != null && opts.get("generations") instanceof Number ? ((Number)opts.get("generations")).intValue() : 150;
        double mutationRate = opts != null && opts.get("mutationRate") instanceof Number ? ((Number)opts.get("mutationRate")).doubleValue() : 0.03;

        Map<String, Object> res = gaTimetableService.generateAndSave(population, generations, mutationRate);
        return ResponseEntity.ok(res);
    }
}
