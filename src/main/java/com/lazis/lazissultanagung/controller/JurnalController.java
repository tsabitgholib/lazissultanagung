package com.lazis.lazissultanagung.controller;

import com.lazis.lazissultanagung.dto.response.JurnalResponse;
import com.lazis.lazissultanagung.dto.response.JurnalResponseWrapper;
import com.lazis.lazissultanagung.dto.response.LaporanAktivitasWithTotalResponse;
import com.lazis.lazissultanagung.model.Coa;
import com.lazis.lazissultanagung.repository.CoaRepository;
import com.lazis.lazissultanagung.repository.SaldoAwalRepository;
import com.lazis.lazissultanagung.repository.TransactionRepository;
import com.lazis.lazissultanagung.service.JurnalService;
import com.lazis.lazissultanagung.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@CrossOrigin(origins= {"*"}, maxAge = 4800, allowCredentials = "false" )
@RestController
@RequestMapping("/api/journal")
public class JurnalController {

    @Autowired
    private JurnalService jurnalService;

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private CoaRepository coaRepository;

    @Autowired
    private SaldoAwalRepository saldoAwalRepository;

    @GetMapping
    public ResponseEntity<JurnalResponseWrapper> getJournal(
            @RequestParam("startDate") LocalDate startDate,
            @RequestParam("endDate") LocalDate endDate
    ) {
        // Ambil data dari service
        JurnalResponseWrapper responseWrapper = jurnalService.getJurnalFilterDate(startDate, endDate);

        // Pastikan return response wrapper sesuai
        return ResponseEntity.ok(responseWrapper);
    }

//    @GetMapping("/neraca/compare")
//    public ResponseEntity<Map<String, Object>> getNeracaComparisonHierarchical(
//            @RequestParam("month1") int month1,
//            @RequestParam("year1") int year1,
//            @RequestParam("month2") int month2,
//            @RequestParam("year2") int year2) {
//        Map<String, Object> response = jurnalService.getNeracaComparisonHierarchical(month1, year1, month2, year2);
//        return ResponseEntity.ok(response);
//    }
//
//    @GetMapping("/neraca/compare-detail")
//    public ResponseEntity<List<Map<String, Object>>> getDetailedNeraca(
//            @RequestParam("month1") int month1,
//            @RequestParam("year1") int year1,
//            @RequestParam("month2") int month2,
//            @RequestParam("year2") int year2) {
//        LocalDate startDate = LocalDate.of(year1, month1, 1);
//        LocalDate endDate = LocalDate.of(year2, month2, 1).withDayOfMonth(LocalDate.of(year2, month2, 1).lengthOfMonth());
//        List<Map<String, Object>> response = jurnalService.getDetailedAccountHierarchy(startDate, endDate);
//        return ResponseEntity.ok(response);
//    }

//    @GetMapping("/laporan-aktivitas")
//    public ResponseEntity<?> getLaporanAktivitasKeuangan(
//            @RequestParam String jenis, // zakat, infak, wakaf, dskl
//            @RequestParam int month1,
//            @RequestParam int year1,
//            @RequestParam int month2,
//            @RequestParam int year2
//    ) {
//        LaporanAktivitasWithTotalResponse response = jurnalService.getLaporanAktivitasKeuangan(jenis, month1, year1, month2, year2);
//        return ResponseEntity.ok(response);
//    }

