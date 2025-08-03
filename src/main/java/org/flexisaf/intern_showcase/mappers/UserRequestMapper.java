package org.flexisaf.intern_showcase.mappers;

import org.flexisaf.intern_showcase.model.UserRequestModel;
import org.flexisaf.intern_showcase.request.UserRequest;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class UserRequestMapper {

    @Autowired
    private ModelMapper mapper;

    public UserRequestModel convertToModel(UserRequest userRequest) {
        if(!Objects.isNull(userRequest))
            return mapper.map(userRequest, UserRequestModel.class);
        else
            return null;
    }

    public UserRequest convertToDTO(UserRequestModel ambulanceRequest) {
        if(!Objects.isNull(ambulanceRequest))
            return mapper.map(ambulanceRequest, UserRequest.class);
        else
            return null;
    }
}
