package com.vsiver.spring.car_rent_project.exceptions;

import com.vsiver.spring.car_rent_project.dtos.InfoMessage;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(ExpiredJwtException.class)
    public ResponseEntity<InfoMessage> handleException(ExpiredJwtException exception){
        return responseFormatter(exception, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(MalformedJwtException.class)
    public ResponseEntity<InfoMessage> handleException(MalformedJwtException exception){
       return responseFormatter(exception, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler({
            NoCarWithSuchIdException.class,
            NoReviewWithSuchIdException.class,
            NoSuchUserException.class,
            NoUserWithSuchIdException.class,
            NoCarWithSuchIdException.class,
            NoOrderWithSuchIdException.class
    })
    public ResponseEntity<InfoMessage> handleIdNotFoundException(Exception exception){
        return responseFormatter(exception, HttpStatus.NOT_FOUND);
    }
    @ExceptionHandler({
            CarOutOfStockException.class,
            IncorrectRentTimeException.class
    })
    public ResponseEntity<InfoMessage> handleIdBadRequestException(Exception exception){
        return responseFormatter(exception, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    public ResponseEntity<InfoMessage> handleException(BadCredentialsException exception){
        return responseFormatter(exception, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler
    public ResponseEntity<InfoMessage> handleException(Exception exception){
       return responseFormatter(exception, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler
    public ResponseEntity<InfoMessage> handleException(RuntimeException exception){
        return responseFormatter(exception, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    public ResponseEntity<InfoMessage> responseFormatter(Exception exception, HttpStatus status) {
        InfoMessage data = new InfoMessage();
        data.setInfo(exception.getMessage());
        return new ResponseEntity<>(data, status);
    }
}
