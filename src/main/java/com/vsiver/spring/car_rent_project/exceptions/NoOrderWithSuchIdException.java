package com.vsiver.spring.car_rent_project.exceptions;

public class NoOrderWithSuchIdException extends RuntimeException {
    public NoOrderWithSuchIdException(String message) {
        super(message);
    }
}
