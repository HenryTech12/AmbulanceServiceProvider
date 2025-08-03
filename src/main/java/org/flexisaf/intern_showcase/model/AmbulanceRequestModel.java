package org.flexisaf.intern_showcase.model;

import jakarta.persistence.*;
import lombok.Data;
import org.flexisaf.intern_showcase.dto.AmbulanceDTO;
import org.flexisaf.intern_showcase.dto.UserDTO;

import java.time.LocalTime;

@Data
@Entity
public class AmbulanceRequestModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "user_model_id")
    private UserModel userModel;
    private String request_time;
    private LocalTime dispatch_time;
    private LocalTime arrivalTime;
    @Column(name = "dispatch_status")
    private String dispatchStatus;
    @ManyToOne
    @JoinColumn(name = "ambulance_model_id")
    private AmbulanceModel ambulanceModel;
    private String request_status;
    private String emergencyLevel;
    private String address;

}
