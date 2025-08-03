package org.flexisaf.intern_showcase.response;

import lombok.Builder;
import lombok.Data;
import org.flexisaf.intern_showcase.dto.UserDTO;


@Data
@Builder
public class UserResponse {

    private String status;
    private String message;
    private UserDTO data;
}
