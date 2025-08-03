package org.flexisaf.intern_showcase.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.flexisaf.intern_showcase.dto.RequestStatus;
import org.flexisaf.intern_showcase.dto.UserDTO;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserRequest {

    private UserDTO userDTO;
    private Long id;
    @NotNull(message = "address can't be null")
    private String address;
    private String latitude;
    private String longtitude;
    private String requestStatus;
    private String request_time;
    @NotBlank(message = "emergency description can't be null")
    private String emergencyDescription;
    @NotBlank(message = "emergency level can't be null")
    private String emergencyLevel;

}
