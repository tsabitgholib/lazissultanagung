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

        // Perhitungan Surplus/Defisit, Saldo Awal, dan Saldo Akhir
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

        // Saldo Awal Dana Pengelola (COA ID 50)
        double saldoAwalDanaPengelola = 0.0;
        current = startDate;

        while (!current.isAfter(endDate)) {
            int month = current.getMonthValue();
            int year = current.getYear();

            Optional<SaldoAwal> saldoAwalOpt = saldoAwalRepository.findSaldoAwalByCoaAndMonthAndYear(50L, month, year);
            if (saldoAwalOpt.isPresent()) {
                saldoAwalDanaPengelola = saldoAwalOpt.get().getSaldoAwal();
            } else {
                String keyPrev = "Saldo Akhir Dana " + current.minusMonths(1).getMonth().name() + " " + current.minusMonths(1).getYear();
                if (response.containsKey(keyPrev) && response.get(keyPrev) != null) {
                    saldoAwalDanaPengelola = (double) response.get(keyPrev);
                } else {
                    LocalDate prevDate = current.minusMonths(1);
                    Optional<SaldoAkhir> prevSaldoAkhir = saldoAkhirRepository.findByCoaAndMonthAndYear(
                            coaRepository.findById(50L).orElse(null),
                            prevDate.getMonthValue(),
                            prevDate.getYear()
                    );
                    saldoAwalDanaPengelola = prevSaldoAkhir.map(SaldoAkhir::getSaldoAkhir).orElse(0.0);
                }
            }

            response.put("Saldo Awal Dana " + current.getMonth().name() + " " + year, saldoAwalDanaPengelola);

            double surplusDefisit = surplusDefisitPerBulan.getOrDefault(current.getMonth().name() + " " + year, 0.0);
            double saldoAkhirDanaPengelola = saldoAwalDanaPengelola + surplusDefisit;

            // Simpan ke database
            Optional<SaldoAkhir> existingSaldoAkhir = saldoAkhirRepository.findByCoa_IdAndMonthAndYear(
                    50L, month, year);
            SaldoAkhir entity = existingSaldoAkhir.orElse(new SaldoAkhir());
            entity.setCoa(coaRepository.findById(50L).orElse(null));
            entity.setMonth(month);
            entity.setYear(year);
            entity.setSaldoAkhir(saldoAkhirDanaPengelola);
            saldoAkhirRepository.save(entity);

            response.put("Saldo Akhir Dana " + current.getMonth().name() + " " + year, saldoAkhirDanaPengelola);
            current = current.plusMonths(1);
        }

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

            String key = coa.getAccountName();
            result.put(key, monthlyTotals);

            totalMonth1 += monthlyTotals.getOrDefault(month1Name, 0.0);
            totalMonth2 += monthlyTotals.getOrDefault(month2Name, 0.0);
        }

        result.put("Total Bulan " + month1Name, totalMonth1);
        result.put("Total Bulan " + month2Name, totalMonth2);

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
        LocalDate endDate = LocalDate.of(year2, month2, 1)
                .plusMonths(1)
                .minusDays(1);

        LocalDate current = startDate;

        double totalMonth1 = 0.0;
        double totalMonth2 = 0.0;

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

            if (current.getMonthValue() == month1 && current.getYear() == year1) {
                totalMonth1 = totalBulanan;
            }

            if (current.getMonthValue() == month2 && current.getYear() == year2) {
                totalMonth2 = totalBulanan;
            }

            current = current.plusMonths(1);
        }

        result.put("Penerimaan Dana Amil", danaAmilMap);
        result.put(namaBagiHasil, bagiHasilMap);
        result.put("Total Bulan " + Month.of(month1).name() + " " + year1, totalMonth1);
        result.put("Total Bulan " + Month.of(month2).name() + " " + year2, totalMonth2);

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
