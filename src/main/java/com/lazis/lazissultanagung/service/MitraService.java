package com.lazis.lazissultanagung.service;

import com.lazis.lazissultanagung.dto.request.MitraRequest;
import com.lazis.lazissultanagung.model.Mitra;

import java.util.List;

public interface MitraService {
    List<Mitra> getAllMitra();

    Mitra createMitra(MitraRequest mitraRequest);

    // READ BY ID
    Mitra getMitraById(Long id);

    Mitra updateMitra(Long id, MitraRequest mitraRequest);

    void deleteMitra(Long id);
}
