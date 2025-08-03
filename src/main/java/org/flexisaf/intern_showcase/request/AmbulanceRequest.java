package org.flexisaf.intern_showcase.request;

import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.flexisaf.intern_showcase.dto.AmbulanceDTO;
import org.flexisaf.intern_showcase.dto.UserDTO;

import java.time.LocalTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AmbulanceRequest {

    private Long id;
    private UserDTO userDTO;
    private String request_time;
    private LocalTime dispatch_time;
    private LocalTime arrivalTime;
    private String dispatch_status;
    private AmbulanceDTO ambulanceDTO;
    private String request_status;
    private String emergencyLevel;
    private String address;
    private String error;
}
