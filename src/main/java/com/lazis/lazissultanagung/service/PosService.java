package com.lazis.lazissultanagung.service;

import com.lazis.lazissultanagung.dto.request.PosTransactionRequest;
import com.lazis.lazissultanagung.dto.response.PosDashboardResponse;
import com.lazis.lazissultanagung.dto.response.PosHistoryResponse;
import com.lazis.lazissultanagung.dto.response.PosTransactionResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;

public interface PosService {
    PosTransactionResponse createPosTransaction(PosTransactionRequest request, Long agenId);

    Page<PosHistoryResponse> getPosHistory(Long agenId, Long eventId, LocalDate startDate, LocalDate endDate, String category, String paymentMethod, String search, Pageable pageable);
    List<PosHistoryResponse> getPosHistoryList(Long agenId, Long eventId, LocalDate startDate, LocalDate endDate, String category, String paymentMethod, String search);

    PosDashboardResponse getPosDashboard(Long agenId);

    byte[] downloadImportTemplate();
    void importTransactionsFromExcel(MultipartFile file, Long agenId);
    List<PosHistoryResponse> getDistinctDonaturPos(String search);
}
