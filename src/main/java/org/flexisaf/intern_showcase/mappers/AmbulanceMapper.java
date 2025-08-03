package org.flexisaf.intern_showcase.mappers;

import org.flexisaf.intern_showcase.dto.AmbulanceDTO;
import org.flexisaf.intern_showcase.dto.UserDTO;
import org.flexisaf.intern_showcase.model.AmbulanceModel;
import org.flexisaf.intern_showcase.model.UserModel;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class AmbulanceMapper {

    @Autowired
    private ModelMapper mapper;

    public AmbulanceModel convertToModel(AmbulanceDTO ambulanceDTO) {
        if(!Objects.isNull(ambulanceDTO))
            return mapper.map(ambulanceDTO, AmbulanceModel.class);
        else
            return null;
    }

    public AmbulanceDTO convertToDTO(AmbulanceModel ambulanceModel) {
        if(!Objects.isNull(ambulanceModel))
            return mapper.map(ambulanceModel, AmbulanceDTO.class);
        else
            return null;
    }
}
