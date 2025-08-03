package org.flexisaf.intern_showcase.exceptionhandler;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.MailSendException;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class ApiException {

    @ExceptionHandler(value = NullPointerException.class)
    public ResponseEntity<ErrorResponse> handleNullPointerException(NullPointerException nullPointerException) {
        ErrorResponse errorResponse =
                ErrorResponse.builder(nullPointerException.getCause(),
                        HttpStatusCode.valueOf(404),nullPointerException.getMessage()).build();
        return new ResponseEntity<>(errorResponse,HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Map<String, String> handleMethodArgumentException(MethodArgumentNotValidException e) {
        Map<String, String> errors = new HashMap<>();
        e.getFieldErrors().forEach(error -> {
            errors.put(error.getField(), new ResponseEntity<>(error.getDefaultMessage(), HttpStatus.BAD_REQUEST).getBody());
        });
        ErrorResponse errorResponse =
                ErrorResponse.builder(e.getCause(),
                        HttpStatusCode.valueOf(404),errors.toString()).build();

        return errors;
    }


    @ExceptionHandler(MailSendException.class)
    public ResponseEntity<ErrorResponse> handleMessageException(MailSendException mailSendException) {
        ErrorResponse errorResponse =
                ErrorResponse.builder(mailSendException.getCause(),
                        HttpStatusCode.valueOf(404),mailSendException.getMessage()).build();
        return new ResponseEntity<>(errorResponse,HttpStatus.REQUEST_TIMEOUT);
    }
}
