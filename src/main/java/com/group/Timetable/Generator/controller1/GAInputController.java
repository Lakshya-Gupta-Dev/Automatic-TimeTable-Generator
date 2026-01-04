package com.group.Timetable.Generator.controller1;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.group.Timetable.Generator.dto.GAInputDTO;
import com.group.Timetable.Generator.service.GAInputService;

@RestController
@RequestMapping("/api/user/ga")
@CrossOrigin(origins = "*")
public class GAInputController {

    private final GAInputService gaInputService;

    public GAInputController(GAInputService gaInputService) {
        this.gaInputService = gaInputService;
    }


    @GetMapping("/input")
    public ResponseEntity<GAInputDTO> getGAInput() {
        GAInputDTO dto = gaInputService.buildInput();
        return ResponseEntity.ok(dto);
    }
}
