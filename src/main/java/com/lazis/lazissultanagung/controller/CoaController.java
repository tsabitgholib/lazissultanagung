package com.lazis.lazissultanagung.controller;

import com.lazis.lazissultanagung.dto.response.ResponseMessage;
import com.lazis.lazissultanagung.model.Coa;
import com.lazis.lazissultanagung.service.CoaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@CrossOrigin(origins= {"*"}, maxAge = 4800, allowCredentials = "false" )
@RestController
@RequestMapping("api/coa")
public class CoaController {

    @Autowired
    public CoaService coaService;

    @GetMapping
    public List<Coa> getAllCoa(){
        return coaService.getAllCoa();
    }

    @GetMapping("/all")
    public List<Coa> getAllCoas(){
        return coaService.getAllCoas();
    }

    @GetMapping("/parent")
    public List<Coa> getAllParentCoa(){
        return coaService.getAllParentCoa();
    }

    @GetMapping("/list")
    public ResponseEntity<List<Coa>> getCoaByAccountType(@RequestParam("accountType") String accountType) {
        try {
            List<Coa> coaList = coaService.getCoaByAccountType(accountType);
            return new ResponseEntity<>(coaList, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Coa> getCoaById(@PathVariable Long id){
        Coa existingCoa = coaService.getCoaById(id);

        return ResponseEntity.ok(existingCoa);
    }

    @PostMapping("/create")
    public ResponseEntity<Coa> createCoa(@RequestBody Coa coa){
        Coa createdCoa = coaService.createCoa(coa);

        return ResponseEntity.ok(createdCoa);
    }

    @PutMapping("/edit/{id}")
    public ResponseEntity<Coa> editCoa(@PathVariable Long id, @RequestBody Coa coa){
        Coa editCoa = coaService.editCoa(id, coa);
        return ResponseEntity.ok(editCoa);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseMessage deleteCoa(@PathVariable Long id){
        return coaService.deleteCoa(id);
    }

    @GetMapping("/pengelola")
    public ResponseEntity<List<Map<String, Object>>> getCategories() {
        List<Map<String, Object>> categories = coaService.getCoaPengelola();
        return ResponseEntity.ok(categories);
    }
}
