package com.lazis.lazissultanagung.service;

import com.lazis.lazissultanagung.model.NomorBuktiSequence;
import com.lazis.lazissultanagung.repository.NomorBuktiSequenceRepository;
import com.lazis.lazissultanagung.repository.TransactionRepository;
import com.lazis.lazissultanagung.repository.TemporaryTransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Service
public class NomorBuktiServiceImpl implements NomorBuktiService {

    @Autowired
    private NomorBuktiSequenceRepository sequenceRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private TemporaryTransactionRepository temporaryTransactionRepository;

    @Override
    @Transactional
    public String generateNomorBukti() {
        DateTimeFormatter periodFormatter = DateTimeFormatter.ofPattern("MM/yyyy");
        String currentPeriod = LocalDate.now().format(periodFormatter);

        // 1. Get the last number from the sequence table
        NomorBuktiSequence sequence = sequenceRepository.findByPeriod(currentPeriod)
                .orElse(new NomorBuktiSequence(null, currentPeriod, 0));
        int sequenceNumber = sequence.getLastNumber();

        // 2. Get the last number from the actual transaction tables
        String lastNomorBuktiFromTx = transactionRepository.findLastNomorBuktiByPeriod(currentPeriod).orElse("0");
        String lastNomorBuktiFromTemp = temporaryTransactionRepository.findLastNomorBuktiByPeriod(currentPeriod).orElse("0");

        int maxTxNumber = 0;
        try {
            maxTxNumber = Integer.parseInt(lastNomorBuktiFromTx.split("/")[0]);
        } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
            // Handle cases where the format is unexpected or empty
            maxTxNumber = 0;
        }

        int maxTempNumber = 0;
        try {
            maxTempNumber = Integer.parseInt(lastNomorBuktiFromTemp.split("/")[0]);
        } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
            maxTempNumber = 0;
        }

        // 3. Determine the highest number
        int highestNumber = Math.max(sequenceNumber, Math.max(maxTxNumber, maxTempNumber));

        // 4. Generate the new number
        int newNumber = highestNumber + 1;

        // 5. Update the sequence table for future use
        sequence.setLastNumber(newNumber);
        sequenceRepository.save(sequence);

        // Format: 1/LAZ/MM/yyyy
        return newNumber + "/" + "LAZ" + "/" + currentPeriod;
    }
}
