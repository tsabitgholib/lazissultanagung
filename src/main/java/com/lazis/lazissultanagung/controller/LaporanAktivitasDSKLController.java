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
import org.springframework.web.bind.annotation.*;

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
public class LaporanAktivitasDSKLController {

    @Autowired
    private SaldoAwalRepository saldoAwalRepository;

    @Autowired
    private CoaRepository coaRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private SaldoAkhirRepository saldoAkhirRepository;

    @GetMapping("/dskl-activity-report")
    public ResponseEntity<Map<String, Object>> getDsklActivityReport(
            @RequestParam("month1") int month1,
            @RequestParam("year1") int year1,
            @RequestParam("month2") int month2,
            @RequestParam("year2") int year2) {
        Map<String, Object> response = new LinkedHashMap<>();

        Map<String, Object> penerimaanDSKLDetails = getCoaDetailsWithTransactions(98L, month1, year1, month2, year2);
        response.put("Penerimaan Dana", penerimaanDSKLDetails);

        Map<String, Object> pendayagunaanDSKLDetails = getPendayagunaanByCategory(month1, year1, month2, year2);
        response.put("Pendayagunaan Dana", pendayagunaanDSKLDetails);

        // Variabel untuk menyimpan surplus/defisit per bulan
        Map<String, Double> surplusDefisitPerBulan = new LinkedHashMap<>();

        // Loop untuk menghitung surplus/defisit per bulan (NOVEMBER, DECEMBER)
        LocalDate start = LocalDate.of(year1, month1, 1);
        LocalDate end = LocalDate.of(year2, month2, 1);

        while (!start.isAfter(end)) {

            int month = start.getMonthValue();
            int year = start.getYear();
            String monthName = start.getMonth().name();

            Double totalPenerimaan = (Double) penerimaanDSKLDetails
                    .getOrDefault("Total Bulan " + monthName + " " + year, 0.0);

            Double totalPendayagunaan = (Double) pendayagunaanDSKLDetails
                    .getOrDefault("Total Bulan " + monthName + " " + year, 0.0);

            double surplus = totalPenerimaan - totalPendayagunaan;

            surplusDefisitPerBulan.put(monthName + " " + year, surplus);

            response.put("Surplus (Defisit) Dana " + monthName + " " + year, surplus);

            start = start.plusMonths(1);
        }

        // Saldo Awal Dana dskl (COA ID 47)
        double saldoAwalDanaDSKL = 0.0;
        LocalDate currentDate = LocalDate.of(year1, month1, 1);

        while (currentDate.getYear() < year2
                || (currentDate.getYear() == year2 && currentDate.getMonthValue() <= month2)) {
            int month = currentDate.getMonthValue();
            int year = currentDate.getYear();

            Optional<SaldoAwal> saldoAwalOpt = saldoAwalRepository.findSaldoAwalByCoaAndMonthAndYear(47L, month, year);
            if (saldoAwalOpt.isPresent()) {
                saldoAwalDanaDSKL = saldoAwalOpt.get().getSaldoAwal();
            } else {
                String key = "Saldo Akhir Dana " + currentDate.minusMonths(1).getMonth().name() + " "
                        + currentDate.minusMonths(1).getYear();
                if (response.containsKey(key) && response.get(key) != null) {
                    saldoAwalDanaDSKL = (double) response.get(key);
                } else {
                    LocalDate prevDate = currentDate.minusMonths(1);
                    Optional<SaldoAkhir> prevSaldoAkhir = saldoAkhirRepository.findByCoa_IdAndMonthAndYear(47L, prevDate.getMonthValue(), prevDate.getYear());
                    saldoAwalDanaDSKL = prevSaldoAkhir.map(SaldoAkhir::getSaldoAkhir).orElse(0.0);
                }
            }

            response.put("Saldo Awal Dana " + currentDate.getMonth().name() + " " + year, saldoAwalDanaDSKL);

            // Mengambil surplus/defisit untuk bulan ini
            String monthName = currentDate.getMonth().name();
            double surplusDefisit = surplusDefisitPerBulan
                    .getOrDefault(monthName + " " + (month == month2 ? year2 : year1), 0.0);

            // Menghitung saldo akhir Dana Zakat bulan ini
            // double saldoAkhirDanaDSKL = surplusDefisit >= 0
            //         ? saldoAwalDanaDSKL + surplusDefisit
            //         : saldoAwalDanaDSKL - surplusDefisit;
            double saldoAkhirDanaDSKL = saldoAwalDanaDSKL + surplusDefisit;

            Optional<SaldoAkhir> existingSaldoAkhir = saldoAkhirRepository.findByCoa_IdAndMonthAndYear(47L, month, year);
            SaldoAkhir saldoAkhirDanaDSKLEntity = existingSaldoAkhir.orElse(new SaldoAkhir());
            saldoAkhirDanaDSKLEntity.setCoa(coaRepository.findById(47L).orElse(null));
            saldoAkhirDanaDSKLEntity.setMonth(month);
            saldoAkhirDanaDSKLEntity.setYear(year);
            saldoAkhirDanaDSKLEntity.setSaldoAkhir(saldoAkhirDanaDSKL);
            saldoAkhirRepository.save(saldoAkhirDanaDSKLEntity);


            response.put("Saldo Akhir Dana " + currentDate.getMonth().name() + " " + year, saldoAkhirDanaDSKL);

            currentDate = currentDate.plusMonths(1);
        }

        return ResponseEntity.ok(response);
    }

