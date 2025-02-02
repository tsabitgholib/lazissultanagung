package com.lazis.lazissultanagung.controller;

import com.lazis.lazissultanagung.dto.response.ResponseMessage;
import com.lazis.lazissultanagung.model.Infak;
import com.lazis.lazissultanagung.service.InfakService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@CrossOrigin(origins= {"*"}, maxAge = 4800, allowCredentials = "false" )
@RestController
@RequestMapping("api/infak")
public class InfakController {

    @Autowired
    private InfakService infakService;

    @GetMapping
    public ResponseEntity<List<Infak>> getAllInfak(){
        List<Infak> infakList = infakService.getAllInfak();
        return ResponseEntity.ok(infakList);
    }

    @PostMapping("/create")
    public ResponseEntity<Infak> createInfak(@RequestBody Infak infak) {
        Infak createInfak = infakService.createInfak(infak);
        return ResponseEntity.ok(createInfak);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<Infak> updateInfak(@PathVariable Long id, @RequestBody Infak infak) {
        Infak updateInfak = infakService.updateInfak(id, infak);
        return ResponseEntity.ok(updateInfak);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Infak> getInfakById(@PathVariable Long id){
        Optional<Infak> infakOptional = infakService.getInfakById(id);
        if (infakOptional.isPresent()){
            return new ResponseEntity<>(infakOptional.get(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/delete/{id}")
    public ResponseMessage deleteInfak(@PathVariable Long id){
        return infakService.deleteInfak(id);
    }
}
