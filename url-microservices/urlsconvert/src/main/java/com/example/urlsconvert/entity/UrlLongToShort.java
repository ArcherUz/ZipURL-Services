package com.example.urlsconvert.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
import java.time.LocalDateTime;

@Entity
@Table(name = "url_long_to_short")
@AllArgsConstructor
@Data
public class UrlLongToShort {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "long_url", nullable = false, columnDefinition = "TEXT")
    private String longUrl;
    @Column(name = "short_url", nullable = false, length = 255)
    private String shortUrl;
    @Column(nullable = false)
    private Timestamp creationDate;
    @Column(length = 255)
    private String title;
    @Column(length = 255)
    private String avatar;

    public UrlLongToShort(){
        this.creationDate = Timestamp.valueOf(LocalDateTime.now());
    }
}
