package com.example.dto;

import lombok.*;
import org.springframework.stereotype.Service;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class UrlResponseDto {
    private String longUrl;
    private String encodeUrl;
    private String title;
    private String avatar;
}
