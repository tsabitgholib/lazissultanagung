package com.lazis.lazissultanagung.service;

import com.lazis.lazissultanagung.exception.BadRequestException;
import com.lazis.lazissultanagung.model.NomZakat;
import com.lazis.lazissultanagung.repository.NomZakatRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class NomZakatService {

    @Autowired
    private NomZakatRepository nomZakatRepository;

    public NomZakat addNomZakat(NomZakat nomZakat){
        return nomZakatRepository.save(nomZakat);
    }

    public NomZakat editNomZakat(Long id, NomZakat nomZakat) {
        NomZakat percentageForCampaign1 = nomZakatRepository.findById(id)
                .orElseThrow(() -> new BadRequestException("gagal mengedit, id tidak ada"));

        percentageForCampaign1.setNomZakat(nomZakat.getNomZakat());

        return nomZakatRepository.save(percentageForCampaign1);
    }


    public List<NomZakat> getAllNomZakat(){
        return nomZakatRepository.findAll();
    }

    public Optional<NomZakat> getNomZakatById(Long id) {
        return nomZakatRepository.findById(id);
    }

    public Optional<NomZakat> getNomZakatByIdOne() {
        return getNomZakatById(1L);
    }
}
