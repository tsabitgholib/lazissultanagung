package com.lazis.lazissultanagung.service;

import com.lazis.lazissultanagung.exception.BadRequestException;
import com.lazis.lazissultanagung.model.PenerimaManfaat;
import com.lazis.lazissultanagung.repository.PenerimaManfaatRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PenerimaManfaatServiceImpl implements PenerimaManfaatService{

    @Autowired
    PenerimaManfaatRepository penerimaManfaatRepository;

    @Override
    public List<PenerimaManfaat> getAllPenerimaManfaat() {
        List<PenerimaManfaat> penerimaManfaat = penerimaManfaatRepository.findAll();
        return penerimaManfaat;
    }

    @Override
    public Optional<PenerimaManfaat> getByIdOne() {
        return penerimaManfaatRepository.findById(1L);
    }

    @Override
    public PenerimaManfaat addPenerimaManfaat(PenerimaManfaat penerimaManfaat){
        return penerimaManfaatRepository.save(penerimaManfaat);
    }

    @Override
    public PenerimaManfaat editPenerimaManfaatIdOne(PenerimaManfaat penerimaManfaat){
        PenerimaManfaat penerimaManfaats = penerimaManfaatRepository.findById(1L)
                .orElseThrow(() -> new BadRequestException("id penerima manfaat tidak ada"));

        penerimaManfaats.setJumlahPenerimaManfaat(penerimaManfaat.getJumlahPenerimaManfaat());
        penerimaManfaats.setPenerimaManfaatCampaign(penerimaManfaat.getPenerimaManfaatCampaign());
        penerimaManfaats.setPenerimaManfaatZakat(penerimaManfaat.getPenerimaManfaatZakat());
        penerimaManfaats.setPenerimaManfaatInfak(penerimaManfaat.getPenerimaManfaatInfak());
        penerimaManfaats.setPenerimaManfaatWakaf(penerimaManfaat.getPenerimaManfaatWakaf());
        penerimaManfaats.setPenerimaManfaatDSKL(penerimaManfaat.getPenerimaManfaatDSKL());

        return penerimaManfaatRepository.save(penerimaManfaats);
    }



}
