package com.lazis.lazissultanagung.service;

import com.lazis.lazissultanagung.dto.response.ResponseMessage;
import com.lazis.lazissultanagung.enumeration.ERole;
import com.lazis.lazissultanagung.exception.BadRequestException;
import com.lazis.lazissultanagung.model.Admin;
import com.lazis.lazissultanagung.model.Infak;
import com.lazis.lazissultanagung.repository.AdminRepository;
import com.lazis.lazissultanagung.repository.InfakRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class InfakServiceImpl implements InfakService{

    @Autowired
    private AdminRepository adminRepository;

    @Autowired
    private InfakRepository infakRepository;

    @Override
    public List<Infak> getAllInfak(){
        return infakRepository.findAll();
    }

    @Override
    public Infak createInfak(Infak infak){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof UserDetailsImpl) {
            UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
            Admin existingAdmin = adminRepository.findByPhoneNumber(userDetails.getPhoneNumber())
                    .orElseThrow(() -> new BadRequestException("Admin tidak ditemukan"));

            if (!existingAdmin.getRole().equals(ERole.ADMIN) && !existingAdmin.getRole().equals(ERole.OPERATOR)) {
                throw new BadRequestException("Hanya Admin dan Operator yang bisa membuat Infak");
            }

            infak.setEmergency(false);

            return infakRepository.save(infak);
        }
        throw new BadRequestException("Admin tidak ditemukan");
    }

    @Override
    public Infak updateInfak(Long id, Infak infak){
        Infak updateInfak = infakRepository.findById(id)
                .orElseThrow(()-> new BadRequestException("Infak tidak ditemukan"));

        updateInfak.setCategoryName(infak.getCategoryName());
        updateInfak.setAmount(infak.getAmount());
        updateInfak.setDistribution(infak.getDistribution());
        updateInfak.setEmergency(infak.isEmergency());
        updateInfak.setCoaDebit(infak.getCoaDebit());
        updateInfak.setCoaKredit(infak.getCoaKredit());

        return infakRepository.save(updateInfak);
    }

    @Override
    public Optional<Infak> getInfakById(Long id){
        return Optional.ofNullable(infakRepository.findById(id)
                .orElseThrow(() -> new BadRequestException("Infak tidak ditemukan ")));
    }

    @Override
    public ResponseMessage deleteInfak(Long id){
        Infak deleteInfak = infakRepository.findById(id)
                .orElseThrow(()-> new BadRequestException("Infak tidak ditemukan"));
        infakRepository.delete(deleteInfak);
        return new ResponseMessage(true, "Infak Berhasil Dihapus");
    }
}
