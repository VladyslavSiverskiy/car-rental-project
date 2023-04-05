package com.vsiver.spring.car_rent_project.exceptions;

public class NoCarWithSuchIdException extends Exception{
    public NoCarWithSuchIdException(String message) {
        super(message);
    }
}
