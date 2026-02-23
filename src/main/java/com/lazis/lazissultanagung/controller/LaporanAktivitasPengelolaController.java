package com.lazis.lazissultanagung.controller;

import com.lazis.lazissultanagung.model.Coa;
import com.lazis.lazissultanagung.repository.CoaRepository;
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

@CrossOrigin(origins = {"*"}, maxAge = 4800, allowCredentials = "false")
@RestController
@RequestMapping("/api/journal")
public class LaporanAktivitasPengelolaController {

    @Autowired
    private CoaRepository coaRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    @GetMapping("/pengelola-activity-report")
    public ResponseEntity<Map<String, Object>> getPengelolaActivityReport(
            @RequestParam("month1") int month1,
            @RequestParam("year1") int year1,
            @RequestParam("month2") int month2,
            @RequestParam("year2") int year2
    ) {
        Map<String, Object> response = new LinkedHashMap<>();

        Map<String, Double> penerimaanDanaAmil = getPenerimaanDanaAmil(month1, year1, month2, year2);
        response.put("Penerimaan Dana Amil", penerimaanDanaAmil);

        Map<String, Object> pendayagunaanPengelola = getPendayagunaanPengelola(month1, year1, month2, year2);
        response.put("Pendayagunaan Pengelola", pendayagunaanPengelola);

        return ResponseEntity.ok(response);
    }

    private Map<String, Object> getPendayagunaanPengelola(int month1, int year1, int month2, int year2) {
        Map<String, Object> result = new LinkedHashMap<>();

        List<Coa> coaPengelola = coaRepository.findByParentAccountId(121L);

        String month1Name = Month.of(month1).name() + " " + year1;
        String month2Name = Month.of(month2).name() + " " + year2;

        double totalMonth1 = 0.0;
        double totalMonth2 = 0.0;

        for (Coa coa : coaPengelola) {
            Map<String, Double> monthlyTotals = calculateMonthlyBreakdown(coa.getId(), month1, year1, month2, year2);

            String key = coa.getAccountCode() + " " + coa.getAccountName();
            result.put(key, monthlyTotals);

            totalMonth1 += monthlyTotals.getOrDefault(month1Name, 0.0);
            totalMonth2 += monthlyTotals.getOrDefault(month2Name, 0.0);
        }

        result.put("Total Bulan " + month1Name, totalMonth1);
        result.put("Total Bulan " + month2Name, totalMonth2);

        return result;
    }

    private Map<String, Double> getPenerimaanDanaAmil(int month1, int year1, int month2, int year2) {
        Map<String, Double> result = new LinkedHashMap<>();

        double percentage = 12.5;

        LocalDate startDate = LocalDate.of(year1, month1, 1);
        LocalDate endDate = LocalDate.of(year2, month2, 1)
                .plusMonths(1)
                .minusDays(1);

        LocalDate current = startDate;

        double totalPeriode = 0.0;

        while (!current.isAfter(endDate)) {
            LocalDateTime monthStart = LocalDateTime.of(current.withDayOfMonth(1), LocalTime.MIN);
            LocalDateTime monthEnd = LocalDateTime.of(current.withDayOfMonth(current.lengthOfMonth()), LocalTime.MAX);

            Double baseAmount = transactionRepository.sumBaseForAmilByDateRange(monthStart, monthEnd);
            double amilAmount = (baseAmount != null ? baseAmount : 0.0) * (percentage / 100.0);

            String key = current.getMonth().name() + " " + current.getYear();
            result.put(key, amilAmount);

            totalPeriode += amilAmount;

            current = current.plusMonths(1);
        }

        result.put("Total Periode", totalPeriode);

        return result;
    }

    private Map<String, Double> calculateMonthlyBreakdown(Long coaId, int month1, int year1, int month2, int year2) {
        Map<String, Double> monthlyTotals = new LinkedHashMap<>();

        LocalDate startDate = LocalDate.of(year1, month1, 1);
        LocalDate endDate = LocalDate.of(year2, month2, 1)
                .plusMonths(1)
                .minusDays(1);

        LocalDate current = startDate;

        while (!current.isAfter(endDate)) {
            LocalDateTime monthStart = LocalDateTime.of(current.withDayOfMonth(1), LocalTime.MIN);
            LocalDateTime monthEnd = LocalDateTime.of(current.withDayOfMonth(current.lengthOfMonth()), LocalTime.MAX);

            Double total = transactionRepository.sumDebitOrKreditByCoaIdAndParentIdAndDateRange(coaId, monthStart, monthEnd);

            String key = current.getMonth().name() + " " + current.getYear();
            monthlyTotals.put(key, total != null ? total : 0.0);

            current = current.plusMonths(1);
        }

        return monthlyTotals;
    }
}
