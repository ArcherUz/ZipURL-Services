package com.example.urlsconvert.dao;

import com.example.urlsconvert.entity.UrlLongToShort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UrlLongToShortRepository extends JpaRepository<UrlLongToShort, Long> {
    Optional<UrlLongToShort> findByShortUrl(String shortUrl);
}
