package com.SFM.KIP.repository;

import com.SFM.KIP.entity.DutyShift;
import com.SFM.KIP.entity.DutyStatus;
import com.SFM.KIP.entity.User;
import org.jspecify.annotations.Nullable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DutyShiftRepository extends JpaRepository<DutyShift, Long> {

    List<DutyShift> findByEmployeeAndStatusOrderByDateAsc(User employee, DutyStatus status);

    List<DutyShift> findAllByStatusOrderByDateAscStartTimeAsc(DutyStatus status);

    List<DutyShift> findAllByEmployee_IdOrderByDateAscStartTimeAsc(Long employeeId);
    List<DutyShift> findAllByNurse_IdOrderByDateAscStartTimeAsc(Long nurseId);
}