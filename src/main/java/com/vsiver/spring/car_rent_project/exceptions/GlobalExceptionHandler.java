package com.vsiver.spring.car_rent_project.exceptions;

import com.vsiver.spring.car_rent_project.dtos.InfoMessage;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler
    public ResponseEntity<InfoMessage> handleException(ExpiredJwtException exception){
        InfoMessage data = new InfoMessage();
        data.setInfo(exception.getMessage());
        return new ResponseEntity<>(data, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler
    public ResponseEntity<InfoMessage> handleException(MalformedJwtException exception){
        InfoMessage data = new InfoMessage();
        data.setInfo(exception.getMessage());
        return new ResponseEntity<>(data, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler
    public ResponseEntity<InfoMessage> handleException(NoCarWithSuchIdException exception){
        InfoMessage data = new InfoMessage();
        data.setInfo(exception.getMessage());
        return new ResponseEntity<>(data, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    public ResponseEntity<InfoMessage> handleException(Exception exception){
        InfoMessage data = new InfoMessage();
        System.out.println(exception);
        data.setInfo(exception.toString());
        return new ResponseEntity<>(data, HttpStatus.BAD_REQUEST);
    }
}
