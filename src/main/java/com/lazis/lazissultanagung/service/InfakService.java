package com.lazis.lazissultanagung.service;

import com.lazis.lazissultanagung.dto.response.ResponseMessage;
import com.lazis.lazissultanagung.model.Infak;

import java.util.List;
import java.util.Optional;

public interface InfakService {
    List<Infak> getAllInfak();

    Infak createInfak(Infak infak);

    Infak updateInfak(Long id, Infak infak);

    Optional<Infak> getInfakById(Long id);

    ResponseMessage deleteInfak(Long id);
}
