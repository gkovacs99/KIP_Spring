package com.SFM.KIP.init;

import com.SFM.KIP.entity.*;
import com.SFM.KIP.repository.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@Order(1)
public class DataInitializer implements CommandLineRunner {

    private final RoleRepository roleRepository;
    private final DepartmentRepository departmentRepository;
    private final SpecializationRepository specializationRepository;
    private final KeywordRepository keywordRepository;
    private final RoomRepository roomRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${kip.admin.email:admin@kip.hu}")
    private String adminEmail;

    @Value("${kip.admin.password:admin123}")
    private String adminPassword;

    public DataInitializer(RoleRepository roleRepository,
                           DepartmentRepository departmentRepository,
                           SpecializationRepository specializationRepository,
                           KeywordRepository keywordRepository,
                           RoomRepository roomRepository,
                           UserRepository userRepository,
                           PasswordEncoder passwordEncoder){
        this.roleRepository = roleRepository;
        this.departmentRepository = departmentRepository;
        this. specializationRepository = specializationRepository;
        this.keywordRepository = keywordRepository;
        this.roomRepository = roomRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }
    @Override
    @Transactional
    public void run(String... args){
        if (roleRepository.count() > 0){
            return; //Már be van töltve
        }
// --- Jogkörök ---
        createRole("USER", "Páciens", 1);
        createRole("NURSE", "Ápoló", 2);
        createRole("DOCTOR", "Orvos", 3);
        createRole("SENIOR", "Főorvos", 4);
        Role director = createRole("DIRECTOR", "Igazgató", 5);

        // --- Osztályok és rendelők ---
        Department belgyogyaszat = createDepartment("Belgyógyászat");
        Department sebeszet = createDepartment("Sebészet");
        Department kardiologia = createDepartment("Kardiológia");

        createRoom("B-101", belgyogyaszat);
        createRoom("B-102", belgyogyaszat);
        createRoom("S-201", sebeszet);
        createRoom("K-301", kardiologia);

        // --- Szakterületek és kulcsszavak ---
        Specialization belgyogyasz = createSpecialization("Belgyógyász",
                "Belső szervek betegségei", List.of("cukorbetegség", "magas vérnyomás", "gyomor", "máj"));
        Specialization ortoped = createSpecialization("Ortopéd",
                "Csontok és ízületek", List.of("térd", "csípő", "gerinc", "hát", "váll"));
        Specialization kardiologus = createSpecialization("Kardiológus",
                "Szív és érrendszer", List.of("szív", "szívritmus", "mellkasi fájdalom", "érszűkület"));

        // --- Első igazgató ---
        User admin = new User();
        admin.setName("Rendszergazda");
        admin.setEmail(adminEmail);
        admin.setPassword(passwordEncoder.encode(adminPassword));
        admin.setRole(director);
        userRepository.save(admin);

        System.out.println(">>> Törzsadatok betöltve, igazgatói fiók: " + adminEmail);
    }

    private Role createRole(String name, String description, int level){
        Role r = new Role();
        r.setName(name);
        r.setDescription(description);
        r.setLevel(level);
        return roleRepository.save(r);
    }

    private Department createDepartment(String name){
        Department d = new Department();
        d.setName(name);
        return departmentRepository.save(d);
    }

    private void createRoom(String name, Department department) {
        Room r = new Room();
        r.setName(name);
        r.setDepartment(department);
        roomRepository.save(r);
    }

    private Specialization createSpecialization(String name, String description, List<String> keywords) {
        Specialization s = new Specialization();
        s.setName(name);
        s.setDescription(description);
        specializationRepository.save(s);
        for (String word : keywords) {
            Keyword k = new Keyword();
            k.setWord(word);
            k.setSpecialization(s);
            keywordRepository.save(k);
        }
        return s;
    }
}
