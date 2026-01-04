package com.group.Timetable.Generator.controller1;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import com.group.Timetable.Generator.Repository.UserRepository;
import com.group.Timetable.Generator.dto.TimeSlotDTO;
import com.group.Timetable.Generator.entities.TimeSlot;
import com.group.Timetable.Generator.entities.User;
import com.group.Timetable.Generator.mapper.TimeSlotMapper;
import com.group.Timetable.Generator.service.TimeSlotService;

@RestController
@RequestMapping("/api/timeslot")
public class TimeSlotController {

    private final TimeSlotService service;
    private final UserRepository userRepo;

    public TimeSlotController(TimeSlotService service, UserRepository userRepo) {
        this.service = service;
        this.userRepo = userRepo;
    }

    private User getCurrentUser(Authentication auth) {
        Object p = auth.getPrincipal();
        String username = (p instanceof UserDetails) ? ((UserDetails)p).getUsername() : p.toString();
        return userRepo.findByUsername(username).orElse(null);
    }

    @PostMapping("/add")
    public ResponseEntity<TimeSlotDTO> create(@RequestBody TimeSlotDTO dto, Authentication auth) {
        User user = getCurrentUser(auth);
        TimeSlot entity = TimeSlotMapper.toEntity(dto, user);
        TimeSlot saved = service.create(entity, user);
        return ResponseEntity.ok(TimeSlotMapper.toDTO(saved));
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<TimeSlotDTO> update(@PathVariable Long id, @RequestBody TimeSlotDTO dto, Authentication auth) {
        User user = getCurrentUser(auth);
        TimeSlot entity = TimeSlotMapper.toEntity(dto, user);
        TimeSlot updated = service.update(id, entity, user);
        return ResponseEntity.ok(TimeSlotMapper.toDTO(updated));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> delete(@PathVariable Long id, Authentication auth) {
        User user = getCurrentUser(auth);
        service.delete(id, user);
        return ResponseEntity.ok("Deleted");
    }

    @GetMapping("/get/{id}")
    public ResponseEntity<TimeSlotDTO> get(@PathVariable Long id, Authentication auth) {
        User user = getCurrentUser(auth);
        TimeSlot ts = service.getById(id, user);
        return ResponseEntity.ok(TimeSlotMapper.toDTO(ts));
    }

    @GetMapping("/all")
    public ResponseEntity<List<TimeSlotDTO>> list(Authentication auth) {
        User user = getCurrentUser(auth);

        List<TimeSlotDTO> list = service.list(user).stream()
                .map(TimeSlotMapper::toDTO)
                .collect(Collectors.toList());

        return ResponseEntity.ok(list);
    }
}
