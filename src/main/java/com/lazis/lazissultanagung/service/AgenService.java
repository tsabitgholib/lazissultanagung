package com.lazis.lazissultanagung.service;

import com.lazis.lazissultanagung.dto.request.AgenRequest;
import com.lazis.lazissultanagung.exception.BadRequestException;
import com.lazis.lazissultanagung.model.Agen;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface AgenService {
    Agen getCurrentAgen() throws BadRequestException;
    Agen createAgen(AgenRequest agenRequest) throws BadRequestException;
    Agen updateAgen(Long id, AgenRequest agenRequest) throws BadRequestException;
    void deleteAgen(Long id) throws BadRequestException;
    Agen getAgenById(Long id) throws BadRequestException;
    Page<Agen> getAllAgen(Pageable pageable);
}
