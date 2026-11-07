package com.SFM.KIP.repository;

import com.SFM.KIP.entity.DutyShift;
import com.SFM.KIP.entity.DutyStatus;
import com.SFM.KIP.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface DutyShiftRepository extends JpaRepository<DutyShift, Long> {

    List<DutyShift> findByEmployeeAndStatusOrderByDateAsc(User employee, DutyStatus status);

    List<DutyShift> findByEmployeeAndDateGreaterThanEqualOrderByDateAsc(User employee, LocalDate from);

    List<DutyShift> findByDateBetweenOrderByDateAsc(LocalDate from, LocalDate to);
}