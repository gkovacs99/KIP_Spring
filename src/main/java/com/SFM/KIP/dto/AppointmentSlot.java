package com.SFM.KIP.dto;

import java.time.LocalDateTime;

public record AppointmentSlot(
        LocalDateTime startTime,
        String label
) { }
