package com.lazis.lazissultanagung.service;

import com.lazis.lazissultanagung.model.NomorBuktiSequence;
import com.lazis.lazissultanagung.repository.NomorBuktiSequenceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Service
public class NomorBuktiServiceImpl implements NomorBuktiService {

    @Autowired
    private NomorBuktiSequenceRepository sequenceRepository;

    @Override
    @Transactional
    public String generateNomorBukti() {
        DateTimeFormatter periodFormatter = DateTimeFormatter.ofPattern("yyyy-MM");
        String currentPeriod = LocalDate.now().format(periodFormatter);

        NomorBuktiSequence sequence = sequenceRepository.findByPeriod(currentPeriod)
                .orElse(new NomorBuktiSequence(null, currentPeriod, 0));

        int newNumber = sequence.getLastNumber() + 1;
        sequence.setLastNumber(newNumber);
        sequenceRepository.save(sequence);

        // Format: 1/LAZ/MM/yyyy
        String transactionNumberFormatted = String.valueOf(newNumber);
        String staticPart = "LAZ";
        String datePart = LocalDate.now().format(DateTimeFormatter.ofPattern("MM/yyyy"));

        return transactionNumberFormatted + "/" + staticPart + "/" + datePart;
    }
}
