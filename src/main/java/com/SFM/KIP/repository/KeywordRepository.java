package com.SFM.KIP.repository;

import com.SFM.KIP.entity.Keyword;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface KeywordRepository extends JpaRepository<Keyword, Long> {
    List<Keyword> findByWordContainingIgnoreCase(String word);
}
