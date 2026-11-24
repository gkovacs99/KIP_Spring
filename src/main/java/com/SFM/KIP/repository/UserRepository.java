package com.SFM.KIP.repository;

import com.SFM.KIP.entity.Specialization;
import com.SFM.KIP.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    List<User> findByRole_NameAndActiveTrue(String rolename);

    List<User> findBySpecializationAndActiveTrue(Specialization specialization);

    List<User> findByRole_NameAndNameContainingIgnoreCaseAndActiveTrue(String roleName, String name);

    List<User> findByAssignedDoctor(User doctor);

    List<User> findAllByRole_Name(String roleName);

    List<User> findAllByOrderByNameAsc();

    List<User> findByActiveTrueOrderByNameAsc();

    boolean existsBySpecialization_Id(Long specializationId);

    List<User> findByEmailContainingIgnoreCaseOrderByNameAsc(String email);

    @Query("""
    SELECT u
    FROM User u
    JOIN FETCH u.role
    WHERE u.department.id = :departmentId
      AND u.role.name = 'DOCTOR'
      AND u.active = true
    ORDER BY u.name
""")
    List<User> findActiveDoctorsByDepartmentId(
            @Param("departmentId") Long departmentId
    );
}
