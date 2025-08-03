package org.flexisaf.intern_showcase.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.flexisaf.intern_showcase.dto.UserDTO;

import java.util.Map;

@Data
@Entity
public class UserRequestModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "user_model_id")
    private UserModel userModel;
    private String address;
    private String latitude;
    private String longtitude;
    private String requestStatus;
    private String request_time;
    @Column(columnDefinition = "TEXT")
    private String emergencyDescription;
    private String emergencyLevel;
}
