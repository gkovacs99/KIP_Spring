package com.SFM.KIP.repository;

import com.SFM.KIP.entity.ConsultationDay;
import com.SFM.KIP.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface ConsultationDayRepository extends JpaRepository<ConsultationDay, Long> {

    List<ConsultationDay> findByDoctorAndDateGreaterThanEqualOrderByDateAsc(User doctor, LocalDate from);

    List<ConsultationDay> findByRoomIsNullAndDateGreaterThanEqualOrderByDateAsc(LocalDate from);

    @Query("""
        SELECT cd
        FROM ConsultationDay cd
        JOIN FETCH cd.room
        WHERE cd.doctor.id = :doctorId
          AND cd.date BETWEEN :from AND :to
          AND cd.room IS NOT NULL
        ORDER BY cd.date, cd.startTime
    """)
    List<ConsultationDay> findAvailableConsultationDays(
            @Param("doctorId") Long doctorId,
            @Param("from") LocalDate from,
            @Param("to") LocalDate to
    );
}