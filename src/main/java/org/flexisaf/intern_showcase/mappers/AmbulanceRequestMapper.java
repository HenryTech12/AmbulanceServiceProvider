package org.flexisaf.intern_showcase.mappers;

import org.flexisaf.intern_showcase.model.AmbulanceRequestModel;
import org.flexisaf.intern_showcase.request.AmbulanceRequest;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class AmbulanceRequestMapper {

    @Autowired
    private ModelMapper mapper;


    public AmbulanceRequest convertToDTO(AmbulanceRequestModel ambulanceRequestModel) {
        if(!Objects.isNull(ambulanceRequestModel))
            return mapper.map(ambulanceRequestModel, AmbulanceRequest.class);
        else
            return null;
    }

    public AmbulanceRequestModel convertToModel(AmbulanceRequest ambulanceRequest) {
        if(!Objects.isNull(ambulanceRequest))
            return mapper.map(ambulanceRequest,AmbulanceRequestModel.class);
        else
            return null;
    }
}
