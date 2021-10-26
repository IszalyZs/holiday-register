package com.oh.register.config;

import com.oh.register.exception.BindingException;
import com.oh.register.exception.RegisterException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.Objects;

@ControllerAdvice
public class MyControllerAdvice {
    private Logger log = LoggerFactory.getLogger(MyControllerAdvice.class);

    @ExceptionHandler(RegisterException.class)
    public ResponseEntity<String> registerExceptionHandler(RegisterException registerException) {
        log.error(registerException.getMessage());
        return new ResponseEntity<>(registerException.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(BindingException.class)
    public ResponseEntity<String> bindingExceptionHandler(BindingException bindingException) {
        log.error(bindingException.getMessage());
        return new ResponseEntity<>(bindingException.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<String> messageNotReadableExceptionHandler(HttpMessageNotReadableException formatException) {
        String error = Objects.requireNonNull(formatException.getRootCause()).getMessage();
        log.error(error);
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }
}
