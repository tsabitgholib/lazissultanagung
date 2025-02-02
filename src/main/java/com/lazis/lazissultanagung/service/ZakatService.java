package com.lazis.lazissultanagung.service;

import com.lazis.lazissultanagung.dto.response.ResponseMessage;
import com.lazis.lazissultanagung.exception.BadRequestException;
import com.lazis.lazissultanagung.model.Zakat;

import java.util.List;
import java.util.Optional;


public interface ZakatService {
    List<Zakat> getAllZakat();

    Zakat crateZakat(Zakat zakat) throws BadRequestException;

    Zakat updateZakat(Long id, Zakat zakat);

    Optional<Zakat> getZakatById(Long id);

    ResponseMessage deleteZakat(Long id);
}
