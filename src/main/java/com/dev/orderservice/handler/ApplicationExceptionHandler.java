package com.dev.orderservice.handler;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class ApplicationExceptionHandler{

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Map<String, String>> resourceNotFoundException(){
        Map<String, String> map = new HashMap<>();
        map.put("message", "Resource Not Found");
        return new ResponseEntity<>(map, HttpStatus.BAD_REQUEST);
    }
}
