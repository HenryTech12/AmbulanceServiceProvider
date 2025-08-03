package org.flexisaf.intern_showcase.dto;

import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.Map;

@Data
@Embeddable
public class AmbulanceDTO {

    private Long id;
    @NotNull(message = "your ambulance ID can't be null")
    private String ambulance_id;
    @NotNull(message = "your location can't be null")
    private String current_location;
    @NotNull(message = "your availability status can't be null")
    private String availabilityStatus;

}
