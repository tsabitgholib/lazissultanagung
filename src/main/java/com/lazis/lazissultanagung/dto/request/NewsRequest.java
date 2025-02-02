package com.lazis.lazissultanagung.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;

@Data
public class NewsRequest {
    private long id;
    private long newsTopicId;
    private String title;
    private String content;
    private MultipartFile newsImage;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate date;
    private String creator;
}
