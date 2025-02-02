package com.lazis.lazissultanagung.service;

import com.lazis.lazissultanagung.dto.response.ResponseMessage;
import com.lazis.lazissultanagung.enumeration.ERole;
import com.lazis.lazissultanagung.exception.BadRequestException;
import com.lazis.lazissultanagung.model.Admin;
import com.lazis.lazissultanagung.model.Wakaf;
import com.lazis.lazissultanagung.repository.AdminRepository;
import com.lazis.lazissultanagung.repository.WakafRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class WakafServiceImpl implements WakafService {

    @Autowired
    private AdminRepository adminRepository;

    @Autowired
    private WakafRepository wakafRepository;

    @Override
    public List<Wakaf> getAllWakaf(){
        return wakafRepository.findAll();
    }

    @Override
    public Wakaf createWakaf(Wakaf wakaf){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof UserDetailsImpl) {
            UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
            Admin existingAdmin = adminRepository.findByPhoneNumber(userDetails.getPhoneNumber())
                    .orElseThrow(() -> new BadRequestException("Admin tidak ditemukan"));

            if (!existingAdmin.getRole().equals(ERole.ADMIN) && !existingAdmin.getRole().equals(ERole.OPERATOR)) {
                throw new BadRequestException("Hanya Admin dan Operator yang bisa membuat wakaf");
            }

            wakaf.setEmergency(false);

            return wakafRepository.save(wakaf);
        }
        throw new BadRequestException("Admin tidak ditemukan");
    }

    @Override
    public Wakaf updateWakaf(Long id, Wakaf wakaf){
        Wakaf updateWakaf = wakafRepository.findById(id)
                .orElseThrow(()-> new BadRequestException("Wakaf tidak ditemukan"));

        updateWakaf.setCategoryName(wakaf.getCategoryName());
        updateWakaf.setAmount(wakaf.getAmount());
        updateWakaf.setDistribution(wakaf.getDistribution());
        updateWakaf.setEmergency(wakaf.isEmergency());

        return wakafRepository.save(updateWakaf);
    }

    @Override
    public Optional<Wakaf> getWakafById(Long id){
        return Optional.ofNullable(wakafRepository.findById(id)
                .orElseThrow(() -> new BadRequestException("Wakaf tidak ditemukan ")));
    }

    @Override
    public ResponseMessage deleteWakaf(Long id){
        Wakaf deleteWakaf = wakafRepository.findById(id)
                .orElseThrow(()-> new BadRequestException("Wakaf tidak ditemukan"));
        wakafRepository.delete(deleteWakaf);
        return new ResponseMessage(true, "Wakaf Berhasil Dihapus");
    }
}
