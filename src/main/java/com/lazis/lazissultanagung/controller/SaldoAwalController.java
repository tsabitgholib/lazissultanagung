package com.lazis.lazissultanagung.controller;

import com.lazis.lazissultanagung.dto.request.SaldoAwalRequest;
import com.lazis.lazissultanagung.dto.response.CoaSaldoResponse;
import com.lazis.lazissultanagung.service.SaldoAwalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@CrossOrigin(origins= {"*"}, maxAge = 4800, allowCredentials = "false" )
@RestController
@RequestMapping("/api/saldo-awal")
public class SaldoAwalController {

    @Autowired
    private SaldoAwalService saldoAwalService;

    @PostMapping("/input")
    public ResponseEntity<?> createBatchSaldoAwal(@RequestBody List<SaldoAwalRequest> requests) {
        List<Map<String, Object>> response = saldoAwalService.inputBatchSaldoAwal(requests);

        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
                "message", "Saldo awal berhasil diproses.",
                "details", response
        ));
    }

    @GetMapping("/get-saldo-coa")
    public ResponseEntity<List<CoaSaldoResponse>> getAllCoaWithSaldoAwal() {
        List<CoaSaldoResponse> responses = saldoAwalService.getAllCoaWithSaldoAwal();
        return ResponseEntity.ok(responses);
    }
}
