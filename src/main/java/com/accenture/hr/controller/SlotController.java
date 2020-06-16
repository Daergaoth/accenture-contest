package com.accenture.hr.controller;

import com.accenture.hr.slots.SlotService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/v1/slots")
public class SlotController {

    private SlotService slotService;

    @Autowired
    public SlotController(SlotService slotService) {
        this.slotService = slotService;
    }

    @PostMapping("/register")
    public ResponseEntity<Void> register(@RequestParam Long userId) {
        slotService.register(userId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/status")
    public ResponseEntity<Void> status(@RequestParam Long userId){
        slotService.status(userId);
        return null;
    }

}
