package org.flexisaf.intern_showcase.request;


import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LoginRequest {

    @NotBlank(message = "email can't be null")
    private String email;
    @NotBlank(message = "password can't be null")
    private String password;

}
