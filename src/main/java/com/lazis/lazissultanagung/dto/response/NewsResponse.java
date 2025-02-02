package com.lazis.lazissultanagung.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDate;

@Data
public class NewsResponse {
    private long id;
    private String newsTopic;
    private String title;
    private String content;
    private String newsImage;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate date;
    private String creator;
    private boolean approved;
}
