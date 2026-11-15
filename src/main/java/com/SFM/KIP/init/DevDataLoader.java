package com.SFM.KIP.init;

import com.SFM.KIP.entity.*;
import com.SFM.KIP.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalTime;

@Component
@Profile("dev")
@Order(2)
public class DevDataLoader implements CommandLineRunner {

    private static final String TEST_PASSWORD = "asd123";

    private final RoleRepository roleRepository;
    private final DepartmentRepository departmentRepository;
    private final SpecializationRepository specializationRepository;
    private final RoomRepository roomRepository;
    private final UserRepository userRepository;
    private final ConsultationDayRepository consultationDayRepository;
    private final PasswordEncoder passwordEncoder;

    public DevDataLoader(RoleRepository roleRepository,
                         DepartmentRepository departmentRepository,
                         SpecializationRepository specializationRepository,
                         RoomRepository roomRepository,
                         UserRepository userRepository,
                         ConsultationDayRepository consultationDayRepository,
                         PasswordEncoder passwordEncoder){
        this.roleRepository = roleRepository;
        this.departmentRepository = departmentRepository;
        this.specializationRepository = specializationRepository;
        this.roomRepository = roomRepository;
        this.userRepository = userRepository;
        this.consultationDayRepository = consultationDayRepository;
        this.passwordEncoder = passwordEncoder;
    }
    @Override
    public void run(String... args) throws Exception {
        if (userRepository.existsByEmail("fjanos@kip.hu")) {
            return;
        }

        Department belgyogyaszat = departmentRepository.findAll().getFirst();
        Specialization belgyogyasz = specializationRepository.findAll().getFirst();
        Room room = roomRepository.findAll().getFirst();

        User senior = createUser("Dr. Főorvosi János", "fjanos@kip.hu", "SENIOR", belgyogyaszat, belgyogyasz);
        User doctor = createUser("Dr. Doktoros Péter", "dpeter@kip.hu", "DOCTOR", belgyogyaszat, belgyogyasz);

        User nurse = createUser("Leápoló Éva", "leva@kip.hu", "NURSE", belgyogyaszat, null);
        nurse.setAssignedDoctor(doctor);
        userRepository.save(nurse);

        User patient = createUser("Beteg Elek", "belek@kip.hu", "USER", null, null);
        patient.setBirthDate(LocalDate.of(1990, 5, 12));
        userRepository.save(patient);

        createConsultationDay(doctor, room, LocalDate.now().plusDays(1), senior);
        createConsultationDay(doctor, room, LocalDate.now().plusDays(3), doctor);
    }

    private User createUser(String name, String email, String roleName, Department department, Specialization specialization){
        User u = new User();
        u.setName(name);
        u.setEmail(email);
        u.setPassword(passwordEncoder.encode(TEST_PASSWORD));
        u.setRole(roleRepository.findByName(roleName).orElseThrow());
        u.setDepartment(department);
        u.setSpecialization(specialization);
        return userRepository.save(u);
    }

    private void createConsultationDay(User doctor, Room room, LocalDate date, User createdBy){
        ConsultationDay day = new ConsultationDay();
        day.setDoctor(doctor);
        day.setRoom(room);
        day.setDate(date);
        day.setStartTime(LocalTime.of(8,0));
        day.setEndTime(LocalTime.of(12,0));
        day.setCreatedBy(createdBy);
        consultationDayRepository.save(day);
    }
}
