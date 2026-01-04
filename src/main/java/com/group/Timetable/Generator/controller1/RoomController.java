package com.group.Timetable.Generator.controller1;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import com.group.Timetable.Generator.Repository.UserRepository;
import com.group.Timetable.Generator.dto.RoomDTO;
import com.group.Timetable.Generator.entities.User;
import com.group.Timetable.Generator.service.RoomService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/user/rooms")
@RequiredArgsConstructor
public class RoomController {

    private final RoomService service;
    private final UserRepository userRepo;

    private User getCurrentUser(Authentication auth) {
        Object p = auth.getPrincipal();
        String username = (p instanceof UserDetails)
                ? ((UserDetails) p).getUsername()
                : p.toString();

        return userRepo.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    @PostMapping("/save")
    public RoomDTO saveRoom(@RequestBody RoomDTO dto, Authentication auth) {
        User user = getCurrentUser(auth);
        return service.saveRoom(dto, user.getId());
    }

    @GetMapping
    public List<RoomDTO> getAllRooms(Authentication auth) {
        User user = getCurrentUser(auth);
        return service.getAllRooms(user.getId());
    }

    @GetMapping("/{id}")
    public RoomDTO getRoom(@PathVariable Long id, Authentication auth) {
        User user = getCurrentUser(auth);
        return service.getRoom(id, user.getId());
    }

    @GetMapping("/search/{name}")
    public List<RoomDTO> searchRooms(@PathVariable String name, Authentication auth) {
        User user = getCurrentUser(auth);
        return service.searchRooms(name, user.getId());
    }

    @PutMapping("/update/{id}")
    public RoomDTO updateRoom(@PathVariable Long id, @RequestBody RoomDTO dto, Authentication auth) {
        User user = getCurrentUser(auth);
        return service.updateRoom(id, dto, user.getId());
    }

    @DeleteMapping("/{id}")
    public void deleteRoom(@PathVariable Long id, Authentication auth) {
        User user = getCurrentUser(auth);
        service.deleteRoom(id, user.getId());
    }
}
