package com.lazis.lazissultanagung.service;

import com.lazis.lazissultanagung.dto.response.ResponseMessage;
import com.lazis.lazissultanagung.model.Wakaf;

import java.util.List;
import java.util.Optional;

public interface WakafService {
    List<Wakaf> getAllWakaf();

    Wakaf createWakaf(Wakaf wakaf);

    Wakaf updateWakaf(Long id, Wakaf wakaf);

    Optional<Wakaf> getWakafById(Long id);

    ResponseMessage deleteWakaf(Long id);
}