    @GetMapping("/neraca-report")
    public ResponseEntity<Map<String, Object>> getFinancialReport(
            @RequestParam("month1") int month1,
            @RequestParam("year1") int year1,
            @RequestParam("month2") int month2,
            @RequestParam("year2") int year2,
            @RequestParam("level") String level // Parameter level ditambahkan
    ) {
        LocalDateTime startDate = LocalDateTime.of(LocalDate.of(year1, month1, 1), LocalTime.MIN);
        LocalDateTime endDate = LocalDateTime.of(LocalDate.of(year2, month2, 1).withDayOfMonth(1).plusMonths(1).minusDays(1), LocalTime.MAX);

        Map<String, Object> response = new LinkedHashMap<>();

        // Level 2 atau Level 3
        boolean isLevel3 = "level3".equalsIgnoreCase(level);

        // ASET LANCAR
        List<Long> asetLancarIds = List.of(1L, 23L, 25L, 27L, 29L, 31L);
        Map<String, Object> asetLancarDetails = isLevel3
                ? calculateDetailsByCoaIdsWithSubAccounts(asetLancarIds, month1, year1, month2, year2)
                : calculateDetailsByCoaIdsWithMonthlyBreakdown(asetLancarIds, month1, year1, month2, year2);
        response.put("Aset Lancar", asetLancarDetails);

        // ASET TETAP
        List<Long> asetTetapIds = List.of(33L);
        Map<String, Object> asetTetapDetails = isLevel3
                ? calculateDetailsByCoaIdsWithSubAccounts(asetTetapIds, month1, year1, month2, year2)
                : calculateDetailsByCoaIdsWithMonthlyBreakdown(asetTetapIds, month1, year1, month2, year2);
        response.put("Aset Tetap", asetTetapDetails);

        // ASET LAIN-LAIN
        List<Long> asetLainLainIds = List.of(40L);
        Map<String, Object> asetLainLainDetails = isLevel3
                ? calculateDetailsByCoaIdsWithSubAccounts(asetLainLainIds, month1, year1, month2, year2)
                : calculateDetailsByCoaIdsWithMonthlyBreakdown(asetLainLainIds, month1, year1, month2, year2);
        response.put("Aset Lain-Lain", asetLainLainDetails);

        // KEWAJIBAN LANCAR
        List<Long> kewajibanLancarIds = List.of(42L);
        Map<String, Object> kewajibanLancarDetails = isLevel3
                ? calculateDetailsByCoaIdsWithSubAccounts(kewajibanLancarIds, month1, year1, month2, year2)
                : calculateDetailsByCoaIdsWithMonthlyBreakdown(kewajibanLancarIds, month1, year1, month2, year2);
        response.put("Kewajiban Lancar", kewajibanLancarDetails);

        // DANA ZIS
        List<Long> danaZISIds = List.of(44L);
        Map<String, Object> danaZisDetails = isLevel3
                ? calculateDetailsByCoaIdsWithSubAccounts(danaZISIds, month1, year1, month2, year2)
                : calculateDetailsByCoaIdsWithMonthlyBreakdown(danaZISIds, month1, year1, month2, year2);
        response.put("Dana ZIS", danaZisDetails);

        // Hitung JUMLAH ASET
        Map<String, Double> jumlahAsetPerBulan = new LinkedHashMap<>();
        asetLancarDetails.forEach((key, value) -> {
            if (key.startsWith("Total")) {
                jumlahAsetPerBulan.merge(key.replace("Total ", ""), (Double) value, Double::sum);
            }
        });
        asetTetapDetails.forEach((key, value) -> {
            if (key.startsWith("Total")) {
                jumlahAsetPerBulan.merge(key.replace("Total ", ""), (Double) value, Double::sum);
            }
        });
        asetLainLainDetails.forEach((key, value) -> {
            if (key.startsWith("Total")) {
                jumlahAsetPerBulan.merge(key.replace("Total ", ""), (Double) value, Double::sum);
            }
        });

        jumlahAsetPerBulan.forEach((month, total) -> response.put("Jumlah Aset " + month, total));


        // Hitung JUMLAH KEWAJIBAN DAN DANA ZIS
        Map<String, Double> jumlahKewajibanDanDanaZisPerBulan = new LinkedHashMap<>();

// Menggabungkan total per bulan dari Kewajiban Lancar
        kewajibanLancarDetails.forEach((key, value) -> {
            if (key.startsWith("Total")) {
                jumlahKewajibanDanDanaZisPerBulan.merge(key.replace("Total ", ""), (Double) value, Double::sum);
            }
        });

// Menggabungkan total per bulan dari Dana ZIS
        danaZisDetails.forEach((key, value) -> {
            if (key.startsWith("Total")) {
                jumlahKewajibanDanDanaZisPerBulan.merge(key.replace("Total ", ""), (Double) value, Double::sum);
            }
        });

// Menyimpan ke response
        jumlahKewajibanDanDanaZisPerBulan.forEach((month, total) -> response.put("Jumlah Kewajiban dan Dana ZIS " + month, total));


        return ResponseEntity.ok(response);
    }



