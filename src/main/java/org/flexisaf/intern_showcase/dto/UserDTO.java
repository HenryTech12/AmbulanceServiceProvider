package org.flexisaf.intern_showcase.dto;


import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Embeddable
public class UserDTO {

    private Long id;
    @NotNull(message = "full name can't be null")
    private String fullname;
    @NotNull(message = "contact can't be null")
    private String contactNum;
    @NotNull(message = "medical info can't be null")
    private String medicalDescription;
    @NotNull(message = "email can't be null")
    private String email;
    @NotNull(message = "password can't be null")
    private String password;
    private String role;
}
