package com.vsiver.spring.car_rent_project.exceptions;

public class NoUserWithSuchIdException extends Exception{
    public NoUserWithSuchIdException(String message) {
        super(message);
    }
}
