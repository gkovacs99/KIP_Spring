package com.SFM.KIP.service;

import com.SFM.KIP.dto.RegisterForm;
import com.SFM.KIP.entity.User;
import com.SFM.KIP.repository.RoleRepository;
import com.SFM.KIP.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public boolean emailExists(String email){
        return userRepository.existsByEmail(email);
    }

    @Transactional
    public void registerPatient(RegisterForm form){
        User user = new User();
        user.setName(form.name().trim());
        user.setEmail(form.email().trim().toLowerCase());
        user.setPassword(passwordEncoder.encode(form.password()));
        user.setRole(roleRepository.findByName("USER").orElseThrow());
        userRepository.save(user);
    }
}
