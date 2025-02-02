package com.lazis.lazissultanagung.service;

import com.lazis.lazissultanagung.dto.response.ResponseMessage;
import com.lazis.lazissultanagung.enumeration.ERole;
import com.lazis.lazissultanagung.exception.BadRequestException;
import com.lazis.lazissultanagung.model.Admin;
import com.lazis.lazissultanagung.model.Zakat;
import com.lazis.lazissultanagung.repository.AdminRepository;
import com.lazis.lazissultanagung.repository.ZakatRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ZakatServiceImpl implements ZakatService {

    @Autowired
    private ZakatRepository zakatRepository;

    @Autowired
    private AdminRepository adminRepository;

    @Override
    public List<Zakat> getAllZakat(){
        return zakatRepository.findAll();
    }

    @Override
    public Zakat crateZakat(Zakat zakat) throws BadRequestException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof UserDetailsImpl) {
            UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
            Admin existingAdmin = adminRepository.findByPhoneNumber(userDetails.getPhoneNumber())
                    .orElseThrow(() -> new BadRequestException("Admin tidak ditemukan"));

            if (!existingAdmin.getRole().equals(ERole.ADMIN) && !existingAdmin.getRole().equals(ERole.OPERATOR)) {
                throw new BadRequestException("Hanya Admin dan Operator yang bisa membuat zakat");
            }

            zakat.setEmergency(false);

            return zakatRepository.save(zakat);
        }
        throw new BadRequestException("Admin tidak ditemukan");
    }

    @Override
    public Zakat updateZakat(Long id, Zakat zakat){
        Zakat updateZakat = zakatRepository.findById(id)
                .orElseThrow(()-> new BadRequestException("Zakat tidak ditemukan"));

        updateZakat.setCategoryName(zakat.getCategoryName());
        updateZakat.setAmount(zakat.getAmount());
        updateZakat.setDistribution(zakat.getDistribution());
        updateZakat.setEmergency(zakat.isEmergency());

        return zakatRepository.save(updateZakat);
    }

    @Override
    public Optional<Zakat> getZakatById(Long id){
        return Optional.ofNullable(zakatRepository.findById(id)
                .orElseThrow(() -> new BadRequestException("Zakat tidak ditemukan ")));
    }

    @Override
    public ResponseMessage deleteZakat(Long id){
        Zakat deleteZakat = zakatRepository.findById(id)
                .orElseThrow(()-> new BadRequestException("Zakat tidak ditemukan"));
        zakatRepository.delete(deleteZakat);
        return new ResponseMessage(true, "Zakat Berhasil Dihapus");
    }
}
