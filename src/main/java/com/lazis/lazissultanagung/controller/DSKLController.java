package com.lazis.lazissultanagung.controller;

import com.lazis.lazissultanagung.dto.response.ResponseMessage;
import com.lazis.lazissultanagung.model.DSKL;
import com.lazis.lazissultanagung.service.DSKLService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@CrossOrigin(origins= {"*"}, maxAge = 4800, allowCredentials = "false" )
@RestController
@RequestMapping("api/dskl")
public class DSKLController {

    @Autowired
    private DSKLService dsklService;

    @GetMapping
    public ResponseEntity<List<DSKL>> getAllDSKL(){
        List<DSKL> dsklList = dsklService.getAllDSKL();
        return ResponseEntity.ok(dsklList);
    }

    @PostMapping("/create")
    public ResponseEntity<DSKL> createDSKL(@RequestBody DSKL dskl) {
        DSKL createDSKL = dsklService.createDSKL(dskl);
        return ResponseEntity.ok(dskl);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<DSKL> updateDSKL(@PathVariable Long id, @RequestBody DSKL dskl){
        DSKL updateDSKL = dsklService.updateDSKL(id, dskl);
        return ResponseEntity.ok(updateDSKL);
    }

    @GetMapping("/{id}")
    public ResponseEntity<DSKL> getDSKLById(@PathVariable Long id){
        Optional<DSKL> dsklOptional = dsklService.getDSKLById(id);
        if (dsklOptional.isPresent()){
            return new ResponseEntity<>(dsklOptional.get(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/delete/{id}")
    public ResponseMessage deleteDSKL(@PathVariable Long id){
        return dsklService.deleteDSKL(id);
    }

}
