package com.accenture.hr.controller;

import com.accenture.hr.Statuses;
import com.accenture.hr.responses.EntryResponse;
import com.accenture.hr.responses.ExitResponse;
import com.accenture.hr.responses.ResgisterResponse;
import com.accenture.hr.responses.StatusResponse;
import com.accenture.hr.slots.SlotService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/v1/slots")
public class SlotController {

    private final SlotService slotService;

    @Autowired
    public SlotController(SlotService slotService) {
        this.slotService = slotService;
    }

    @PostMapping("/register")
    public ResponseEntity<ResgisterResponse> register(@RequestParam Long userId) {
        ResgisterResponse resgisterResponse = slotService.register(userId);
        Statuses status = resgisterResponse.getStatus();
        return getResgisterResponseResponseEntity(resgisterResponse, status);
    }

    @GetMapping("/status")
    public ResponseEntity<StatusResponse> status(@RequestParam Long userId){
        StatusResponse statusResponse = slotService.status(userId);
        Statuses status = statusResponse.getStatus();
        return getStatusResponseResponseEntity(statusResponse, status);
    }

    @PutMapping("/entry")
    public ResponseEntity<EntryResponse> entry(@RequestParam Long userId){
        EntryResponse entryResponse = slotService.entry(userId);
        Statuses status = entryResponse.getStatus();
        return getEntryResponseResponseEntity(entryResponse, status);
    }

    @PutMapping("/exit")
    public ResponseEntity<ExitResponse> exit(@RequestParam Long userId){
        ExitResponse exitResponse = slotService.exit(userId);
        Statuses status = exitResponse.getStatus();
        return status.equals(Statuses.SUCCESS) ?
                new ResponseEntity<>(exitResponse,HttpStatus.OK) :
                new ResponseEntity<>(exitResponse,HttpStatus.BAD_REQUEST);
    }

    private ResponseEntity<ResgisterResponse> getResgisterResponseResponseEntity(ResgisterResponse resgisterResponse, Statuses status) {
        if (status.equals(Statuses.ALREADY_IN_BUILDING)){
            return new ResponseEntity<>(resgisterResponse, HttpStatus.FORBIDDEN);
        }else if (status.equals(Statuses.ALREADY_ON_WAITINGLIST)){
            return new ResponseEntity<>(resgisterResponse,HttpStatus.FORBIDDEN);
        }else if (status.equals(Statuses.CAN_ENTER)){
            return new ResponseEntity<>(resgisterResponse,HttpStatus.OK);
        }else{
            return new ResponseEntity<>(resgisterResponse,HttpStatus.OK);
        }
    }

    private ResponseEntity<StatusResponse> getStatusResponseResponseEntity(StatusResponse statusResponse, Statuses status) {
        if (status.equals(Statuses.CAN_ENTER)){
            return  new ResponseEntity<>(statusResponse, HttpStatus.OK);
        }else if (status.equals((Statuses.ALREADY_IN_BUILDING))){
            return  new ResponseEntity<>(statusResponse,HttpStatus.FORBIDDEN);
        }else if (status.equals((Statuses.POSITION_IN_LINE))){
            return  new ResponseEntity<>(statusResponse,HttpStatus.OK);
        }else{
            return  new ResponseEntity<>(statusResponse,HttpStatus.NOT_FOUND);
        }
    }

    private ResponseEntity<EntryResponse> getEntryResponseResponseEntity(EntryResponse entryResponse, Statuses status) {
        if (status.equals(Statuses.SUCCESS)){
            return new ResponseEntity<>(entryResponse, HttpStatus.OK);
        }else if (status.equals(Statuses.ALREADY_IN_BUILDING)){
            return new ResponseEntity<>(entryResponse,HttpStatus.FORBIDDEN);
        }else if (status.equals(Statuses.WAIT_MORE)){
            return new ResponseEntity<>(entryResponse,HttpStatus.BAD_REQUEST);
        }else{
            return new ResponseEntity<>(entryResponse,HttpStatus.NOT_FOUND);
        }
    }

}
