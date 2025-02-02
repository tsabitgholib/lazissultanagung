package com.lazis.lazissultanagung.controller;

import com.lazis.lazissultanagung.dto.response.ResponseMessage;
import com.lazis.lazissultanagung.model.Wakaf;
import com.lazis.lazissultanagung.service.WakafService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@CrossOrigin(origins= {"*"}, maxAge = 4800, allowCredentials = "false" )
@RestController
@RequestMapping("api/wakaf")
public class WakafController {

    @Autowired
    private WakafService wakafService;

    @GetMapping
    public ResponseEntity<List<Wakaf>> getAllWakaf(){
        List<Wakaf> wakafList = wakafService.getAllWakaf();
        return ResponseEntity.ok(wakafList);
    }

    @PostMapping("/create")
    public ResponseEntity<Wakaf> createWakaf(@RequestBody Wakaf wakaf) {
        Wakaf createWakaf = wakafService.createWakaf(wakaf);
        return ResponseEntity.ok(createWakaf);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<Wakaf> updateInfak(@PathVariable Long id, @RequestBody Wakaf wakaf) {
        Wakaf updateWakaf = wakafService.updateWakaf(id, wakaf);
        return ResponseEntity.ok(updateWakaf);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Wakaf> getWakafById(@PathVariable Long id){
        Optional<Wakaf> wakafOptional = wakafService.getWakafById(id);
        if (wakafOptional.isPresent()){
            return new ResponseEntity<>(wakafOptional.get(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/delete/{id}")
    public ResponseMessage deleteWakaf(@PathVariable Long id){
        return wakafService.deleteWakaf(id);
    }
}
