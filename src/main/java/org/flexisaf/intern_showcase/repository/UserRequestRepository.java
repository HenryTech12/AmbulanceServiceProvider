package org.flexisaf.intern_showcase.repository;

import org.flexisaf.intern_showcase.model.UserRequestModel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRequestRepository extends JpaRepository<UserRequestModel,Long> {

    List<UserRequestModel> findByRequestStatus(String requestStatus);
    long countByRequestStatus(String requestStatus);
}
