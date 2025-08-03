package org.flexisaf.intern_showcase.response;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class ErrorResponse {

    private String message;
    private Throwable throwable;
    private String timeStamp;
}
