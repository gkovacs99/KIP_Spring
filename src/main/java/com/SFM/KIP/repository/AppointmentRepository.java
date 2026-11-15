package com.SFM.KIP.repository;

import com.SFM.KIP.entity.Appointment;
import com.SFM.KIP.entity.AppointmentStatus;
import com.SFM.KIP.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

    List<Appointment> findByPatientAndStatusAndStartTimeAfterOrderByStartTimeAsc(User patient, AppointmentStatus status, LocalDateTime after);

    List<Appointment> findByPatientOrderByStartTimeDesc(User patient);

    List<Appointment> findByDoctorAndStatusAndStartTimeBetweenOrderByStartTimeAsc(User doctor, AppointmentStatus status, LocalDateTime from, LocalDateTime to);
}