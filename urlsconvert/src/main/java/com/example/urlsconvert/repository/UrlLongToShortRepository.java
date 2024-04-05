package com.example.urlsconvert.repository;

import com.example.urlsconvert.entity.UrlLongToShort;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UrlLongToShortRepository extends JpaRepository<UrlLongToShort, Long> {
    Optional<UrlLongToShort> findByShortUrl(String shortUrl);
}
