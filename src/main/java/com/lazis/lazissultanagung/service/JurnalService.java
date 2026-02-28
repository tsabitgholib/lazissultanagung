package com.lazis.lazissultanagung.service;

import com.lazis.lazissultanagung.dto.response.*;
import com.lazis.lazissultanagung.model.Transaction;
import com.lazis.lazissultanagung.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class JurnalService {


    @Autowired
    private TransactionRepository transactionRepository;



    public JurnalResponseWrapper getJurnalFilterDate(LocalDate startDate, LocalDate endDate) {
        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.atTime(23, 59, 59);

        List<Transaction> transactions = transactionRepository.findAllByTransactionDateBetween(startDateTime, endDateTime);

        Map<String, JurnalResponse> jurnalMap = new LinkedHashMap<>(); // Gunakan LinkedHashMap untuk menjaga urutan
        double totalDebitKeseluruhan = 0.0;
        double totalKreditKeseluruhan = 0.0;

        for (Transaction transaction : transactions) {
            // Gunakan nomor bukti sebagai kunci unik untuk JurnalResponse
            String nomorBukti = transaction.getNomorBukti();
            JurnalResponse jurnalDTO = jurnalMap.get(nomorBukti);

            if (jurnalDTO == null) {
                // Jika belum, buat JurnalResponse baru
                jurnalDTO = new JurnalResponse();
                jurnalDTO.setTanggal(transaction.getTransactionDate().toLocalDate());
                jurnalDTO.setUnit("Lazis Sultan Agung");
                jurnalDTO.setNomorBukti(nomorBukti);

                // Set uraian berdasarkan foreign key yang ada
                if (transaction.getCampaign() != null) {
                    jurnalDTO.setUraian("Penerimaan Dana Campaign - " + transaction.getCampaign().getCampaignName());
                } else if (transaction.getInfak() != null) {
                    jurnalDTO.setUraian("Penerimaan Dana Infak - " + transaction.getInfak().getCategoryName());
                } else if (transaction.getZakat() != null) {
                    jurnalDTO.setUraian("Penerimaan Dana Zakat - " + transaction.getZakat().getCategoryName());
                } else if (transaction.getWakaf() != null) {
                    jurnalDTO.setUraian("Penerimaan Dana Wakaf - " + transaction.getWakaf().getCategoryName());
                } else if (transaction.getDskl() != null) {
                    jurnalDTO.setUraian("Penerimaan Dana DSKL - " + transaction.getDskl().getCategoryName());
                } else {
                    jurnalDTO.setUraian(" ");
                }
                jurnalDTO.setCoa(new ArrayList<>());
                jurnalDTO.setJumlahDebit(0);
                jurnalDTO.setJumlahKredit(0);
                jurnalDTO.setTotalDebit(0.0);
                jurnalDTO.setTotalKredit(0.0);
                jurnalMap.put(nomorBukti, jurnalDTO);
            }

            // Buat COAResponse untuk setiap transaksi
            COAResponse coaResponse = new COAResponse();
            coaResponse.setAkun(transaction.getCoa().getAccountCode() + " - " + transaction.getCoa().getAccountName());
            coaResponse.setDebit(transaction.getDebit());
            coaResponse.setKredit(transaction.getKredit());

            // Tambahkan COA ke dalam list COA di JurnalResponse
            jurnalDTO.getCoa().add(coaResponse);

            // Update total debit dan kredit di JurnalResponse
            jurnalDTO.setTotalDebit(jurnalDTO.getTotalDebit() + transaction.getDebit());
            jurnalDTO.setTotalKredit(jurnalDTO.getTotalKredit() + transaction.getKredit());

            // Update jumlah debit dan kredit di JurnalResponse
            if (transaction.getDebit() > 0) {
                jurnalDTO.setJumlahDebit(jurnalDTO.getJumlahDebit() + 1);
            }
            if (transaction.getKredit() > 0) {
                jurnalDTO.setJumlahKredit(jurnalDTO.getJumlahKredit() + 1);
            }

            // Update total debit dan kredit keseluruhan
            totalDebitKeseluruhan += transaction.getDebit();
            totalKreditKeseluruhan += transaction.getKredit();
        }

        // Menyusun response wrapper
        JurnalResponseWrapper responseWrapper = new JurnalResponseWrapper();
        responseWrapper.setJurnalResponses(new ArrayList<>(jurnalMap.values()));
        responseWrapper.setTotalDebitKeseluruhan(totalDebitKeseluruhan);
        responseWrapper.setTotalKreditKeseluruhan(totalKreditKeseluruhan);

        return responseWrapper;
    }


//    public List<JurnalResponse> getAllJurnal() {
//        // Ambil semua transaksi
//        List<Transaction> transactions = transactionRepository.findAll();
//
//        // Urutkan transaksi berdasarkan id atau nomor bukti
//        transactions.sort(Comparator.comparing(Transaction::getTransactionId)); // Urutkan berdasarkan ID transaksi
//
//        Map<String, JurnalResponse> jurnalMap = new LinkedHashMap<>(); // Gunakan LinkedHashMap untuk menjaga urutan
//
//        for (Transaction transaction : transactions) {
//            // Cek apakah transaksi ini sudah ada dalam ringkasan berdasarkan nomor bukti
//            JurnalResponse jurnalDTO = jurnalMap.get(transaction.getNomorBukti());
//
//            if (jurnalDTO == null) {
//                // Jika belum, buat entry baru
//                jurnalDTO = new JurnalResponse();
//                jurnalDTO.setTanggal(transaction.getTransactionDate().toLocalDate());
//                jurnalDTO.setUnit("Lazis Sultan Agung");
//                jurnalDTO.setNomorBukti(transaction.getNomorBukti());
//
//                // Set uraian berdasarkan foreign key yang ada
//                if (transaction.getCampaign() != null) {
//                    jurnalDTO.setUraian("Penerimaan Dana Campaign - " + transaction.getCampaign().getCampaignName());
//                } else if (transaction.getInfak() != null) {
//                    jurnalDTO.setUraian("Penerimaan Dana Infak - " + transaction.getInfak().getCategoryName());
//                } else if (transaction.getZakat() != null) {
//                    jurnalDTO.setUraian("Penerimaan Dana Zakat - " + transaction.getZakat().getCategoryName());
//                } else if (transaction.getWakaf() != null) {
//                    jurnalDTO.setUraian("Penerimaan Dana Wakaf - " + transaction.getWakaf().getCategoryName());
//                } else if (transaction.getDskl() != null) {
//                    jurnalDTO.setUraian("Penerimaan Dana DSKL - " + transaction.getDskl().getCategoryName());
//                } else {
//                    jurnalDTO.setUraian("Uraian tidak tersedia");
//                }
//
//                jurnalMap.put(transaction.getNomorBukti(), jurnalDTO);
//            }
//
//            // Cek apakah ini debit atau kredit, dan tambahkan ke COA yang sesuai
//            if (transaction.getDebit() > 0) {
//                jurnalDTO.setCoaDebit(transaction.getCoa().getAccountCode() + " - " + transaction.getCoa().getAccountName());
//                jurnalDTO.setDebit(transaction.getDebit());
//            } else if (transaction.getKredit() > 0) {
//                jurnalDTO.setCoaKredit(transaction.getCoa().getAccountCode() + " - " + transaction.getCoa().getAccountName());
//                jurnalDTO.setKredit(transaction.getKredit());
//            }
//        }
//
//        // Return list sesuai urutan transaksi
//        return new ArrayList<>(jurnalMap.values());
//    }

//    public Map<String, Object> getNeracaComparisonHierarchical(int month1, int year1, int month2, int year2) {
//        Map<String, Object> response = new HashMap<>();
//
//        // Ambil semua COA
//        List<Coa> coaList = coaRepository.findAll();
//
//        // Buat peta saldo per akun untuk kedua bulan
//        LocalDateTime startDate1 = LocalDateTime.of(year1, month1, 1, 0, 0);
//        LocalDateTime endDate1 = startDate1.with(TemporalAdjusters.lastDayOfMonth()).with(LocalTime.MAX);
//        LocalDateTime startDate2 = LocalDateTime.of(year2, month2, 1, 0, 0);
//        LocalDateTime endDate2 = startDate2.with(TemporalAdjusters.lastDayOfMonth()).with(LocalTime.MAX);
//
//        List<Transaction> transactions1 = transactionRepository.findByTransactionDateBetween(startDate1, endDate1);
//        List<Transaction> transactions2 = transactionRepository.findByTransactionDateBetween(startDate2, endDate2);
//
//        Map<String, Double> coaBalances1 = calculateCoaBalances(transactions1);
//        Map<String, Double> coaBalances2 = calculateCoaBalances(transactions2);
//
//        // Bangun struktur hierarkis
//        List<Map<String, Object>> hierarchicalAccounts = buildHierarchy(coaList, coaBalances1, coaBalances2, null);
//
//        response.put("accounts", hierarchicalAccounts);
//        return response;
//    }
//
//    private List<Map<String, Object>> buildHierarchy(List<Coa> coaList, Map<String, Double> balances1, Map<String, Double> balances2, Coa parent) {
//        List<Map<String, Object>> result = new ArrayList<>();
//
//        for (Coa coa : coaList) {
//            if ((parent == null && coa.getParentAccount() == null) || (parent != null && coa.getParentAccount() != null && coa.getParentAccount().getId().equals(parent.getId()))) {
//                Map<String, Object> accountData = new HashMap<>();
//                accountData.put("accountCode", coa.getAccountCode());
//                accountData.put("accountName", coa.getAccountName());
//
//                // Cari anak-anak akun ini
//                List<Map<String, Object>> children = buildHierarchy(coaList, balances1, balances2, coa);
//
//                // Hitung saldo akun ini
//                double balanceMonth1 = balances1.getOrDefault(coa.getAccountCode(), 0.0);
//                double balanceMonth2 = balances2.getOrDefault(coa.getAccountCode(), 0.0);
//
//                // Tambahkan saldo anak-anak (jika ada)
//                for (Map<String, Object> child : children) {
//                    balanceMonth1 += (double) child.get("balanceMonth1");
//                    balanceMonth2 += (double) child.get("balanceMonth2");
//                }
//
//                accountData.put("balanceMonth1", balanceMonth1);
//                accountData.put("balanceMonth2", balanceMonth2);
//
//                // Tambahkan anak-anak ke data akun ini
//                if (!children.isEmpty()) {
//                    accountData.put("children", children);
//                }
//
//                result.add(accountData);
//            }
//        }
//
//        return result;
//    }
//
//
//    private Map<String, Double> calculateCoaBalances(List<Transaction> transactions) {
//        Map<String, Double> balances = new HashMap<>();
//        for (Transaction transaction : transactions) {
//            String accountCode = transaction.getCoa().getAccountCode();
//            double balance = transaction.getDebit() - transaction.getKredit();
//            balances.put(accountCode, balances.getOrDefault(accountCode, 0.0) + balance);
//        }
//        return balances;
//    }
//
//    public List<Map<String, Object>> getDetailedAccountHierarchy(LocalDate startDate, LocalDate endDate) {
//        List<Coa> allCoa = coaRepository.findAll();
//
//        // Fetch all saldo awal in the range
//        Map<Long, SaldoAwal> saldoAwalMap = saldoAwalRepository.findAllByTanggalInputBetween(startDate, endDate).stream()
//                .collect(Collectors.toMap(s -> s.getCoa().getId(), s -> s));
//
//        // Fetch all transactions in the range
//        List<Transaction> transactions = transactionRepository.findAllByTransactionDateBetween(startDate.atStartOfDay(), endDate.atTime(23, 59, 59));
//
//        // Group transactions by COA
//        Map<Long, List<Transaction>> transactionMap = transactions.stream()
//                .collect(Collectors.groupingBy(t -> t.getCoa().getId()));
//
//        return buildDetailedHierarchy(allCoa, saldoAwalMap, transactionMap, null);
//    }
//
//    private List<Map<String, Object>> buildDetailedHierarchy(List<Coa> coaList, Map<Long, SaldoAwal> saldoAwalMap,
//                                                             Map<Long, List<Transaction>> transactionMap, Coa parent) {
//        List<Map<String, Object>> result = new ArrayList<>();
//
//        for (Coa coa : coaList) {
//            if ((parent == null && coa.getParentAccount() == null) || (parent != null && coa.getParentAccount() != null && coa.getParentAccount().getId().equals(parent.getId()))) {
//                Map<String, Object> accountData = new HashMap<>();
//                accountData.put("accountCode", coa.getAccountCode());
//                accountData.put("accountName", coa.getAccountName());
//
//                // Get saldo awal for this COA
//                SaldoAwal saldoAwal = saldoAwalMap.getOrDefault(coa.getId(), new SaldoAwal());
//                double initialBalance = saldoAwal.getSaldoAwal();
//                double totalDebit = saldoAwal.getDebit();
//                double totalKredit = saldoAwal.getKredit();
//
//                // Add transactions to debit/kredit
//                List<Transaction> transactions = transactionMap.getOrDefault(coa.getId(), new ArrayList<>());
//                for (Transaction transaction : transactions) {
//                    totalDebit += transaction.getDebit();
//                    totalKredit += transaction.getKredit();
//                }
//
//                double finalBalance = initialBalance + totalDebit - totalKredit;
//
//                accountData.put("initialBalance", initialBalance);
//                accountData.put("totalDebit", totalDebit);
//                accountData.put("totalKredit", totalKredit);
//                accountData.put("finalBalance", finalBalance);
//
//                // Find children
//                List<Map<String, Object>> children = buildDetailedHierarchy(coaList, saldoAwalMap, transactionMap, coa);
//                if (!children.isEmpty()) {
//                    accountData.put("children", children);
//                }
//
//                result.add(accountData);
//            }
//        }
//
//        return result;
//    }

    //LAPORAN AKTIVITAS
    public LaporanAktivitasWithTotalResponse getLaporanAktivitasKeuangan(String jenis, int month1, int year1, int month2, int year2) {
        LocalDate startDate1 = LocalDate.of(year1, month1, 1);
        LocalDateTime startMonth1 = startDate1.atStartOfDay(); // Awal bulan pertama
        LocalDateTime endMonth1 = startDate1.withDayOfMonth(startDate1.lengthOfMonth()).atTime(23, 59, 59); // Akhir bulan pertama

        LocalDate startDate2 = LocalDate.of(year2, month2, 1);
        LocalDateTime startMonth2 = startDate2.atStartOfDay(); // Awal bulan kedua
        LocalDateTime endMonth2 = startDate2.withDayOfMonth(startDate2.lengthOfMonth()).atTime(23, 59, 59); // Akhir bulan kedua

        // Panggil repository
        List<Object[]> result = transactionRepository.getAktivitasKeuangan(jenis, startMonth1, endMonth1, startMonth2, endMonth2);

        // Format data menjadi response
        List<LaporanAktivitasResponse> responseList = new ArrayList<>();
        double totalMonth1 = 0.0;
        double totalMonth2 = 0.0;

        for (Object[] row : result) {
            LaporanAktivitasResponse response = new LaporanAktivitasResponse();
            response.setAccountCode((String) row[0]);
            response.setAccountName((String) row[1]);
            response.setParentAccountCode((String) row[4]);
            response.setParentAccountName((String) row[5]);
            double month1Total = row[2] != null ? (Double) row[2] : 0.0;
            double month2Total = row[3] != null ? (Double) row[3] : 0.0;

            response.setTotalMonth1(month1Total);
            response.setTotalMonth2(month2Total);

            // Tambahkan ke total keseluruhan
            totalMonth1 += month1Total;
            totalMonth2 += month2Total;

            responseList.add(response);
        }

        // Bungkus dengan total keseluruhan
        LaporanAktivitasWithTotalResponse finalResponse = new LaporanAktivitasWithTotalResponse();
        finalResponse.setAktivitas(responseList);
        finalResponse.setFinalTotalMonth1(totalMonth1);
        finalResponse.setFinalTtotalMonth2(totalMonth2);

        return finalResponse;
    }
}
