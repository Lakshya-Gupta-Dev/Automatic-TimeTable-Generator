package com.group.Timetable.Generator.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.group.Timetable.Generator.dto.GAInputDTO;
import com.group.Timetable.Generator.ga.GAChromosome;
import com.group.Timetable.Generator.ga.GAEngine;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class GATimetableService {

    private final GAInputService gaInputService;
    private final ScheduleService scheduleService;

    public GATimetableService(GAInputService gaInputService,
                              ScheduleService scheduleService) {
        this.gaInputService = gaInputService;
        this.scheduleService = scheduleService;
    }

    public Map<String, Object> generateAndSave(int populationSize, int generations, double mutationRate) {
        GAInputDTO input = gaInputService.buildInput();

        GAEngine engine = new GAEngine(input, populationSize, generations, mutationRate);
        GAChromosome best = engine.run();

        List<Map<String, Object>> generated = best.getGenes().stream().map(g -> {
            Map<String, Object> m = new HashMap<>();
            m.put("sectionId", g.getSectionId());
            m.put("subjectId", g.getSubjectId());
            m.put("teacherId", g.getTeacherId());
            m.put("roomId", g.getRoomId());
            m.put("timeSlotId", g.getTimeSlotId());
            return m;
        }).collect(Collectors.toList());

        // Save schedules
        scheduleService.saveGenerated(generated);

        Map<String, Object> result = new HashMap<>();
        result.put("generatedCount", generated.size());
        result.put("fitness", best.getFitness());
        result.put("sample", generated.size() > 0 ? generated.get(0) : Collections.emptyMap());
        return result;
    }
}

