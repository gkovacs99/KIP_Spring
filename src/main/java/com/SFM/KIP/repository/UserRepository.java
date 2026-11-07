package com.SFM.KIP.repository;

import com.SFM.KIP.entity.Specialization;
import com.SFM.KIP.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    List<User> findByRole_NameAndActiveTrue(String rolename);

    List<User> findBySpecializationAndActiveTrueAndActiveTrue(Specialization specialization);

    List<User> findByRole_NameAndNameContainingIgnoreCaseAndActiveTrue(String roleName, String name);

    List<User> findByAssignedDoctor(User doctor);
}
