package com.lazis.lazissultanagung.controller;

import com.lazis.lazissultanagung.model.Coa;
import com.lazis.lazissultanagung.model.SaldoAkhir;
import com.lazis.lazissultanagung.model.SaldoAwal;
import com.lazis.lazissultanagung.repository.CoaRepository;
import com.lazis.lazissultanagung.repository.SaldoAkhirRepository;
import com.lazis.lazissultanagung.repository.SaldoAwalRepository;
import com.lazis.lazissultanagung.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@CrossOrigin(origins = { "*" }, maxAge = 4800, allowCredentials = "false")
@RestController
@RequestMapping("/api/journal")
public class LaporanAktivitasPengelolaController {

    @Autowired
    private CoaRepository coaRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private SaldoAwalRepository saldoAwalRepository;

    @Autowired
    private SaldoAkhirRepository saldoAkhirRepository;

    @GetMapping("/pengelola-activity-report")
    public ResponseEntity<Map<String, Object>> getPengelolaActivityReport(
            @RequestParam("month1") int month1,
            @RequestParam("year1") int year1,
            @RequestParam("month2") int month2,
            @RequestParam("year2") int year2) {
        String bulan1 = String.format("%d-%02d", year1, month1);
        String bulan2 = String.format("%d-%02d", year2, month2);

        List<Object[]> results = transactionRepository.getPengelolaActivityReportNative(bulan1, bulan2);

        Map<String, Object> response = new LinkedHashMap<>();
        Map<String, Object> penerimaanDana = new LinkedHashMap<>();
        Map<String, Object> pendayagunaanDana = new LinkedHashMap<>();
        Map<String, Double> danaAmilMap = new LinkedHashMap<>();
        Map<String, Double> bagiHasilMap = new LinkedHashMap<>();

        String month1Name = Month.of(month1).name() + " " + year1;
        String month2Name = Month.of(month2).name() + " " + year2;

        for (Object[] row : results) {
            String uraian = (String) row[0];
            double val1 = row[1] != null ? ((Number) row[1]).doubleValue() : 0.0;
            double val2 = row[2] != null ? ((Number) row[2]).doubleValue() : 0.0;

            if (uraian.equals("Penerimaan Dana Amil Zakat") || uraian.equals("Penerimaan Dana Amil (Infak, DSKL, Campaign)")) {
                danaAmilMap.put(month1Name, danaAmilMap.getOrDefault(month1Name, 0.0) + val1);
                danaAmilMap.put(month2Name, danaAmilMap.getOrDefault(month2Name, 0.0) + val2);
            } else if (uraian.equals("Penerimaan Bagi Hasil Bank")) {
                bagiHasilMap.put(month1Name, val1);
                bagiHasilMap.put(month2Name, val2);
            } else if (uraian.equals("Jumlah Penerimaan Dana Pengelola")) {
                penerimaanDana.put("Total Bulan " + month1Name, val1);
                penerimaanDana.put("Total Bulan " + month2Name, val2);
            } else if (uraian.equals("Jumlah Pendayagunaan Dana Pengelola")) {
                pendayagunaanDana.put("Total Bulan " + month1Name, val1);
                pendayagunaanDana.put("Total Bulan " + month2Name, val2);
            } else if (uraian.equals("Surplus (Defisit) Dana Pengelola")) {
                response.put("Surplus (Defisit) Dana " + month1Name, val1);
                response.put("Surplus (Defisit) Dana " + month2Name, val2);
            } else if (uraian.equals("Saldo Awal Dana Pengelola")) {
                response.put("Saldo Awal Dana " + month1Name, val1);
                response.put("Saldo Awal Dana " + month2Name, val2);
            } else if (uraian.equals("Saldo Akhir Dana Pengelola")) {
                response.put("Saldo Akhir Dana " + month1Name, val1);
                response.put("Saldo Akhir Dana " + month2Name, val2);

                // Save Saldo Akhir to database
                // saveSaldoAkhir(50L, month1, year1, val1);
                // saveSaldoAkhir(50L, month2, year2, val2);
            } else if (!uraian.equals("KOREKSI DANA PENGELOLA")) {
                // Ini adalah akun beban di bawah PENDAYAGUNAAN
                Map<String, Double> breakdown = new LinkedHashMap<>();
                breakdown.put(month1Name, val1);
                breakdown.put(month2Name, val2);
                pendayagunaanDana.put(uraian, breakdown);
            }
        }

        penerimaanDana.put("Penerimaan Dana Amil", danaAmilMap);
        penerimaanDana.put("Penerimaan Bagi Hasil Bank", bagiHasilMap);

        Map<String, Object> finalResponse = new LinkedHashMap<>();
        finalResponse.put("Penerimaan Dana Pengelola", penerimaanDana);
        finalResponse.put("Pendayagunaan Pengelola", pendayagunaanDana);
        finalResponse.put("Surplus (Defisit) Dana " + month1Name, response.get("Surplus (Defisit) Dana " + month1Name));
        finalResponse.put("Surplus (Defisit) Dana " + month2Name, response.get("Surplus (Defisit) Dana " + month2Name));
        finalResponse.put("Saldo Awal Dana " + month1Name, response.get("Saldo Awal Dana " + month1Name));
        finalResponse.put("Saldo Akhir Dana " + month1Name, response.get("Saldo Akhir Dana " + month1Name));
        finalResponse.put("Saldo Awal Dana " + month2Name, response.get("Saldo Awal Dana " + month2Name));
        finalResponse.put("Saldo Akhir Dana " + month2Name, response.get("Saldo Akhir Dana " + month2Name));

        return ResponseEntity.ok(finalResponse);
    }

    private void saveSaldoAkhir(Long coaId, int month, int year, double saldoAkhir) {
        Optional<SaldoAkhir> existingSaldoAkhir = saldoAkhirRepository.findByCoa_IdAndMonthAndYear(coaId, month, year);
        SaldoAkhir saldoAkhirEntity = existingSaldoAkhir.orElse(new SaldoAkhir());
        saldoAkhirEntity.setCoa(coaRepository.findById(coaId).orElse(null));
        saldoAkhirEntity.setMonth(month);
        saldoAkhirEntity.setYear(year);
        saldoAkhirEntity.setSaldoAkhir(saldoAkhir);
        saldoAkhirRepository.save(saldoAkhirEntity);
    }
}
