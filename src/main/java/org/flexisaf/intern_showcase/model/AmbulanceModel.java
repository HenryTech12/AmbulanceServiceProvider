package org.flexisaf.intern_showcase.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.Map;

@Data
@Entity
public class AmbulanceModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true)
    private String ambulance_id;
    private String current_location;
    private String availabilityStatus;

}
