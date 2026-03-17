package com.lazis.lazissultanagung.service;

import com.lazis.lazissultanagung.dto.response.ResponseMessage;
import com.lazis.lazissultanagung.exception.BadRequestException;
import com.lazis.lazissultanagung.model.Coa;
import com.lazis.lazissultanagung.repository.CoaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class CoaServiceImpl implements CoaService{

    @Autowired
    public CoaRepository coaRepository;

    @Override
    public List<Coa> getAllParentCoa(){
        return coaRepository.findByParentAccountIsNullAndDeletedAtIsNull(Sort.by(Sort.Direction.ASC, "accountCode"));
    }

    @Override
    public List<Coa> getAllCoa(){
        return coaRepository.findByParentAccountIsNotNullAndDeletedAtIsNull(Sort.by(Sort.Direction.ASC, "accountCode"));
    }

    @Override
    public List<Coa> getAllCoas() {
        return coaRepository.findAllActive(Sort.by(Sort.Direction.ASC, "accountCode"));
    }

    @Override
    public Coa getCoaById(Long id){
        return coaRepository.findById(id)
                .orElseThrow(()->new BadRequestException("Coa tidak ditemukan"));

    }

    @Override
    public List<Coa> getCoaByAccountType(String accountType) {
        switch (accountType.toLowerCase()) {
            case "asset":
                return coaRepository.getCoaAsset();
            case "liability":
                return coaRepository.getCoaLiability();
            case "equity":
                return coaRepository.getCoaEquity();
            case "revenue":
                return coaRepository.getCoaRevenue();
            case "expense":
                return coaRepository.getCoaExpense();
            default:
                throw new IllegalArgumentException("Invalid account type: " + accountType);
        }
    }

    @Override
    public Coa createCoa(Coa coa){
        return coaRepository.save(coa);
    }

    @Override
    public Coa editCoa(Long id, Coa coa){
        Coa editedCoa = coaRepository.findById(id)
                .filter(c -> c.getDeletedAt() == null)
                .orElseThrow(()-> new BadRequestException("COA tidak ditemukan"));

        editedCoa.setAccountCode(coa.getAccountCode());
        editedCoa.setAccountName(coa.getAccountName());
        editedCoa.setAccountType(coa.getAccountType());
        editedCoa.setParentAccount(coa.getParentAccount());

        return coaRepository.save(editedCoa);
    }

    @Override
    public ResponseMessage deleteCoa(Long id){
        Coa deleteCoa = coaRepository.findById(id)
                .orElseThrow(()-> new BadRequestException("COA tidak ditemukan"));
        coaRepository.delete(deleteCoa);
        return new ResponseMessage(true, "COA berhasil dihapus permanen");
    }

    @Override
    public ResponseMessage softDeleteCoa(Long id) {
        Coa coa = coaRepository.findById(id)
                .filter(c -> c.getDeletedAt() == null)
                .orElseThrow(() -> new BadRequestException("COA tidak ditemukan"));
        coa.setDeletedAt(LocalDateTime.now());
        coaRepository.save(coa);
        return new ResponseMessage(true, "COA berhasil dihapus (soft delete)");
    }

    @Override
    public ResponseMessage restoreCoa(Long id) {
        Coa coa = coaRepository.findById(id)
                .filter(c -> c.getDeletedAt() != null)
                .orElseThrow(() -> new BadRequestException("COA yang terhapus tidak ditemukan"));
        coa.setDeletedAt(null);
        coaRepository.save(coa);
        return new ResponseMessage(true, "COA berhasil dikembalikan");
    }

    @Override
    public Page<Coa> getDeletedCoa(Pageable pageable) {

        Pageable sortedPageable = PageRequest.of(
                pageable.getPageNumber(),
                pageable.getPageSize(),
                Sort.by(Sort.Direction.ASC, "accountCode")
        );

        return coaRepository.findByDeletedAtIsNotNull(sortedPageable);
    }

    @Override
    public List<Map<String, Object>> getCoaPengelola() {
        List<Object[]> results = coaRepository.findByParentAccountsPengelola();

        return results.stream().map(obj -> Map.of(
                "id", obj[0],
                "accountCode", obj[1],
                "categoryName", obj[2]  // accountName sebagai categoryName
        )).collect(Collectors.toList());
    }
}
