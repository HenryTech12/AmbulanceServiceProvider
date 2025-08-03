package org.flexisaf.intern_showcase.repository;

import org.flexisaf.intern_showcase.dto.AmbulanceDTO;
import org.flexisaf.intern_showcase.model.AmbulanceModel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AmbulanceRepository extends JpaRepository<AmbulanceModel,Long> {

    List<AmbulanceModel> findByAvailabilityStatus(String availabilityStatus);
}
