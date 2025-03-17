package com.lazis.lazissultanagung.service;

import com.lazis.lazissultanagung.dto.request.NewsRequest;
import com.lazis.lazissultanagung.dto.response.NewsResponse;
import com.lazis.lazissultanagung.dto.response.ResponseMessage;
import com.lazis.lazissultanagung.enumeration.ERole;
import com.lazis.lazissultanagung.exception.BadRequestException;
import com.lazis.lazissultanagung.model.Admin;
import com.lazis.lazissultanagung.model.News;
import com.lazis.lazissultanagung.model.NewsTopic;
import com.lazis.lazissultanagung.repository.AdminRepository;
import com.lazis.lazissultanagung.repository.NewsRepository;
import com.lazis.lazissultanagung.repository.NewsTopicRepository;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class NewsServiceImpl implements NewsService {

    @Autowired
    private NewsRepository newsRepository;

    @Autowired
    private NewsTopicRepository newsTopicRepository;

    @Autowired
    private AdminRepository adminRepository;

    @Autowired
    private CloudinaryService cloudinaryService;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private FileStorageService fileStorageService;

    @Override
    public Page<NewsResponse> getAllNews(Pageable pageable) {
        return newsRepository.findAllApprovedNews(pageable)
                .map(allNews -> {
                    NewsResponse response = modelMapper.map(allNews, NewsResponse.class);
                    response.setNewsTopic(allNews.getNewsTopic().getNewsTopic());
                    response.setCreator(allNews.getAdmin().getUsername());
                    response.setNewsImage("https://skyconnect.lazis-sa.org/api/images/"+allNews.getNewsImage());

                    return response;
                });
    }

    @Override
    public Optional<NewsResponse> getNewsById(Long id) {
        return newsRepository.findById(id)
                .map(existingNews -> {
                    NewsResponse response = modelMapper.map(existingNews, NewsResponse.class);
                    response.setNewsTopic(existingNews.getNewsTopic().getNewsTopic());
                    response.setCreator(existingNews.getAdmin().getUsername());
                    response.setNewsImage("https://skyconnect.lazis-sa.org/api/images/"+existingNews.getNewsImage());

                    return response;
                });
    }


    @Override
    public NewsResponse createNews(NewsRequest newsRequest) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof UserDetailsImpl) {
            UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
            Admin existingAdmin = adminRepository.findByPhoneNumber(userDetails.getPhoneNumber())
                    .orElseThrow(() -> new BadRequestException("Admin tidak ditemukan"));

            if (!existingAdmin.getRole().equals(ERole.ADMIN) && !existingAdmin.getRole().equals(ERole.OPERATOR)) {
                throw new BadRequestException("Hanya Admin dan Operator yang bisa membuat Berita");
            }

            String imageUrl = null;
            if (newsRequest.getNewsImage() != null && !newsRequest.getNewsImage().isEmpty()) {
                imageUrl = fileStorageService.saveFile(newsRequest.getNewsImage());
            }
            News news = new News();
            NewsTopic newsTopic = newsTopicRepository.findById(newsRequest.getNewsTopicId())
                    .orElseThrow(() -> new BadRequestException("Topic tidak ditemukan"));
            news.setNewsTopic(newsTopic);
            news.setTitle(newsRequest.getTitle());
            news.setContent(newsRequest.getContent());
            news.setNewsImage(imageUrl);
            news.setDate(newsRequest.getDate());
            news.setAdmin(existingAdmin);
            if (existingAdmin.getRole().equals(ERole.ADMIN)) {
                news.setApproved(true);
            } else if (existingAdmin.getRole().equals(ERole.OPERATOR)) {
                news.setApproved(false);
            }

            News savedNews = newsRepository.save(news);

            NewsResponse newsResponse = modelMapper.map(savedNews, NewsResponse.class);
            newsResponse.setNewsTopic(news.getNewsTopic().getNewsTopic());
            newsResponse.setCreator(news.getAdmin().getUsername());

            return newsResponse;
        }
        throw new BadRequestException("Admin tidak ditemukan");
    }

    @Override
    public NewsResponse updateNews(Long id, NewsRequest newsRequest) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof UserDetailsImpl) {
            UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
            Admin existingAdmin = adminRepository.findByPhoneNumber(userDetails.getPhoneNumber())
                    .orElseThrow(() -> new BadRequestException("Admin tidak ditemukan"));

            if (!existingAdmin.getRole().equals(ERole.ADMIN) && !existingAdmin.getRole().equals(ERole.OPERATOR)) {
                throw new BadRequestException("Hanya Admin dan Operator yang bisa mengedit berita");
            }

            News updateNews = newsRepository.findById(id)
                    .orElseThrow(() -> new BadRequestException("Berita tidak ditemukan"));

            NewsTopic newsTopic = newsTopicRepository.findById(newsRequest.getNewsTopicId())
                    .orElseThrow(() -> new BadRequestException("Topik tidak ditemukan"));

            updateNews.setNewsTopic(newsTopic);
            updateNews.setTitle(newsRequest.getTitle());
            updateNews.setContent(newsRequest.getContent());

            // Hanya perbarui gambar jika gambar baru diunggah
            if (newsRequest.getNewsImage() != null && !newsRequest.getNewsImage().isEmpty()) {
                String imageUrl = fileStorageService.saveFile(newsRequest.getNewsImage());
                updateNews.setNewsImage(imageUrl);
            }

            updateNews.setDate(newsRequest.getDate());
            updateNews.setAdmin(existingAdmin);

            // Tentukan status persetujuan berdasarkan peran admin
            if (existingAdmin.getRole().equals(ERole.ADMIN)) {
                updateNews.setApproved(true);
            } else if (existingAdmin.getRole().equals(ERole.OPERATOR)) {
                updateNews.setApproved(false);
            }

            News savedNews = newsRepository.save(updateNews);

            NewsResponse newsResponse = modelMapper.map(savedNews, NewsResponse.class);
            newsResponse.setNewsTopic(savedNews.getNewsTopic().getNewsTopic());
            newsResponse.setCreator(savedNews.getAdmin().getUsername());

            return newsResponse;
        }

        throw new BadRequestException("Admin tidak ditemukan");
    }

    @Override
    public ResponseMessage deleteNews(Long id){
        News news = newsRepository.findById(id)
                .orElseThrow(()-> new BadRequestException("Berita tidak ditemukan"));
        newsRepository.delete(news);

        return new ResponseMessage(true, "Berita berhasil dihapus");
    }

    @Override
    @Transactional
    public ResponseMessage approveNews(Long id) throws BadRequestException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof UserDetailsImpl) {
            UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
            Admin existingAdmin = adminRepository.findByPhoneNumber(userDetails.getPhoneNumber())
                    .orElseThrow(() -> new BadRequestException("Admin tidak ditemukan"));

            if (!existingAdmin.getRole().equals(ERole.ADMIN)) {
                throw new BadRequestException("Hanya Admin yang bisa menyetujui berita");
            }

            News news = newsRepository.findById(id)
                    .orElseThrow(()-> new BadRequestException("News tidak ditemukan"));

            news.setApproved(true);
            newsRepository.save(news);

            return new ResponseMessage(true, "Berita Berhasil disetujui");
        }
        throw new BadRequestException("Admin tidak ditemukan");
    }

    @Override
    public Page<NewsResponse> getNewsByTitleAndTopic(String title, String newsTopic, Pageable pageable) {
        Page<News> existingNews = newsRepository.findByTitleAndTopic(title, newsTopic, pageable);
        return existingNews.map(news -> {
            NewsResponse response = modelMapper.map(news, NewsResponse.class);
            response.setNewsTopic(news.getNewsTopic().getNewsTopic());
            response.setCreator(news.getAdmin().getUsername());
            response.setNewsImage("https://skyconnect.lazis-sa.org/api/images/"+news.getNewsImage());

            return response;
        });
    }

}