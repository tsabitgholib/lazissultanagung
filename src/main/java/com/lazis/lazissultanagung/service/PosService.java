package com.lazis.lazissultanagung.service;

import java.time.LocalDate;
import com.lazis.lazissultanagung.dto.request.PosTransactionRequest;
import com.lazis.lazissultanagung.dto.response.PosHistoryResponse;
import com.lazis.lazissultanagung.dto.response.PosTransactionResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;

public interface PosService {
    PosTransactionResponse createPosTransaction(PosTransactionRequest request, Long agenId);
    
    Page<PosHistoryResponse> getPosHistory(Long agenId, Long eventId, LocalDate startDate, LocalDate endDate, String category, String paymentMethod, Pageable pageable);
}
