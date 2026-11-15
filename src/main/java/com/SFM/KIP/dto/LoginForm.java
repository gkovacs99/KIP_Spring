package com.SFM.KIP.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record LoginForm (

    @NotBlank(message = "Az e-mail cím megadása kötelező")
    @Email(message = "Érvénytelen e-mail cím")
    String email,

    @NotBlank(message = "A jelszó megadása kötelező")
    String password

) {}
