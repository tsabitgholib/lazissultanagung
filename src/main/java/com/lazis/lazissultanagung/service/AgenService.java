package com.lazis.lazissultanagung.service;

import com.lazis.lazissultanagung.dto.request.AgenRequest;
import com.lazis.lazissultanagung.dto.response.AgenResponse;
import com.lazis.lazissultanagung.exception.BadRequestException;
import com.lazis.lazissultanagung.model.Agen;
import java.util.List;

public interface AgenService {
    Agen getCurrentAgen() throws BadRequestException;
    Agen createAgen(AgenRequest agenRequest) throws BadRequestException;
    Agen updateAgen(Long id, AgenRequest agenRequest) throws BadRequestException;
    void deleteAgen(Long id) throws BadRequestException;
    Agen getAgenById(Long id) throws BadRequestException;
    List<AgenResponse> getAllAgen();
}
