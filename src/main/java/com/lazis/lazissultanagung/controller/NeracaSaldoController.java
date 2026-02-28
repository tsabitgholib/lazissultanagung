package com.lazis.lazissultanagung.controller;

import com.lazis.lazissultanagung.model.Coa;
import com.lazis.lazissultanagung.repository.CoaRepository;
import com.lazis.lazissultanagung.repository.SaldoAwalRepository;
import com.lazis.lazissultanagung.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@CrossOrigin(origins= {"*"}, maxAge = 4800, allowCredentials = "false")
@RestController
@RequestMapping("/api/journal")
public class NeracaSaldoController {

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private CoaRepository coaRepository;

    @Autowired
    private SaldoAwalRepository saldoAwalRepository;

    @GetMapping("/neraca-saldo-report")
    public ResponseEntity<Map<String, Object>> getFinancialReport(
            @RequestParam("startDate") String startDateStr,
            @RequestParam("endDate") String endDateStr
    ) {
        Map<String, Object> response = new LinkedHashMap<>();

        // Konversi parameter string ke LocalDate
        LocalDate startDate = LocalDate.parse(startDateStr);
        LocalDate endDate = LocalDate.parse(endDateStr);

        // Kategori Akun COA
        Map<String, Object> asetLancar = calculateDetailsByParentId(List.of(1L, 23L, 25L, 28L, 30L, 32L), startDate, endDate);
        Map<String, Object> asetTetap = calculateDetailsByParentId(List.of(34L, 36L, 38L), startDate, endDate);
        Map<String, Object> asetLainLain = calculateDetailsByParentId(List.of(40L), startDate, endDate);
        Map<String, Object> kewajibanLancar = calculateDetailsByParentId(List.of(42L), startDate, endDate);
        Map<String, Object> saldoDanaZis = calculateDetailsByParentId(List.of(44L), startDate, endDate);

        // Menambahkan kategori ke dalam response
        response.put("Aset Lancar", asetLancar);
        response.put("Aset Tetap", asetTetap);
        response.put("Aset Lain-Lain", asetLainLain);
        response.put("Kewajiban Lancar", kewajibanLancar);
        response.put("Dana ZIS", saldoDanaZis);

        // Perhitungan total aset
        Double jumlahAsetLancar = sumAccountBalances(asetLancar);
        Double jumlahAsetTetap = sumAccountBalances(asetTetap);
        Double jumlahAsetLainLain = sumAccountBalances(asetLainLain);
        Double jumlahAset = jumlahAsetLancar + jumlahAsetTetap + jumlahAsetLainLain;

        // Perhitungan total kewajiban dan dana ZIS
        Double jumlahKewajibanLancar = sumAccountBalances(kewajibanLancar);
        Double jumlahSaldoDanaZis = sumAccountBalances(saldoDanaZis);
        Double jumlahKewajibanDanDanaZis = jumlahKewajibanLancar + jumlahSaldoDanaZis;

        // Menambahkan total ke dalam response
        response.put("Jumlah Aset Lancar", jumlahAsetLancar);
        response.put("Jumlah Aset Tetap", jumlahAsetTetap);
        response.put("Jumlah Aset Lain-Lain", jumlahAsetLainLain);
        response.put("JUMLAH ASET", jumlahAset);
        response.put("Jumlah Kewajiban Lancar", jumlahKewajibanLancar);
        response.put("Jumlah Saldo Dana-Dana ZIS", jumlahSaldoDanaZis);
        response.put("JUMLAH KEWAJIBAN DAN DANA ZIS", jumlahKewajibanDanDanaZis);

        return ResponseEntity.ok(response);
    }

    private Double sumAccountBalances(Map<String, Object> accountDetails) {
        return accountDetails.values().stream()
                .filter(value -> value instanceof Map)
                .map(value -> (Map<String, Object>) value)
                .mapToDouble(account -> (Double) account.getOrDefault("Saldo Akhir", 0.0))
                .sum();
    }

    // private Map<String, Object> calculateDetailsByCoaIds(List<Long> coaIds, LocalDate startDate, LocalDate endDate) {
    //     Map<String, Object> details = new LinkedHashMap<>();
    //     for (Long coaId : coaIds) {
    //         Coa coa = coaRepository.findById(coaId).orElse(null);
    //         if (coa != null) {
    //             Map<String, Object> accountDetails = calculateAccountDetails(coaId, startDate, endDate);
    //             String kodeName = coa.getAccountCode() + " " + coa.getAccountName();
    //             details.put(kodeName, accountDetails);
    //         }
    //     }
    //     return details;
    // }

    private Map<String, Object> calculateDetailsByParentId(List<Long> parentId, LocalDate startDate, LocalDate endDate) {
        List<Coa> coaList = coaRepository.findByParentAccount_IdIn(parentId);
        Map<String, Object> details = new LinkedHashMap<>();
        for (Coa coa : coaList) {
            Map<String, Object> accountDetails = calculateAccountDetails(coa.getId(), startDate, endDate);
            String kodeName = coa.getAccountCode() + " " + coa.getAccountName();
            details.put(kodeName, accountDetails);
        }
        return details;
    }

    private Map<String, Object> calculateAccountDetails(Long coaId, LocalDate startDate, LocalDate endDate) {
        Map<String, Object> accountDetails = new LinkedHashMap<>();

        // Saldo Awal
        Double saldoAwal = saldoAwalRepository.findSaldoAwalByCoaId(coaId).orElse(0.0);

        // Debit, Kredit, dan Saldo Akhir
        LocalDateTime startDateTime = LocalDateTime.of(startDate, LocalTime.MIN);
        LocalDateTime endDateTime = LocalDateTime.of(endDate, LocalTime.MAX);

        Double totalDebit = transactionRepository.sumDebitByCoaIdAndDateRanges(coaId, startDateTime, endDateTime).orElse(0.0);
        Double totalKredit = transactionRepository.sumCreditByCoaIdAndDateRange(coaId, startDateTime, endDateTime).orElse(0.0);
        Double saldoAkhir = saldoAwal + totalDebit - totalKredit;

        // Masukkan hasil ke dalam detail akun
        accountDetails.put("Saldo Awal", saldoAwal);
        accountDetails.put("Total Debit", totalDebit);
        accountDetails.put("Total Kredit", totalKredit);
        accountDetails.put("Saldo Akhir", saldoAkhir);

        return accountDetails;
    }
}
