package com.lazis.lazissultanagung.service;

import com.lazis.lazissultanagung.dto.request.AgenRequest;
import com.lazis.lazissultanagung.model.Agen;

import java.util.List;

public interface AgenService {
    List<Agen> getAllAgen();
    Agen createAgen(AgenRequest agenRequest);
    Agen getAgenById(Long id);
    Agen updateAgen(Long id, AgenRequest agenRequest);
    void deleteAgen(Long id);
}
