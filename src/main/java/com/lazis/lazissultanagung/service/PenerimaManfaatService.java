package com.lazis.lazissultanagung.service;

import com.lazis.lazissultanagung.model.PenerimaManfaat;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

public interface PenerimaManfaatService {
    List<PenerimaManfaat> getAllPenerimaManfaat();

    Optional<PenerimaManfaat> getByIdOne();

    PenerimaManfaat addPenerimaManfaat(PenerimaManfaat penerimaManfaat);

    PenerimaManfaat editPenerimaManfaatIdOne(PenerimaManfaat penerimaManfaat);
}
