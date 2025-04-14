package com.lazis.lazissultanagung.service;

import com.lazis.lazissultanagung.dto.request.SaldoAwalRequest;
import com.lazis.lazissultanagung.dto.response.CoaSaldoResponse;
import com.lazis.lazissultanagung.model.Coa;
import com.lazis.lazissultanagung.model.SaldoAwal;
import com.lazis.lazissultanagung.repository.CoaRepository;
import com.lazis.lazissultanagung.repository.SaldoAwalRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;

@Service
public class SaldoAwalService {

    @Autowired
    private SaldoAwalRepository saldoAwalRepository;

    @Autowired
    private CoaRepository coaRepository;

    /**
     * Proses input saldo awal untuk satu COA
     */
    public SaldoAwal inputSaldoAwal(Coa coa, Double debit, Double kredit) {
        // Cek apakah saldo awal untuk COA ini sudah ada
        Optional<SaldoAwal> existingSaldo = saldoAwalRepository.findByCoa(coa);
        if (existingSaldo.isPresent()) {
            throw new IllegalArgumentException("Saldo awal untuk COA ini sudah diinput.");
        }

        // Validasi nilai debit dan kredit
        if ((debit == null || debit <= 0) && (kredit == null || kredit <= 0)) {
            throw new IllegalArgumentException("Debit atau kredit harus memiliki nilai.");
        }
        if (debit != null && kredit != null && debit > 0 && kredit > 0) {
            throw new IllegalArgumentException("Hanya salah satu antara debit atau kredit yang boleh diisi.");
        }

        // Buat entitas SaldoAwal
        SaldoAwal saldoAwal = new SaldoAwal();
        saldoAwal.setCoa(coa);
        saldoAwal.setDebit(debit != null && debit > 0 ? debit : 0);
        saldoAwal.setKredit(kredit != null && kredit > 0 ? kredit : 0);
        saldoAwal.setSaldoAwal(debit != null && debit > 0 ? debit : kredit);
        saldoAwal.setTanggalInput(LocalDate.now().withDayOfMonth(1)); // Set tanggal awal bulan

        return saldoAwalRepository.save(saldoAwal);
    }

    /**
     * Proses batch input saldo awal
     */
    public List<Map<String, Object>> inputBatchSaldoAwal(List<SaldoAwalRequest> requests) {
        List<Map<String, Object>> results = new ArrayList<>();

        for (SaldoAwalRequest request : requests) {
            Map<String, Object> result = new HashMap<>();
            result.put("coaId", request.getCoaId());

            try {
                // Ambil COA berdasarkan ID
                Coa coa = coaRepository.findById(request.getCoaId())
                        .orElseThrow(() -> new IllegalArgumentException("COA tidak ditemukan"));

                // Proses input saldo awal
                inputSaldoAwal(coa, request.getDebit(), request.getKredit());
                result.put("status", "SUCCESS");
            } catch (Exception e) {
                result.put("status", "FAILED");
                result.put("error", e.getMessage());
            }

            results.add(result);
        }

        return results;
    }


    public List<CoaSaldoResponse> getAllCoaWithSaldoAwal() {
        // Ambil semua COA yang memiliki parentAccount != null
        List<Coa> coas = coaRepository.findByParentAccountIsNotNull(Sort.by(Sort.Direction.ASC, "accountCode"));

        // Map COA ke dalam response DTO
        List<CoaSaldoResponse> responses = new ArrayList<>();
        for (Coa coa : coas) {
            // Cari saldo awal berdasarkan COA
            Optional<SaldoAwal> saldoAwal = saldoAwalRepository.findByCoa(coa);

            // Isi response
            CoaSaldoResponse response = new CoaSaldoResponse();
            response.setCoaId(coa.getId());
            response.setAccountCode(coa.getAccountCode());
            response.setAccountName(coa.getAccountName());
            response.setParentAccountName(coa.getParentAccount().getAccountName());
            response.setDebit(saldoAwal.map(SaldoAwal::getDebit).orElse(0.0));
            response.setKredit(saldoAwal.map(SaldoAwal::getKredit).orElse(0.0));
            response.setSaldoAwal(saldoAwal.map(SaldoAwal::getSaldoAwal).orElse(0.0));
            responses.add(response);
        }

        return responses;
    }
}

