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

        // Loop untuk menghitung surplus/defisit per bulan
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

        // --- PERBAIKAN DINAMIS: Saldo Awal Dana DSKL (COA ID 47) ---
        LocalDate reportStartDate = LocalDate.of(year1, month1, 1);
        
        // Ambil semua COA ID pendayagunaan dari kategori
        List<Long> pendayagunaanCoaIds = getPendayagunaanCategories().values().stream()
                .flatMap(List::stream)
                .toList();

        double runningSaldo = calculateInitialSaldo(47L, 98L, pendayagunaanCoaIds, reportStartDate);

        LocalDate currentDate = reportStartDate;
        while (!currentDate.isAfter(end)) {
            int month = currentDate.getMonthValue();
            int year = currentDate.getYear();
            String monthName = currentDate.getMonth().name();

            // Saldo Awal bulan ini adalah runningSaldo saat ini
            response.put("Saldo Awal Dana " + monthName + " " + year, runningSaldo);

            // Ambil mutasi surplus/defisit bulan ini
            double surplusDefisit = surplusDefisitPerBulan.getOrDefault(monthName + " " + year, 0.0);

            // Update runningSaldo (Saldo Akhir bulan ini)
            runningSaldo = runningSaldo + surplusDefisit;

            response.put("Saldo Akhir Dana " + monthName + " " + year, runningSaldo);

            currentDate = currentDate.plusMonths(1);
        }

        return ResponseEntity.ok(response);
    }

    /**
     * Menghitung saldo awal secara dinamis berdasarkan Saldo Awal Global + Mutasi Transaksi
     * hingga sehari sebelum tanggal laporan dimulai.
     */
    private double calculateInitialSaldo(Long danaCoaId, Long penerimaanParentId, List<Long> pendayagunaanCoaIds, LocalDate startDate) {
        // 1. Ambil Saldo Awal Global dari tabel saldo_awal
        Optional<SaldoAwal> saldoAwalOpt = saldoAwalRepository.findByCoa(coaRepository.findById(danaCoaId).orElseThrow());
        double initialSaldo = saldoAwalOpt.map(SaldoAwal::getSaldoAwal).orElse(0.0);
        
        // 2. Hitung mutasi transaksi dari awal sistem hingga sehari sebelum startDate
        LocalDateTime endOfPrevDay = startDate.minusDays(1).atTime(LocalTime.MAX);
        LocalDateTime systemStart = saldoAwalOpt.isPresent() 
            ? saldoAwalOpt.get().getTanggalInput().withDayOfMonth(1).atStartOfDay()
            : LocalDate.of(1900, 1, 1).atStartOfDay();

        // Gunakan logika yang sama dengan laporan: Penerimaan - Pendayagunaan
        Double totalPenerimaan = transactionRepository.sumVolumeByCoaTree(penerimaanParentId, systemStart, endOfPrevDay);
        Double totalPendayagunaan = transactionRepository.sumVolumeByCoaIds(pendayagunaanCoaIds, systemStart, endOfPrevDay);

        return initialSaldo + totalPenerimaan - (totalPendayagunaan != null ? totalPendayagunaan : 0.0);
    }

    // Helper method to get details of COA with transactions
    private Map<String, Object> getCoaDetailsWithTransactions(Long coaId, int month1, int year1, int month2,
            int year2) {
        Map<String, Object> details = new LinkedHashMap<>();
        List<Coa> childCoas = coaRepository.findByParentAccount_IdIn(List.of(coaId));

        for (Coa childCoa : childCoas) {
            // Menghitung breakdown per bulan untuk seluruh rentang periode
            Map<String, Double> monthlyTotals = calculateMonthlyBreakdown(childCoa.getId(), month1, year1, month2,
                    year2);

            // Memasukkan data breakdown per bulan ke dalam details
            details.put(childCoa.getAccountName(), monthlyTotals);
        }

        // Menghitung total masing-masing bulan untuk seluruh rentang periode
        LocalDate start = LocalDate.of(year1, month1, 1);
        LocalDate end = LocalDate.of(year2, month2, 1);
        
        while (!start.isAfter(end)) {
            String monthKey = start.getMonth().name() + " " + start.getYear();
            double monthlyTotal = 0.0;
            
            for (Coa childCoa : childCoas) {
                Map<String, Double> monthlyTotals = (Map<String, Double>) details.get(childCoa.getAccountName());
                monthlyTotal += monthlyTotals.getOrDefault(monthKey, 0.0);
            }
            
            details.put("Total Bulan " + monthKey, monthlyTotal);
            start = start.plusMonths(1);
        }

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

        for (var entry : categories.entrySet()) {
            String category = entry.getKey();
            List<Long> coaIds = entry.getValue();

            Map<String, Double> monthlyTotals = calculateMonthlyBreakdownForMultipleCoa(
                    coaIds, month1, year1, month2, year2);

            result.put(category, monthlyTotals);
        }

        // Menghitung total masing-masing bulan untuk seluruh rentang periode
        LocalDate start = LocalDate.of(year1, month1, 1);
        LocalDate end = LocalDate.of(year2, month2, 1);
        
        while (!start.isAfter(end)) {
            String monthKey = start.getMonth().name() + " " + start.getYear();
            double monthlyTotal = 0.0;
            
            for (var entry : categories.entrySet()) {
                Map<String, Double> monthlyTotals = (Map<String, Double>) result.get(entry.getKey());
                monthlyTotal += monthlyTotals.getOrDefault(monthKey, 0.0);
            }
            
            result.put("Total Bulan " + monthKey, monthlyTotal);
            start = start.plusMonths(1);
        }

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
