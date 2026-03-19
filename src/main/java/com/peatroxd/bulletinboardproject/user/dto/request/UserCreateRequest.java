package com.peatroxd.bulletinboardproject.user.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UserCreateRequest(
        @NotBlank
        @Size(max = 100)
        String username,

        @NotBlank
        @Email
        @Size(max = 255)
        String email,

        @Size(max = 100)
        String name,

        @Size(max = 30)
        String phone,

        @NotBlank
        @Size(min = 8, max = 128)
        String password
) {
}
