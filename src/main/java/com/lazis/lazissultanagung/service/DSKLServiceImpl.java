package com.lazis.lazissultanagung.service;

import com.lazis.lazissultanagung.dto.response.ResponseMessage;
import com.lazis.lazissultanagung.enumeration.ERole;
import com.lazis.lazissultanagung.exception.BadRequestException;
import com.lazis.lazissultanagung.model.Admin;
import com.lazis.lazissultanagung.model.DSKL;
import com.lazis.lazissultanagung.repository.AdminRepository;
import com.lazis.lazissultanagung.repository.DSKLRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class DSKLServiceImpl implements DSKLService{

    @Autowired
    private AdminRepository adminRepository;

    @Autowired
    private DSKLRepository dsklRepository;

    @Override
    public List<DSKL> getAllDSKL(){
        return dsklRepository.findAll();
    }

    @Override
    public DSKL createDSKL(DSKL dskl){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof UserDetailsImpl) {
            UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
            Admin existingAdmin = adminRepository.findByPhoneNumber(userDetails.getPhoneNumber())
                    .orElseThrow(() -> new BadRequestException("Admin tidak ditemukan"));

            if (!existingAdmin.getRole().equals(ERole.ADMIN) && !existingAdmin.getRole().equals(ERole.OPERATOR)) {
                throw new BadRequestException("Hanya Admin dan Operator yang bisa membuat DSKL");
            }

            dskl.setEmergency(false);

            return dsklRepository.save(dskl);
        }
        throw new BadRequestException("Admin tidak ditemukan");
    }


    @Override
    public DSKL updateDSKL(Long id, DSKL dskl){
        DSKL updateDSKL = dsklRepository.findById(id)
                .orElseThrow(()-> new BadRequestException("DSKL tidak ditemukan"));

        updateDSKL.setCategoryName(dskl.getCategoryName());
        updateDSKL.setAmount(dskl.getAmount());
        updateDSKL.setDistribution(dskl.getDistribution());
        updateDSKL.setEmergency(dskl.isEmergency());
        updateDSKL.setCoaDebit(dskl.getCoaDebit());
        updateDSKL.setCoaKredit(dskl.getCoaKredit());

        return dsklRepository.save(updateDSKL);
    }

    @Override
    public Optional<DSKL> getDSKLById(Long id){
        return Optional.ofNullable(dsklRepository.findById(id)
                .orElseThrow(() -> new BadRequestException("DSKL tidak ditemukan ")));
    }

    @Override
    public ResponseMessage deleteDSKL(Long id){
        DSKL deleteDSKL = dsklRepository.findById(id)
                .orElseThrow(()-> new BadRequestException("DSKL tidak ditemukan"));
        dsklRepository.delete(deleteDSKL);
        return new ResponseMessage(true, "DSKL Berhasil Dihapus");
    }
}
