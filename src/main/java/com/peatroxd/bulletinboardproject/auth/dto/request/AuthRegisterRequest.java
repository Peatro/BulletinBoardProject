package com.peatroxd.bulletinboardproject.auth.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record AuthRegisterRequest(
        @NotBlank
        @Size(min = 3, max = 255)
        String username,

        @NotBlank
        @Email
        @Size(max = 255)
        String email,

        @NotBlank
        @Size(max = 255)
        String firstName,

        @Size(max = 255)
        String lastName,

        @Size(max = 30)
        String phone,

        @NotBlank
        @Size(min = 8, max = 128)
        String password
) {
}
