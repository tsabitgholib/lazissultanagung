package com.lazis.lazissultanagung.service;

import com.lazis.lazissultanagung.config.BukuBesarWrapper;
import com.lazis.lazissultanagung.dto.response.BukuBesarResponse;
import com.lazis.lazissultanagung.model.SaldoAwal;
import com.lazis.lazissultanagung.model.Transaction;
import com.lazis.lazissultanagung.repository.CoaRepository;
import com.lazis.lazissultanagung.repository.SaldoAwalRepository;
import com.lazis.lazissultanagung.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class BukuBesarService {

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private SaldoAwalRepository saldoAwalRepository;

    @Autowired
    private CoaRepository coaRepository;

    public BukuBesarWrapper getBukuBesar(Long coaId1, Long coaId2, LocalDate startDate, LocalDate endDate) {
        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.atTime(23, 59, 59);

        BukuBesarWrapper result = new BukuBesarWrapper();

        if (coaId1 != null) {
            double saldoAwal1 = calculateSaldoAwal(coaId1, startDate);
            List<Transaction> transactions1 = transactionRepository.findByCoaIdAndTransactionDateBetween(coaId1, startDateTime, endDateTime);
            List<BukuBesarResponse> bukuBesarCoa1 = processTransactions(transactions1, saldoAwal1);

            result.setSaldoAwal1(saldoAwal1);
            result.setBukuBesarCoa1(bukuBesarCoa1);
        }

        if (coaId2 != null) {
            double saldoAwal2 = calculateSaldoAwalKredit(coaId2, startDate);
            List<Transaction> transactions2 = transactionRepository.findByCoaIdAndTransactionDateBetween(coaId2, startDateTime, endDateTime);
            List<BukuBesarResponse> bukuBesarCoa2 = processTransactionsKredit(transactions2, saldoAwal2);

            result.setSaldoAwal2(saldoAwal2);
            result.setBukuBesarCoa2(bukuBesarCoa2);
        }

        return result;
    }

    private List<BukuBesarResponse> processTransactions(List<Transaction> transactions, double saldoAwal) {
        double saldoIncrement = saldoAwal;
        List<BukuBesarResponse> bukuBesarList = new ArrayList<>();

        for (Transaction transaction : transactions) {
            BukuBesarResponse response = new BukuBesarResponse();
            response.setTanggal(transaction.getTransactionDate().toLocalDate());
            response.setUnit("Lazis Sultan Agung");
            response.setNomorBukti(transaction.getNomorBukti());
            response.setUraian(getUraian(transaction));
            response.setDebit(transaction.getDebit());
            response.setKredit(transaction.getKredit());
            saldoIncrement += transaction.getDebit() - transaction.getKredit();
            response.setSaldo(saldoIncrement);
            bukuBesarList.add(response);
        }
        return bukuBesarList;
    }

    private List<BukuBesarResponse> processTransactionsKredit(List<Transaction> transactions, double saldoAwal) {
        double saldoIncrement = saldoAwal;
        List<BukuBesarResponse> bukuBesarList = new ArrayList<>();

        for (Transaction transaction : transactions) {
            BukuBesarResponse response = new BukuBesarResponse();
            response.setTanggal(transaction.getTransactionDate().toLocalDate());
            response.setUnit("Lazis Sultan Agung");
            response.setNomorBukti(transaction.getNomorBukti());
            response.setUraian(getUraian(transaction));
            response.setDebit(transaction.getDebit());
            response.setKredit(transaction.getKredit());
            saldoIncrement += transaction.getKredit() - transaction.getDebit();
            response.setSaldo(saldoIncrement);
            bukuBesarList.add(response);
        }
        return bukuBesarList;
    }


    private double calculateSaldoAwal(Long coaId, LocalDate startDate) {
        // Awal bulan dari tanggal filter (startDate)
        LocalDate startOfMonth = startDate.withDayOfMonth(1);

        // Akhir bulan sebelumnya
        LocalDate endOfPreviousMonth = startOfMonth.minusDays(1);

        // Ambil saldo awal dari tabel saldo_awal untuk coaId
        Optional<SaldoAwal> saldoAwalOpt = saldoAwalRepository.findByCoa(coaRepository.findById(coaId).orElseThrow());

        // Jika saldo awal belum diinput, mulai dengan saldo awal 0
        double saldoAwal = saldoAwalOpt.map(SaldoAwal::getSaldoAwal).orElse(0.0);

        // Ambil semua transaksi hingga akhir bulan sebelum startDate
        List<Transaction> previousTransactions = transactionRepository.findByCoaIdAndTransactionDateBetween(
                coaId,
                saldoAwalOpt.isPresent() ?
                        saldoAwalOpt.get().getTanggalInput().withDayOfMonth(1).atStartOfDay() :
                        LocalDate.of(1900, 1, 1).atStartOfDay(), // Jika tidak ada saldo awal, mulai dari tanggal sangat lama
                endOfPreviousMonth.atTime(23, 59, 59)
        );

        // Tambahkan semua transaksi ke saldo awal
        for (Transaction transaction : previousTransactions) {
            saldoAwal += transaction.getDebit() - transaction.getKredit();
        }

        return saldoAwal;
    }

    private double calculateSaldoAwalKredit(Long coaId, LocalDate startDate) {
        // Awal bulan dari tanggal filter (startDate)
        LocalDate startOfMonth = startDate.withDayOfMonth(1);

        // Akhir bulan sebelumnya
        LocalDate endOfPreviousMonth = startOfMonth.minusDays(1);

        // Ambil saldo awal dari tabel saldo_awal untuk coaId
        Optional<SaldoAwal> saldoAwalOpt = saldoAwalRepository.findByCoa(coaRepository.findById(coaId).orElseThrow());

        // Jika saldo awal belum diinput, mulai dengan saldo awal 0
        double saldoAwal = saldoAwalOpt.map(SaldoAwal::getSaldoAwal).orElse(0.0);

        // Ambil semua transaksi hingga akhir bulan sebelum startDate
        List<Transaction> previousTransactions = transactionRepository.findByCoaIdAndTransactionDateBetween(
                coaId,
                saldoAwalOpt.isPresent() ?
                        saldoAwalOpt.get().getTanggalInput().withDayOfMonth(1).atStartOfDay() :
                        LocalDate.of(1900, 1, 1).atStartOfDay(), // Jika tidak ada saldo awal, mulai dari tanggal sangat lama
                endOfPreviousMonth.atTime(23, 59, 59)
        );

        // Tambahkan semua transaksi ke saldo awal
        for (Transaction transaction : previousTransactions) {
            saldoAwal += transaction.getKredit() - transaction.getDebit();
        }

        return saldoAwal;
    }


    private String getUraian(Transaction transaction) {
        if (transaction.getCampaign() != null) {
            return "Penerimaan Dana Campaign - " + transaction.getCampaign().getCampaignName();
        } else if (transaction.getInfak() != null) {
            return "Penerimaan Dana Infak - " + transaction.getInfak().getCategoryName();
        } else if (transaction.getZakat() != null) {
            return "Penerimaan Dana Zakat - " + transaction.getZakat().getCategoryName();
        } else if (transaction.getWakaf() != null) {
            return "Penerimaan Dana Wakaf - " + transaction.getWakaf().getCategoryName();
        } else if (transaction.getDskl() != null) {
            return "Penerimaan Dana DSKL - " + transaction.getDskl().getCategoryName();
        } else {
            return "Uraian tidak tersedia";
        }
    }
}
