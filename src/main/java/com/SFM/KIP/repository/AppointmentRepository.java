package com.SFM.KIP.repository;

import com.SFM.KIP.entity.Appointment;
import com.SFM.KIP.entity.AppointmentStatus;
import com.SFM.KIP.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

    List<Appointment> findByPatientAndStatusAndStartTimeAfterOrderByStartTimeAsc(User patient, AppointmentStatus status, LocalDateTime after);

    List<Appointment> findByPatientOrderByStartTimeDesc(User patient);

    List<Appointment> findByDoctorAndStatusAndStartTimeBetweenOrderByStartTimeAsc(User doctor, AppointmentStatus status, LocalDateTime from, LocalDateTime to);

    Optional<Appointment> findByIdAndPatient_Id(
            Long id,
            Long patientId
    );

    @Query("""
        SELECT COUNT(a) > 0
        FROM Appointment a
        WHERE a.doctor.id = :doctorId
          AND a.status <> :cancelled
          AND a.startTime < :endTime
          AND a.endTime > :startTime
    """)
    boolean isSlotOccupied(
            @Param("doctorId") Long doctorId,
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime,
            @Param("cancelled") AppointmentStatus cancelled
    );

    @Query("""
    SELECT a
    FROM Appointment a
    JOIN FETCH a.doctor
    WHERE a.patient.id = :patientId
    ORDER BY a.startTime DESC
""")
    List<Appointment> findPatientAppointments(@Param("patientId") Long patientId);
}