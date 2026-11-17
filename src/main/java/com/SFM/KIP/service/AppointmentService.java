package com.SFM.KIP.service;

import com.SFM.KIP.dto.AppointmentSlot;
import com.SFM.KIP.entity.*;
import com.SFM.KIP.repository.*;

import jakarta.persistence.EntityNotFoundException;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class AppointmentService {

    private static final int SLOT_DURATION = 30;
    private static final int SEARCH_DAYS = 90;

    private final AppointmentRepository appointmentRepository;
    private final ConsultationDayRepository consultationDayRepository;
    private final UserRepository userRepository;
    private final DepartmentRepository departmentRepository;

    public AppointmentService(
            AppointmentRepository appointmentRepository,
            ConsultationDayRepository consultationDayRepository,
            UserRepository userRepository,
            DepartmentRepository departmentRepository
    ) {
        this.appointmentRepository = appointmentRepository;
        this.consultationDayRepository = consultationDayRepository;
        this.userRepository = userRepository;
        this.departmentRepository = departmentRepository;
    }

    public List<Department> getDepartments() {
        return departmentRepository.findAll();
    }

    public List<User> getDoctors(Long departmentId) {

        if (!departmentRepository.existsById(departmentId)) {
            throw new EntityNotFoundException("A részleg nem található.");
        }

        return userRepository.findActiveDoctorsByDepartmentId(departmentId);
    }

    public List<AppointmentSlot> getAvailableSlots(Long doctorId) {

        User doctor = userRepository.findById(doctorId)
                .orElseThrow(() ->
                        new EntityNotFoundException("Az orvos nem található."));

        if (!doctor.isActive()
                || doctor.getRole() == null
                || !"DOCTOR".equals(doctor.getRole().getName())) {
            throw new IllegalArgumentException("A kiválasztott orvos nem elérhető.");
        }

        LocalDate today = LocalDate.now();
        LocalDate lastDay = today.plusDays(SEARCH_DAYS);

        List<ConsultationDay> consultationDays =
                consultationDayRepository.findAvailableConsultationDays(
                        doctorId,
                        today,
                        lastDay
                );

        List<AppointmentSlot> availableSlots = new ArrayList<>();

        LocalDateTime now = LocalDateTime.now();

        for (ConsultationDay day : consultationDays) {

            LocalDate date = day.getDate();

            LocalTime currentTime = day.getStartTime();
            LocalTime endTime = day.getEndTime();

            while (!currentTime.plusMinutes(SLOT_DURATION).isAfter(endTime)) {

                LocalDateTime slotStart = LocalDateTime.of(date, currentTime);
                LocalDateTime slotEnd = slotStart.plusMinutes(SLOT_DURATION);

                if (slotStart.isAfter(now)) {

                    boolean occupied =
                            appointmentRepository.isSlotOccupied(
                                    doctorId,
                                    slotStart,
                                    slotEnd,
                                    AppointmentStatus.CANCELLED
                            );

                    if (!occupied) {

                        String label = slotStart.toString()
                                .replace("T", " ");

                        availableSlots.add(
                                new AppointmentSlot(slotStart, label)
                        );
                    }
                }

                currentTime = currentTime.plusMinutes(SLOT_DURATION);
            }
        }

        return availableSlots;
    }

    @Transactional
    public void bookAppointment(
            String email,
            Long doctorId,
            LocalDateTime startTime
    ) {

        User patient = getPatient(email);

        User doctor = userRepository.findById(doctorId)
                .orElseThrow(() ->
                        new EntityNotFoundException("Az orvos nem található."));

        if (!doctor.isActive()
                || doctor.getRole() == null
                || !"DOCTOR".equals(doctor.getRole().getName())) {
            throw new IllegalArgumentException("A kiválasztott orvos nem elérhető.");
        }

        LocalDate date = startTime.toLocalDate();

        List<ConsultationDay> days =
                consultationDayRepository.findAvailableConsultationDays(
                        doctorId,
                        date,
                        date
                );

        LocalDateTime endTime = startTime.plusMinutes(SLOT_DURATION);

        ConsultationDay selectedDay = days.stream()
                .filter(day ->
                        !startTime.toLocalTime().isBefore(day.getStartTime())
                                && !endTime.toLocalTime().isAfter(day.getEndTime()))
                .filter(day ->
                        !startTime.isBefore(LocalDateTime.now()))
                .filter(day ->
                        startTime.getMinute() % SLOT_DURATION == 0
                                && startTime.getSecond() == 0
                                && startTime.getNano() == 0)
                .filter(day ->
                        !appointmentRepository.isSlotOccupied(
                                doctorId,
                                startTime,
                                endTime,
                                AppointmentStatus.CANCELLED
                        ))
                .findFirst()
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "A kiválasztott időpont már nem elérhető."
                        ));

        Appointment appointment = new Appointment();

        appointment.setPatient(patient);
        appointment.setDoctor(doctor);
        appointment.setConsultationDay(selectedDay);
        appointment.setRoom(selectedDay.getRoom());
        appointment.setStartTime(startTime);
        appointment.setEndTime(endTime);
        appointment.setStatus(AppointmentStatus.BOOKED);

        appointmentRepository.save(appointment);
    }

    public List<Appointment> getMyAppointments(String email) {

        User patient = getPatient(email);

        return appointmentRepository.findPatientAppointments(patient.getId());
    }

    @Transactional
    public void cancelAppointment(String email, Long appointmentId) {

        User patient = getPatient(email);

        Appointment appointment = appointmentRepository
                .findByIdAndPatient_Id(appointmentId, patient.getId())
                .orElseThrow(() ->
                        new EntityNotFoundException("Az időpont nem található."));

        if (appointment.getStatus() == AppointmentStatus.CANCELLED) {
            throw new IllegalArgumentException("Ez az időpont már le van mondva.");
        }

        appointment.setStatus(AppointmentStatus.CANCELLED);

        appointmentRepository.save(appointment);
    }

    private User getPatient(String email) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new EntityNotFoundException("A felhasználó nem található."));

        if (user.getRole() == null
                || !"USER".equals(user.getRole().getName())) {
            throw new AccessDeniedException(
                    "Ehhez a művelethez beteg jogosultság szükséges."
            );
        }

        return user;
    }
}