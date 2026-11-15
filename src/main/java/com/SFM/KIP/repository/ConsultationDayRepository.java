package com.SFM.KIP.repository;

import com.SFM.KIP.entity.ConsultationDay;
import com.SFM.KIP.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface ConsultationDayRepository extends JpaRepository<ConsultationDay, Long> {

    List<ConsultationDay> findByDoctorAndDateGreaterThanEqualOrderByDateAsc(User doctor, LocalDate from);

    List<ConsultationDay> findByRoomIsNullAndDateGreaterThanEqualOrderByDateAsc(LocalDate from);
}