package com.lazis.lazissultanagung.service;

import com.lazis.lazissultanagung.dto.response.ResponseMessage;
import com.lazis.lazissultanagung.model.DSKL;

import java.util.List;
import java.util.Optional;

public interface DSKLService {
    List<DSKL> getAllDSKL();

    DSKL createDSKL(DSKL dskl);

    DSKL updateDSKL(Long id, DSKL dskl);

    Optional<DSKL> getDSKLById(Long id);

    ResponseMessage deleteDSKL(Long id);
}
