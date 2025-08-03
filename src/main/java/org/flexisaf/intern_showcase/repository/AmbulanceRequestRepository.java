package org.flexisaf.intern_showcase.repository;

import org.flexisaf.intern_showcase.model.AmbulanceRequestModel;
import org.flexisaf.intern_showcase.request.AmbulanceRequest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalTime;
import java.util.List;

public interface AmbulanceRequestRepository extends JpaRepository<AmbulanceRequestModel,Long> {

    List<AmbulanceRequestModel> findByArrivalTimeLessThanEqual(LocalTime arrivalTime);
    long countByDispatchStatus(String dispatchStatus);
}
