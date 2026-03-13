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
        Map<String, Object> response = new LinkedHashMap<>();

        Map<String, Object> penerimaan = getPenerimaanDanaPengelola(month1, year1, month2, year2);
        response.put("Penerimaan Dana Pengelola", penerimaan);

        Map<String, Object> pendayagunaanPengelola = getPendayagunaanPengelola(month1, year1, month2, year2);
        response.put("Pendayagunaan Pengelola", pendayagunaanPengelola);

        // Perhitungan Surplus/Defisit per bulan
        Map<String, Double> surplusDefisitPerBulan = new LinkedHashMap<>();
        LocalDate startDate = LocalDate.of(year1, month1, 1);
        LocalDate endDate = LocalDate.of(year2, month2, 1);
        LocalDate current = startDate;

        while (!current.isAfter(endDate)) {
            String monthName = current.getMonth().name();
            String keyTotal = "Total Bulan " + monthName + " " + current.getYear();

            Double totalPenerimaan = (Double) penerimaan.get(keyTotal);
            Double totalPendayagunaan = (Double) pendayagunaanPengelola.get(keyTotal);

            if (totalPenerimaan == null) totalPenerimaan = 0.0;
            if (totalPendayagunaan == null) totalPendayagunaan = 0.0;

            double surplusDefisit = totalPenerimaan - totalPendayagunaan;
            surplusDefisitPerBulan.put(monthName + " " + current.getYear(), surplusDefisit);

            response.put("Surplus (Defisit) Dana " + monthName + " " + current.getYear(), surplusDefisit);
            current = current.plusMonths(1);
        }

        // --- PERBAIKAN DINAMIS: Saldo Awal Dana Pengelola (COA ID 50) ---
        double runningSaldo = calculateInitialSaldo(startDate);
        current = startDate;

        while (!current.isAfter(endDate)) {
            String monthName = current.getMonth().name();
            String yearMonthKey = monthName + " " + current.getYear();

            // Saldo Awal bulan ini
            response.put("Saldo Awal Dana " + yearMonthKey, runningSaldo);

            // Mutasi bulan ini
            double surplusDefisit = surplusDefisitPerBulan.getOrDefault(yearMonthKey, 0.0);
            
            // Update running saldo (menjadi Saldo Akhir bulan ini)
            runningSaldo = runningSaldo + surplusDefisit;

            response.put("Saldo Akhir Dana " + yearMonthKey, runningSaldo);
            current = current.plusMonths(1);
        }

        return ResponseEntity.ok(response);
    }

    /**
     * Menghitung saldo awal Dana Pengelola secara dinamis.
     * Menggabungkan Saldo Awal Global + (Penerimaan Amil - Pendayagunaan Amil) hingga sebelum startDate.
     */
    private double calculateInitialSaldo(LocalDate startDate) {
        // 1. Ambil Saldo Awal Global (COA 50)
        Optional<SaldoAwal> saldoAwalOpt = saldoAwalRepository.findByCoa(coaRepository.findById(50L).orElseThrow());
        double initialSaldo = saldoAwalOpt.map(SaldoAwal::getSaldoAwal).orElse(0.0);

        // 2. Tentukan rentang waktu mutasi (Awal sistem s/d H-1)
        LocalDateTime endOfPrevDay = startDate.minusDays(1).atTime(LocalTime.MAX);
        LocalDateTime systemStart = saldoAwalOpt.isPresent()
                ? saldoAwalOpt.get().getTanggalInput().withDayOfMonth(1).atStartOfDay()
                : LocalDate.of(1900, 1, 1).atStartOfDay();

        // 3. Hitung akumulasi Penerimaan Dana Pengelola (Amil + Bagi Hasil)
        // Logika Amil: 12.5% Zakat + 20% Infak/Campaign/DSKL
        Double zakat = transactionRepository.sumZakatByDateRange(systemStart, endOfPrevDay);
        Double infak = transactionRepository.sumInfakByDateRange(systemStart, endOfPrevDay);
        Double campaign = transactionRepository.sumCampaignByDateRange(systemStart, endOfPrevDay);
        Double dskl = transactionRepository.sumDSKLByDateRange(systemStart, endOfPrevDay);
        
        double accumPenerimaanAmil = (zakat * 0.125) + ((infak + campaign) * 0.20) + (dskl * 0.20);
        
        // Logika Bagi Hasil (COA 120)
        Double bagiHasil = transactionRepository.sumDebitOrKreditByCoaIdAndParentIdAndDateRange(120L, systemStart, endOfPrevDay);
        double accumBagiHasil = bagiHasil != null ? bagiHasil : 0.0;

        // 4. Hitung akumulasi Pendayagunaan Pengelola (COA 121 dan anak-anaknya)
        Double accumPendayagunaan = transactionRepository.sumVolumeByCoaTree(121L, systemStart, endOfPrevDay);

        return initialSaldo + (accumPenerimaanAmil + accumBagiHasil) - accumPendayagunaan;
    }

    private Map<String, Object> getPendayagunaanPengelola(int month1, int year1, int month2, int year2) {
        Map<String, Object> result = new LinkedHashMap<>();

        List<Coa> coaPengelola = coaRepository.findByParentAccountId(121L);

        for (Coa coa : coaPengelola) {
            Map<String, Double> monthlyTotals = calculateMonthlyBreakdown(coa.getId(), month1, year1, month2, year2);
            result.put(coa.getAccountName(), monthlyTotals);
        }

        // Menghitung total masing-masing bulan untuk seluruh rentang periode
        LocalDate start = LocalDate.of(year1, month1, 1);
        LocalDate end = LocalDate.of(year2, month2, 1);
        
        while (!start.isAfter(end)) {
            String monthKey = start.getMonth().name() + " " + start.getYear();
            double monthlyTotal = 0.0;
            
            for (Coa coa : coaPengelola) {
                Map<String, Double> monthlyTotals = (Map<String, Double>) result.get(coa.getAccountName());
                monthlyTotal += monthlyTotals.getOrDefault(monthKey, 0.0);
            }
            
            result.put("Total Bulan " + monthKey, monthlyTotal);
            start = start.plusMonths(1);
        }

        return result;
    }

    private Map<String, Object> getPenerimaanDanaPengelola(int month1, int year1, int month2, int year2) {

        Map<String, Object> result = new LinkedHashMap<>();
        Map<String, Double> danaAmilMap = new LinkedHashMap<>();
        Map<String, Double> bagiHasilMap = new LinkedHashMap<>();

        Coa coa4404 = coaRepository.findById(120L)
                .orElseThrow(() -> new RuntimeException("COA 4404 tidak ditemukan"));

        String namaBagiHasil = coa4404.getAccountName();

        LocalDate startDate = LocalDate.of(year1, month1, 1);
        LocalDate endDate = LocalDate.of(year2, month2, 1);

        LocalDate current = startDate;

        while (!current.isAfter(endDate)) {

            LocalDateTime start = current.atStartOfDay();
            LocalDateTime end = current.withDayOfMonth(current.lengthOfMonth()).atTime(LocalTime.MAX);

            Double zakat = transactionRepository.sumZakatByDateRange(start, end);
            Double infak = transactionRepository.sumInfakByDateRange(start, end);
            Double campaign = transactionRepository.sumCampaignByDateRange(start, end);
            Double dskl = transactionRepository.sumDSKLByDateRange(start, end);

            double danaAmil = (zakat * 0.125) +
                    ((infak + campaign) * 0.20) +
                    (dskl * 0.20);

            Double bagiHasil = transactionRepository
                    .sumDebitOrKreditByCoaIdAndParentIdAndDateRange(120L, start, end);

            double bagiHasilFinal = bagiHasil != null ? bagiHasil : 0.0;

            String key = current.getMonth().name() + " " + current.getYear();

            danaAmilMap.put(key, danaAmil);
            bagiHasilMap.put(key, bagiHasilFinal);

            double totalBulanan = danaAmil + bagiHasilFinal;
            result.put("Total Bulan " + key, totalBulanan);

            current = current.plusMonths(1);
        }

        result.put("Penerimaan Dana Amil", danaAmilMap);
        result.put(namaBagiHasil, bagiHasilMap);

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

            Double total = transactionRepository.sumDebitOrKreditByCoaIdAndParentIdAndDateRange(coaId, monthStart,
                    monthEnd);

            String key = current.getMonth().name() + " " + current.getYear();
            monthlyTotals.put(key, total != null ? total : 0.0);

            current = current.plusMonths(1);
        }

        return monthlyTotals;
    }
}
