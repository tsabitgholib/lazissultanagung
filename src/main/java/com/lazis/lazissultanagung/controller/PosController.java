package com.lazis.lazissultanagung.controller;

import com.lazis.lazissultanagung.dto.request.PosTransactionRequest;
import com.lazis.lazissultanagung.dto.response.PosDashboardResponse;
import com.lazis.lazissultanagung.dto.response.PosHistoryResponse;
import com.lazis.lazissultanagung.dto.response.PosTransactionResponse;
import com.lazis.lazissultanagung.dto.response.ResponseMessage;
import com.lazis.lazissultanagung.exception.BadRequestException;
import com.lazis.lazissultanagung.service.PosService;
import com.lazis.lazissultanagung.service.UserDetailsImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

import org.springframework.http.HttpHeaders;
import org.springframework.web.multipart.MultipartFile;

@CrossOrigin(origins= {"*"}, maxAge = 4800, allowCredentials = "false" )
@RestController
@RequestMapping("api/pos")
public class PosController {

    @Autowired
    private PosService posService;

    @GetMapping("/history")
    public ResponseEntity<Page<PosHistoryResponse>> getPosHistory(
            @RequestParam(required = false) Long eventId,
            @RequestParam(required = false) LocalDate startDate,
            @RequestParam(required = false) LocalDate endDate,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String paymentMethod,
            @RequestParam(required = false) String search,
            @PageableDefault(sort = "transactionDate", direction = Sort.Direction.DESC) Pageable pageable,
            Authentication authentication) {
        
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        Long agenId = userDetails.getId();
        
        Page<PosHistoryResponse> history = posService.getPosHistory(agenId, eventId, startDate, endDate, category, paymentMethod, search, pageable);
        return ResponseEntity.ok(history);
    }

    @GetMapping("/dashboard")
    public ResponseEntity<PosDashboardResponse> getPosDashboard(Authentication authentication) {
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        Long agenId = userDetails.getId();
        
        PosDashboardResponse dashboard = posService.getPosDashboard(agenId);
        return ResponseEntity.ok(dashboard);
    }

    @GetMapping("/history-by-agent")
    public ResponseEntity<Page<PosHistoryResponse>> getPosHistoryByAgentId(
            @RequestParam(required = false) Long agenId,
            @RequestParam(required = false) Long eventId,
            @RequestParam(required = false) LocalDate startDate,
            @RequestParam(required = false) LocalDate endDate,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String paymentMethod,
            @RequestParam(required = false) String search,
            @PageableDefault(sort = "transactionDate", direction = Sort.Direction.DESC) Pageable pageable) {
        
        Page<PosHistoryResponse> history = posService.getPosHistory(agenId, eventId, startDate, endDate, category, paymentMethod, search, pageable);
        return ResponseEntity.ok(history);
    }

    @GetMapping("/history-recap")
    public ResponseEntity<List<PosHistoryResponse>> getPosHistoryByAgentIdList(
            @RequestParam(required = false) Long agenId,
            @RequestParam(required = false) Long eventId,
            @RequestParam(required = false) LocalDate startDate,
            @RequestParam(required = false) LocalDate endDate,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String paymentMethod,
            @RequestParam(required = false) String search) {
        
        List<PosHistoryResponse> history = posService.getPosHistoryList(agenId, eventId, startDate, endDate, category, paymentMethod, search);
        return ResponseEntity.ok(history);
    }

    @PostMapping(value = "/create-transaction", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> createPosTransaction(@ModelAttribute PosTransactionRequest request, Authentication authentication) {
        try {
            UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
            Long agenId = userDetails.getId();
            PosTransactionResponse response = posService.createPosTransaction(request, agenId);
            return ResponseEntity.ok(response);
        } catch (BadRequestException e) {
            return ResponseEntity.badRequest().body(new ResponseMessage(false, e.getMessage()));
        }
    }

    @GetMapping("/download-template")
    public ResponseEntity<byte[]> downloadTemplate() {
        byte[] excelContent = posService.downloadImportTemplate();
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=template_import_transaksi_pos.xlsx")
                .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(excelContent);
    }

    @PostMapping(value = "/import-excel", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> importExcel(@RequestParam("file") MultipartFile file, Authentication authentication) {
        try {
            UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
            Long agenId = userDetails.getId();
            posService.importTransactionsFromExcel(file, agenId);
            return ResponseEntity.ok(new ResponseMessage(true, "Import transaksi berhasil"));
        } catch (BadRequestException e) {
            return ResponseEntity.badRequest().body(new ResponseMessage(false, e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(new ResponseMessage(false, "Terjadi kesalahan: " + e.getMessage()));
        }
    }

    @GetMapping("/search-donatur")
    public ResponseEntity<List<PosHistoryResponse>> getDistinctDonaturPos(@RequestParam(required = false) String search) {
        List<PosHistoryResponse> donatur = posService.getDistinctDonaturPos(search);
        return ResponseEntity.ok(donatur);
    }
}
