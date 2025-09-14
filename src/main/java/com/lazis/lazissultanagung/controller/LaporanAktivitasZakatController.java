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
import java.util.*;

@CrossOrigin(origins = {"*"}, maxAge = 4800, allowCredentials = "false")
@RestController
@RequestMapping("/api/journal")
public class LaporanAktivitasZakatController {

    @Autowired
    private SaldoAwalRepository saldoAwalRepository;

    @Autowired
    private CoaRepository coaRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private SaldoAkhirRepository saldoAkhirRepository;

    @GetMapping("/zakat-activity-report")
    public ResponseEntity<Map<String, Object>> getZakatActivityReport(
            @RequestParam("month1") int month1,
            @RequestParam("year1") int year1,
            @RequestParam("month2") int month2,
            @RequestParam("year2") int year2
    ) {
        Map<String, Object> response = new LinkedHashMap<>();

        // Penerimaan Dana Zakat (COA ID 52)
        Map<String, Object> penerimaanZakatDetails = getCoaDetailsWithTransactions(52L, month1, year1, month2, year2);
        response.put("Penerimaan Dana", penerimaanZakatDetails);

        // Pendayagunaan Dana Zakat (COA ID 56)
        Map<String, Object> pendayagunaanZakatDetails = getCoaDetailsWithTransactions(56L, month1, year1, month2, year2);
        response.put("Pendayagunaan Dana", pendayagunaanZakatDetails);

        // Variabel untuk menyimpan surplus/defisit per bulan
        Map<String, Double> surplusDefisitPerBulan = new LinkedHashMap<>();

        // Loop untuk menghitung surplus/defisit per bulan (NOVEMBER, DECEMBER)
        for (int month = month1; month <= month2; month++) {
            String monthName = LocalDate.of(year1, month, 1).getMonth().name();

            // Mendapatkan total penerimaan dan pendayagunaan per bulan
            Double totalPenerimaan = (Double) penerimaanZakatDetails.get("Total Bulan " + monthName + " " + (month == month2 ? year2 : year1));
            Double totalPendayagunaan = (Double) pendayagunaanZakatDetails.get("Total Bulan " + monthName + " " + (month == month2 ? year2 : year1));

            if (totalPenerimaan == null) totalPenerimaan = 0.0;
            if (totalPendayagunaan == null) totalPendayagunaan = 0.0;

            // Menghitung surplus/defisit untuk bulan ini
            double surplusDefisit = totalPenerimaan - totalPendayagunaan;
            surplusDefisitPerBulan.put(monthName + " " + (month == month2 ? year2 : year1), surplusDefisit);

            response.put("Surplus (Defisit) Dana " + monthName + " " + (month == month2 ? year2 : year1), surplusDefisit);
        }

        // Saldo Awal Dana Zakat (COA ID 45)
        double saldoAwalDanaZakat = 0.0;
        LocalDate currentDate = LocalDate.of(year1, month1, 1);

        while (currentDate.getYear() < year2 || (currentDate.getYear() == year2 && currentDate.getMonthValue() <= month2)) {
            int month = currentDate.getMonthValue();
            int year = currentDate.getYear();

            Optional<SaldoAwal> saldoAwalOpt = saldoAwalRepository.findSaldoAwalByCoaAndMonthAndYear(45L, month, year);
            if (saldoAwalOpt.isPresent()) {
                saldoAwalDanaZakat = saldoAwalOpt.get().getSaldoAwal();
            } else {
                String key = "Saldo Akhir Dana " + currentDate.minusMonths(1).getMonth().name() + " " + currentDate.minusMonths(1).getYear();
                if (response.containsKey(key) && response.get(key) != null) {
                    saldoAwalDanaZakat = (double) response.get(key);
                } else {
                    saldoAwalDanaZakat = 0.0; // Default jika tidak ditemukan
                }
            }

            response.put("Saldo Awal Dana " + currentDate.getMonth().name() + " " + year, saldoAwalDanaZakat);

            // Mengambil surplus/defisit untuk bulan ini
            String monthName = currentDate.getMonth().name();
            double surplusDefisit = surplusDefisitPerBulan.getOrDefault(monthName + " " + (month == month2 ? year2 : year1), 0.0);

            // Menghitung saldo akhir Dana Zakat bulan ini
            double saldoAkhirDanaZakat = surplusDefisit >= 0
                    ? saldoAwalDanaZakat + surplusDefisit
                    : saldoAwalDanaZakat - surplusDefisit;

            // Menyimpan saldo akhir ke tabel SaldoAkhirDanaZakat
            Optional<SaldoAkhir> existingSaldoAkhir = saldoAkhirRepository.findByCoaAndMonthAndYear(coaRepository.findById(45L).orElse(null), month, year);
            SaldoAkhir saldoAkhirDanaZakatEntity = existingSaldoAkhir.orElse(new SaldoAkhir());
            saldoAkhirDanaZakatEntity.setCoa(coaRepository.findById(45L).orElse(null));
            saldoAkhirDanaZakatEntity.setMonth(month);
            saldoAkhirDanaZakatEntity.setYear(year);
            saldoAkhirDanaZakatEntity.setSaldoAkhir(saldoAkhirDanaZakat);
            saldoAkhirRepository.save(saldoAkhirDanaZakatEntity);

            response.put("Saldo Akhir Dana " + currentDate.getMonth().name() + " " + year, saldoAkhirDanaZakat);

            currentDate = currentDate.plusMonths(1);
        }

        return ResponseEntity.ok(response);
    }




    // Helper method to get details of COA with transactions
    private Map<String, Object> getCoaDetailsWithTransactions(Long coaId, int month1, int year1, int month2, int year2) {
        Map<String, Object> details = new LinkedHashMap<>();
        List<Coa> childCoas = coaRepository.findByParentAccount_IdIn(List.of(coaId));

        double month1Total = 0.0;
        double month2Total = 0.0;

        // Menyimpan nama bulan untuk digunakan di akhir
        String month1Name = Month.of(month1).name() + " " + year1;
        String month2Name = Month.of(month2).name() + " " + year2;

        for (Coa childCoa : childCoas) {
            // Menghitung breakdown per bulan untuk bulan 1 dan bulan 2
            Map<String, Double> monthlyTotals = calculateMonthlyBreakdown(childCoa.getId(), month1, year1, month2, year2);

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

        // Memasukkan total masing-masing bulan ke dalam details dengan format yang lebih deskriptif
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
            Double totalDebit = transactionRepository.sumDebitOrKreditByCoaIdAndParentIdAndDateRange(coaId, monthStart, monthEnd);

            monthlyTotals.put(current.getMonth().name() + " " + current.getYear(), totalDebit != null ? totalDebit : 0.0);

            current = current.plusMonths(1);
        }

        return monthlyTotals;
    }
}

