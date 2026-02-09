package com.lazis.lazissultanagung.service;

import com.lazis.lazissultanagung.dto.request.JurnalUmumRequest;
import com.lazis.lazissultanagung.dto.request.TransactionRequest;
import com.lazis.lazissultanagung.dto.response.ResponseMessage;
import com.lazis.lazissultanagung.dto.response.TransactionResponse;
import com.lazis.lazissultanagung.dto.response.DonaturTransactionsHistoryResponse;
import com.lazis.lazissultanagung.exception.BadRequestException;
import com.lazis.lazissultanagung.model.Transaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;

import java.util.List;
import java.util.Map;

public interface TransactionService {

    Page<TransactionResponse> getAllTransaction(Integer month, Integer year, Pageable pageable);

//    TransactionResponse createTransactionOFF(String categoryType, Long id, TransactionRequest transactionRequest) throws BadRequestException;

    ResponseMessage createJurnalUmum(JurnalUmumRequest jurnalUmumRequest) throws BadRequestException;

    Page<TransactionResponse> getTransactionsByCampaignId(Long campaignId, Pageable pageable);

    Page<TransactionResponse> getTransactionsByZakatId(Long zakatId, Pageable pageable);

    Page<TransactionResponse> getTransactionsByInfakId(Long infakId, Pageable pageable);

    Page<TransactionResponse> getTransactionsByDSKLId(Long dsklId, Pageable pageable);

    Page<TransactionResponse> getTransactionsByWakafId(Long wakafId, Pageable pageable);

    List<DonaturTransactionsHistoryResponse> getDonaturTransactionsHistory() throws BadRequestException;

    Map<String, Double> getTransactionSummaryForDonatur(Authentication authentication);

    Page<Transaction> searchTransactions(String search, Pageable pageable);

    List<Map<String, Object>> getAllDonatur();

    ResponseMessage updateJurnalUmumPenyaluran(String nomorBukti, JurnalUmumRequest jurnalUmumRequest) throws BadRequestException;

    Map<String, Object> getJurnalUmumByNomorBukti(String nomorBukti) throws BadRequestException;

    List<Map<String, Object>> getAllPenyaluran();

    ResponseMessage validateTemporaryTransaction(String nomorBukti);

    List<TemporaryTransaction> getAllTemporaryTransactions();
}
