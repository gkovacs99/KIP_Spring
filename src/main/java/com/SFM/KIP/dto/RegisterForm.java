package com.SFM.KIP.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterForm(

        @NotBlank(message = "A név megadása kötelező")
        String name,

        @NotBlank(message = "Az e-mail megadása kötelező")
        @Email(message = "Érvénytelen e-mail cím")
        String email,

        @NotBlank(message = "A telefonszám megadása kötelező")
        String phone,

        @NotBlank(message = "A jelszó megadása kötelező")
        @Size(min = 12, message = "A jelszó legalább 12 karakter legyen")
        String password,

        @NotBlank(message = "A jelszó megerősítése kötelező")
        String confirmPassword

) {}