    //HELPERSSSS
    private Map<String, Object> calculateDetailsByCoaIdsWithMonthlyBreakdown(List<Long> coaIds, int month1, int year1, int month2, int year2) {
        Map<String, Object> details = new LinkedHashMap<>();
        Map<String, Double> monthlyTotals = new LinkedHashMap<>();

        for (Long coaId : coaIds) {
            Coa coa = coaRepository.findById(coaId).orElse(null);
            if (coa != null) {
                Map<String, Double> breakdown = calculateMonthlyBreakdown(coa.getId(), month1, year1, month2, year2);
                String kodeName = coa.getAccountCode() + " " + coa.getAccountName();
                details.put(kodeName, breakdown);

                // Tambahkan nilai bulanan ke monthlyTotals
                breakdown.forEach((month, value) ->
                        monthlyTotals.merge(month, value, Double::sum)
                );
            }
        }
        // Masukkan total bulanan ke dalam details
        monthlyTotals.forEach((month, total) -> details.put("Total " + month, total));

        return details;
    }

//    private Map<String, Object> calculateDetailsByCoaIdsWithMonthlyBreakdown(List<Long> coaIds, int month1, int year1, int month2, int year2) {
//        Map<String, Object> details = new LinkedHashMap<>();
//        Map<String, Double> monthlyTotals = new LinkedHashMap<>();
//
//        for (Long coaId : coaIds) {
//            Coa coa = coaRepository.findById(coaId).orElse(null);
//            if (coa != null) {
//                Map<String, Double> breakdown = calculateMonthlyBreakdown(coa.getId(), month1, year1, month2, year2);
//
//                // Format data seperti yang diminta
//                Map<String, Object> accountDetails = new LinkedHashMap<>();
//                accountDetails.put("account_code", coa.getAccountCode());
//                accountDetails.put("account_name", coa.getAccountName());
//                accountDetails.put("monthly_breakdown", breakdown);
//
//                details.put(coa.getAccountName(), accountDetails);
//
//                // Tambahkan nilai bulanan ke monthlyTotals
//                breakdown.forEach((month, value) ->
//                        monthlyTotals.merge(month, value, Double::sum)
//                );
//            }
//        }
//
//        // Masukkan total bulanan ke dalam details
//        Map<String, Double> totalBreakdown = new LinkedHashMap<>();
//        monthlyTotals.forEach(totalBreakdown::put);
//        details.put("Total", totalBreakdown);
//
//        return details;
//    }



    private Map<String, Double> calculateMonthlyBreakdown(Long coaId, int month1, int year1, int month2, int year2) {
        Map<String, Double> monthlyTotals = new LinkedHashMap<>();

        LocalDate startDate = LocalDate.of(year1, month1, 1);
        LocalDate endDate = LocalDate.of(year2, month2, 1).withDayOfMonth(1).plusMonths(1).minusDays(1);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM yyyy").withLocale(new Locale("id", "ID"));

        // Hitung total untuk bulan pertama (month1)
        LocalDateTime month1Start = LocalDateTime.of(startDate.withDayOfMonth(1), LocalTime.MIN);
        LocalDateTime month1End = LocalDateTime.of(startDate.withDayOfMonth(startDate.lengthOfMonth()), LocalTime.MAX);

        Double totalMonth1 = transactionRepository.sumDebitByCoaIdAndParentIdAndDateRange(coaId, month1Start, month1End);
        Double saldoAwal = saldoAwalRepository.findSaldoAwalByCoaId(coaId).orElse(0.0);

        totalMonth1 = (totalMonth1 != null ? totalMonth1 : 0.0) + saldoAwal;
        monthlyTotals.put(startDate.format(formatter), totalMonth1);

        // Hitung akumulasi dari bulan1 sampai bulan2
        LocalDateTime month2Start = LocalDateTime.of(startDate.withDayOfMonth(1), LocalTime.MIN); // Mulai dari bulan1
        LocalDateTime month2End = LocalDateTime.of(endDate.withDayOfMonth(endDate.lengthOfMonth()), LocalTime.MAX); // Hingga bulan2 akhir

        Double totalMonth2 = transactionRepository.sumDebitByCoaIdAndParentIdAndDateRange(coaId, month2Start, month2End);
        totalMonth2 = (totalMonth2 != null ? totalMonth2 : 0.0) + saldoAwal;

        monthlyTotals.put(endDate.format(formatter), totalMonth2);

        return monthlyTotals;
    }


    private Map<String, Object> calculateDetailsByCoaIdsWithSubAccounts(List<Long> coaIds, int month1, int year1, int month2, int year2) {
        Map<String, Object> details = new LinkedHashMap<>();
        Map<String, Double> monthlyTotals = new LinkedHashMap<>();

        for (Long coaId : coaIds) {
            Coa parentCoa = coaRepository.findById(coaId).orElse(null);
            if (parentCoa != null) {
                Map<String, Object> parentBreakdown = new LinkedHashMap<>();
                List<Coa> subAccounts = coaRepository.findByParentAccount_Id(parentCoa.getId());

                // Hitung breakdown untuk setiap sub-akun
                for (Coa subCoa : subAccounts) {
                    Map<String, Double> subBreakdown = calculateMonthlyBreakdown(subCoa.getId(), month1, year1, month2, year2);
                    parentBreakdown.put(subCoa.getAccountCode() + " " + subCoa.getAccountName(), subBreakdown);

                    // Tambahkan nilai bulanan ke monthlyTotals
                    subBreakdown.forEach((month, value) ->
                            monthlyTotals.merge(month, value, Double::sum)
                    );
                }
                String kodeName = parentCoa.getAccountCode() + " " + parentCoa.getAccountName();

                details.put(kodeName, parentBreakdown);
            }
        }

        // Masukkan total bulanan ke dalam details
        monthlyTotals.forEach((month, total) -> details.put("Total " + month, total));

        return details;
    }



}

