package com.lazis.lazissultanagung.service;

import com.lazis.lazissultanagung.exception.BadRequestException;
import com.lazis.lazissultanagung.model.Agen;

public interface AgenService {
    Agen getCurrentAgen() throws BadRequestException;
}