    // Helper method to get details of COA with transactions
    private Map<String, Object> getCoaDetailsWithTransactions(Long coaId, int month1, int year1, int month2,
            int year2) {
        Map<String, Object> details = new LinkedHashMap<>();
        List<Coa> childCoas = coaRepository.findByParentAccount_IdIn(List.of(coaId));

        double month1Total = 0.0;
        double month2Total = 0.0;

        // Menyimpan nama bulan untuk digunakan di akhir
        String month1Name = Month.of(month1).name() + " " + year1;
        String month2Name = Month.of(month2).name() + " " + year2;

        for (Coa childCoa : childCoas) {
            // Menghitung breakdown per bulan untuk bulan 1 dan bulan 2
            Map<String, Double> monthlyTotals = calculateMonthlyBreakdown(childCoa.getId(), month1, year1, month2,
                    year2);

            // Memasukkan data breakdown per bulan ke dalam details
            details.put(childCoa.getAccountName(), monthlyTotals);

            // Cek apakah data bulan pertama ada, jika ada tambah ke total bulan pertama
            if (monthlyTotals.containsKey(month1Name)) {
                month1Total += monthlyTotals.get(month1Name);
            }

            // Cek apakah data bulan kedua ada, jika ada tambah ke total bulan kedua
            if (monthlyTotals.containsKey(month2Name)) {
                month2Total += monthlyTotals.get(month2Name);
            }
        }

        // Memasukkan total masing-masing bulan ke dalam details dengan format yang
        // lebih deskriptif
        details.put("Total Bulan " + month1Name, month1Total);
        details.put("Total Bulan " + month2Name, month2Total);

        return details;
    }

    private Map<String, Double> calculateMonthlyBreakdown(Long coaId, int month1, int year1, int month2, int year2) {
        Map<String, Double> monthlyTotals = new LinkedHashMap<>();

        LocalDate startDate = LocalDate.of(year1, month1, 1);
        LocalDate endDate = LocalDate.of(year2, month2, 1).withDayOfMonth(1).plusMonths(1).minusDays(1);

        LocalDate current = startDate;
        while (!current.isAfter(endDate)) {
            LocalDateTime monthStart = LocalDateTime.of(current.withDayOfMonth(1), LocalTime.MIN);
            LocalDateTime monthEnd = LocalDateTime.of(current.withDayOfMonth(current.lengthOfMonth()), LocalTime.MAX);

            // Gunakan query yang dimodifikasi
            Double totalDebit = transactionRepository.sumDebitOrKreditByCoaIdAndParentIdAndDateRange(coaId, monthStart,
                    monthEnd);

            monthlyTotals.put(current.getMonth().name() + " " + current.getYear(),
                    totalDebit != null ? totalDebit : 0.0);

            current = current.plusMonths(1);
        }

        return monthlyTotals;
    }

    private Map<String, List<Long>> getPendayagunaanCategories() {

        Map<String, List<Long>> categories = new LinkedHashMap<>();

        categories.put("Dakwah", List.of(103L, 104L, 105L));
        categories.put("Pendidikan", List.of(106L, 107L, 108L, 109L, 110L, 111L, 112L, 113L));
        categories.put("Kesehatan", List.of(114L, 115L));
        // categories.put("EKONOMI", List.of(87L,88L,89L,90L));
        // categories.put("SOSIAL KEMANUSIAAN", List.of(91L,92L,93L,94L));
        // categories.put("LINGKUNGAN", List.of(95L,96L,97L));

        return categories;
    }

    private Map<String, Object> getPendayagunaanByCategory(
            int month1, int year1, int month2, int year2) {

        Map<String, Object> result = new LinkedHashMap<>();
        Map<String, List<Long>> categories = getPendayagunaanCategories();

        String month1Name = Month.of(month1).name() + " " + year1;
        String month2Name = Month.of(month2).name() + " " + year2;

        double totalMonth1 = 0;
        double totalMonth2 = 0;

        for (var entry : categories.entrySet()) {

            String category = entry.getKey();
            List<Long> coaIds = entry.getValue();

            Map<String, Double> monthlyTotals = calculateMonthlyBreakdownForMultipleCoa(
                    coaIds, month1, year1, month2, year2);

            result.put(category, monthlyTotals);

            totalMonth1 += monthlyTotals.getOrDefault(month1Name, 0.0);
            totalMonth2 += monthlyTotals.getOrDefault(month2Name, 0.0);
        }

        result.put("Total Bulan " + month1Name, totalMonth1);
        result.put("Total Bulan " + month2Name, totalMonth2);

        return result;
    }

    private Map<String, Double> calculateMonthlyBreakdownForMultipleCoa(
            List<Long> coaIds,
            int month1, int year1,
            int month2, int year2) {

        Map<String, Double> monthlyTotals = new LinkedHashMap<>();

        LocalDate startDate = LocalDate.of(year1, month1, 1);
        LocalDate endDate = LocalDate.of(year2, month2, 1)
                .plusMonths(1)
                .minusDays(1);

        LocalDate current = startDate;

        while (!current.isAfter(endDate)) {

            LocalDateTime monthStart = current.withDayOfMonth(1).atStartOfDay();

            LocalDateTime monthEnd = current.withDayOfMonth(current.lengthOfMonth())
                    .atTime(LocalTime.MAX);

            Double total = transactionRepository.sumByCoaIdsAndDateRange(
                    coaIds, monthStart, monthEnd);

            monthlyTotals.put(
                    current.getMonth().name() + " " + current.getYear(),
                    total != null ? total : 0.0);

            current = current.plusMonths(1);
        }

        return monthlyTotals;
    }

}
