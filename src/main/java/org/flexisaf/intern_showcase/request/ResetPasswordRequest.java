package org.flexisaf.intern_showcase.request;

import lombok.Data;

@Data
public class ResetPasswordRequest {

    private String email;
    private String newPassword;
}
