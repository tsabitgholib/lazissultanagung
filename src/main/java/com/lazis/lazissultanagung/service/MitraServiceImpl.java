package com.lazis.lazissultanagung.service;

import com.lazis.lazissultanagung.dto.request.MitraRequest;
import com.lazis.lazissultanagung.model.Mitra;
import com.lazis.lazissultanagung.repository.MitraRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MitraServiceImpl implements MitraService{

    @Autowired
    private MitraRepository mitraRepository;

    @Autowired
    private FileStorageService fileStorageService;

    @Override
    public List<Mitra> getAllMitra() {
        List<Mitra> mitras = mitraRepository.findAll();
        mitras.forEach(m -> {
            if (m.getImage() != null) {
                m.setImage("https://skyconnect.lazis-sa.org/api/images/" + m.getImage());
            }
        });
        return mitras;
    }

    @Override
    public Mitra createMitra(MitraRequest mitraRequest) {
        String imageUrl = null;
        if (mitraRequest.getImage() != null && !mitraRequest.getImage().isEmpty()) {
            imageUrl = fileStorageService.saveFile(mitraRequest.getImage());
        }

        Mitra newMitra = new Mitra();
        newMitra.setName(mitraRequest.getName());
        newMitra.setImage(imageUrl);

        return mitraRepository.save(newMitra);
    }

    @Override
    public Mitra getMitraById(Long id) {
        Mitra mitra = mitraRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Mitra not found with id " + id));

        if (mitra.getImage() != null && !mitra.getImage().isEmpty()) {
            mitra.setImage("https://skyconnect.lazis-sa.org/api/images/" + mitra.getImage());
        }

        return mitra;
    }

    @Override
    public Mitra updateMitra(Long id, MitraRequest mitraRequest) {
        Mitra existingMitra = getMitraById(id);

        if (mitraRequest.getName() != null) {
            existingMitra.setName(mitraRequest.getName());
        }

        if (mitraRequest.getImage() != null && !mitraRequest.getImage().isEmpty()) {
            String imageUrl = fileStorageService.saveFile(mitraRequest.getImage());
            existingMitra.setImage(imageUrl);
        }

        return mitraRepository.save(existingMitra);
    }

    @Override
    public void deleteMitra(Long id) {
        Mitra mitra = getMitraById(id);
        mitraRepository.delete(mitra);
    }
}
