package com.SFM.KIP.repository;

import com.SFM.KIP.entity.Specialization;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SpecializationRepository extends JpaRepository<Specialization, Long> {
    List<Specialization> findByNameContainingIgnoreCase(String name);
}
