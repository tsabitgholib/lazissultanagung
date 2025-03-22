package com.lazis.lazissultanagung.service;

import com.lazis.lazissultanagung.dto.response.ResponseMessage;
import com.lazis.lazissultanagung.enumeration.ERole;
import com.lazis.lazissultanagung.exception.BadRequestException;
import com.lazis.lazissultanagung.model.Admin;
import com.lazis.lazissultanagung.model.NewsTopic;
import com.lazis.lazissultanagung.repository.AdminRepository;
import com.lazis.lazissultanagung.repository.NewsRepository;
import com.lazis.lazissultanagung.repository.NewsTopicRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NewsTopicServiceImpl implements NewsTopicService {

    @Autowired
    private NewsTopicRepository newsTopicRepository;

    @Autowired
    private AdminRepository adminRepository;

    @Autowired
    private NewsRepository newsRepository;

    @Override
    public List<NewsTopic> getAllTopic(){
        return newsTopicRepository.findAll();
    }

    @Override
    public NewsTopic createTopic(NewsTopic newsTopic) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof UserDetailsImpl) {
            UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
            Admin existingAdmin = adminRepository.findByPhoneNumber(userDetails.getPhoneNumber())
                    .orElseThrow(() -> new BadRequestException("Admin tidak ditemukan"));

            if (!existingAdmin.getRole().equals(ERole.ADMIN) && !existingAdmin.getRole().equals(ERole.OPERATOR)) {
                throw new BadRequestException("Hanya Admin dan Operator yang bisa membuat kategori");
            }

            // Convert campaignCategory name to title case
            String formattedName = toTitleCase(newsTopic.getNewsTopic());
            newsTopic.setNewsTopic(formattedName);

            return newsTopicRepository.save(newsTopic);
        }
        throw new BadRequestException("Admin tidak ditemukan");
    }

    @Override
    public NewsTopic updateTopic(Long id, NewsTopic newsTopic){
        NewsTopic updateTopic = newsTopicRepository.findById(id)
                .orElseThrow(()-> new BadRequestException("Topic tidak ditemukan"));

        String formattedName = toTitleCase(newsTopic.getNewsTopic());
        updateTopic.setNewsTopic(formattedName);
        return newsTopicRepository.save(updateTopic);
    }

    @Override
    public ResponseMessage deleteTopic(Long id) {
        NewsTopic deleteTopic = newsTopicRepository.findById(id)
                .orElse(null);

        if (deleteTopic == null) {
            return new ResponseMessage(false, "Topic tidak ditemukan");
        }

        boolean isUsedInNews = newsRepository.existsByNewsTopic(deleteTopic);
        if (isUsedInNews) {
            return new ResponseMessage(false, "Topic tidak bisa dihapus karena sudah digunakan di Berita");
        }

        newsTopicRepository.delete(deleteTopic);
        return new ResponseMessage(true, "Topic Berhasil dihapus");
    }




    // Helper method to convert a string to title case
    private String toTitleCase(String input) {
        if (input == null || input.isEmpty()) {
            return input;
        }
        String[] words = input.toLowerCase().split(" ");
        StringBuilder titleCase = new StringBuilder();
        for (String word : words) {
            if (word.length() > 1) {
                titleCase.append(Character.toUpperCase(word.charAt(0)))
                        .append(word.substring(1))
                        .append(" ");
            } else {
                titleCase.append(Character.toUpperCase(word.charAt(0))).append(" ");
            }
        }
        return titleCase.toString().trim();
    }
}
