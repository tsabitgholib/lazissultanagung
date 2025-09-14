package com.lazis.lazissultanagung.controller;

import com.lazis.lazissultanagung.dto.request.JurnalUmumRequest;
import com.lazis.lazissultanagung.dto.request.TransactionRequest;
import com.lazis.lazissultanagung.dto.response.DonaturTransactionsHistoryResponse;
import com.lazis.lazissultanagung.dto.response.ResponseMessage;
import com.lazis.lazissultanagung.dto.response.TransactionResponse;
import com.lazis.lazissultanagung.exception.BadRequestException;
import com.lazis.lazissultanagung.model.Transaction;
import com.lazis.lazissultanagung.service.TransactionService;
import org.apache.tomcat.util.net.openssl.ciphers.Authentication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;

@CrossOrigin(origins= {"*"}, maxAge = 4800, allowCredentials = "false" )
@RestController
@RequestMapping("api/transaction")
public class TransactionController {

    @Autowired
    private TransactionService transactionService;

    @GetMapping
    public ResponseEntity<Page<TransactionResponse>> getTransactions(
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(required = false) Integer month,
            @RequestParam(required = false) Integer year) {

        int pageSize = 12;
        PageRequest pageRequest = PageRequest.of(page, pageSize);
        Page<TransactionResponse> transactions = transactionService.getAllTransaction(month, year, pageRequest);
        return ResponseEntity.ok(transactions);
    }

//    @PostMapping("/{categoryType}/{id}")
//    public ResponseEntity<TransactionResponse> createTransaction(@PathVariable String categoryType,
//                                                                 @PathVariable Long id,
//                                                                 @RequestBody TransactionRequest transactionRequest) {
//        TransactionResponse transactionResponse = transactionService.createTransactionOFF(categoryType, id, transactionRequest);
//        return ResponseEntity.ok(transactionResponse);
//    }

    @PostMapping("/jurnal-umum")
    public ResponseMessage createJurnalUmum(@RequestBody JurnalUmumRequest request) {
        // Panggil service untuk membuat jurnal umum
        ResponseMessage response = transactionService.createJurnalUmum(request);
        return new ResponseMessage(true, "Input Jurnal umum berhasil disimpan");
    }

    @GetMapping("/campaign/{campaignId}")
    public ResponseEntity<Page<TransactionResponse>> getTransactionsByCampaignId(
            @PathVariable Long campaignId,
            @RequestParam(name = "page", defaultValue = "0") int page) {

        int pageSize = 12;
        PageRequest pageRequest = PageRequest.of(page, pageSize);
        Page<TransactionResponse> transactions = transactionService.getTransactionsByCampaignId(campaignId, pageRequest);
        return ResponseEntity.ok(transactions);
    }

    @GetMapping("/zakat/{zakatId}")
    public ResponseEntity<Page<TransactionResponse>> getTransactionsByZakatId(
            @PathVariable Long zakatId,
            @RequestParam(name = "page", defaultValue = "0") int page) {

        int pageSize = 12;
        PageRequest pageRequest = PageRequest.of(page, pageSize);
        Page<TransactionResponse> transactions = transactionService.getTransactionsByZakatId(zakatId, pageRequest);
        return ResponseEntity.ok(transactions);
    }

    @GetMapping("/infak/{infakId}")
    public ResponseEntity<Page<TransactionResponse>> getTransactionsByInfakId(
            @PathVariable Long infakId,
            @RequestParam(name = "page", defaultValue = "0") int page) {

        int pageSize = 12;
        PageRequest pageRequest = PageRequest.of(page, pageSize);
        Page<TransactionResponse> transactions = transactionService.getTransactionsByInfakId(infakId, pageRequest);
        return ResponseEntity.ok(transactions);
    }

    @GetMapping("/dskl/{dsklId}")
    public ResponseEntity<Page<TransactionResponse>> getTransactionsByDSKLId(
            @PathVariable Long dsklId,
            @RequestParam(name = "page", defaultValue = "0") int page) {

        int pageSize = 12;
        PageRequest pageRequest = PageRequest.of(page, pageSize);
        Page<TransactionResponse> transactions = transactionService.getTransactionsByDSKLId(dsklId, pageRequest);
        return ResponseEntity.ok(transactions);
    }

    @GetMapping("/wakaf/{wakafId}")
    public ResponseEntity<Page<TransactionResponse>> getTransactionsByWakafId(
            @PathVariable Long wakafId,
            @RequestParam(name = "page", defaultValue = "0") int page) {

        int pageSize = 12;
        PageRequest pageRequest = PageRequest.of(page, pageSize);
        Page<TransactionResponse> transactions = transactionService.getTransactionsByWakafId(wakafId, pageRequest);
        return ResponseEntity.ok(transactions);
    }

    @GetMapping("/donaturHistory")
    public ResponseEntity<List<DonaturTransactionsHistoryResponse>> getUserTransactionsHistory(Authentication authentication) {
        try {
            List<DonaturTransactionsHistoryResponse> historyResponses = transactionService.getDonaturTransactionsHistory();
            return ResponseEntity.ok(historyResponses);
        } catch (BadRequestException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @GetMapping("/search")
    public ResponseEntity<Page<Transaction>> searchTransactions(
            @RequestParam String search,
            @RequestParam(name = "page", defaultValue = "0") int page) {
        int pageSize = 12;
        PageRequest pageRequest = PageRequest.of(page, pageSize);
        Page<Transaction> transactions = transactionService.searchTransactions(search, pageRequest);
        return ResponseEntity.ok(transactions);
    }

    @PutMapping("/edit-penyaluran")
    public ResponseEntity<?> editJurnalUmum(@RequestParam String nomorBukti,
                                            @RequestBody JurnalUmumRequest jurnalUmumRequest) {
        try {
            ResponseMessage response = transactionService.updateJurnalUmumPenyaluran(nomorBukti, jurnalUmumRequest);
            return ResponseEntity.ok(response);
        } catch (BadRequestException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/get-penyaluran-by-nomor-bukti")
    public ResponseEntity<?> getPenyaluranByNomorBukti(@RequestParam String nomorBukti) {
        Map<String, Object> result = transactionService.getJurnalUmumByNomorBukti(nomorBukti);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/get-all-penyaluran")
    public ResponseEntity<?> getAllPenyaluran() {
        List<Map<String, Object>> result = transactionService.getAllPenyaluran();
        return ResponseEntity.ok(result);
    }



}
