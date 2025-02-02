package com.lazis.lazissultanagung.controller;

import com.lazis.lazissultanagung.dto.request.EditProfileDonaturRequest;
import com.lazis.lazissultanagung.dto.response.ResponseMessage;
import com.lazis.lazissultanagung.model.Admin;
import com.lazis.lazissultanagung.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@CrossOrigin(origins= {"*"}, maxAge = 4800, allowCredentials = "false" )
@RestController
@RequestMapping("api/admin")
public class AdminController {

    @Autowired
    private AdminService adminService;

    @GetMapping("/profile")
    public ResponseEntity<?> getCurrentUser(){
        Admin currentAdmin = adminService.getCurrentAdmin();
        return ResponseEntity.ok(currentAdmin);
    }

    @GetMapping("/get-all-operator")
    public Page<Map<String, Object>> getAllOperator(@RequestParam(name = "page", defaultValue = "0") int page) {
        int pageSize = 12;
        PageRequest pageRequest = PageRequest.of(page, pageSize);
        return adminService.getAllOperator(pageRequest);
    }

    @PostMapping("/edit")
    public ResponseEntity<?> editUserProfile(@ModelAttribute EditProfileDonaturRequest editProfileRequest) {
        Admin updateAdmin = adminService.editProfileAdmin(editProfileRequest);
        return ResponseEntity.ok(updateAdmin);
    }

    @PutMapping("/nonaktive-Operator/{id}")
    public ResponseMessage nonaktiveOperator(@PathVariable Long id){
        return adminService.nonaktiveOperator(id);
    }

    @PutMapping("/aktive-Operator/{id}")
    public ResponseMessage aktiveOperator(@PathVariable Long id){
        return adminService.aktiveOperator(id);
    }

    @GetMapping("/get-active-operator")
    public Page<Map<String, Object>> getActiveOperator(@RequestParam(name = "page", defaultValue = "0") int page) {
        int pageSize = 12;
        PageRequest pageRequest = PageRequest.of(page, pageSize);
        return adminService.getActiveOperator(pageRequest);
    }

    @GetMapping("/search-operator")
    public ResponseEntity<Page<Admin>> searchOperators(
            @RequestParam String search,
            @RequestParam(name = "page", defaultValue = "0") int page) {
        int pageSize = 12;
        PageRequest pageRequest = PageRequest.of(page, pageSize);
        Page<Admin> result = adminService.searchOperators(search, pageRequest);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/get-all-keuangan")
    public Page<Map<String, Object>> getAllKeuangan(@RequestParam(name = "page", defaultValue = "0") int page) {
        int pageSize = 12;
        PageRequest pageRequest = PageRequest.of(page, pageSize);
        return adminService.getAllKeuangan(pageRequest);
    }
}
