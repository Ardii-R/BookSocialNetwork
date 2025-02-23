package com.arra.book.authentication;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class RegistrationRequest {

  // First name, last name, email and password are the data required to register a user

    @NotEmpty(message = "Firstname is required")
    @NotNull(message = "Firstname is required")
    private String firstname;
    @NotEmpty(message = "Lastname is required")
    @NotNull(message = "Lastname is required")
    private String lastname;
    @Email(message = "Email is not well formatted")
    @NotEmpty(message = "Email is required")
    @NotNull(message = "Email is required")
    private String email;
    @NotEmpty(message = "Password is required")
    @NotNull(message = "Password is required")
    @Size(min = 8, message = "Password must be at least 8 characters long")
    private String password;
}


