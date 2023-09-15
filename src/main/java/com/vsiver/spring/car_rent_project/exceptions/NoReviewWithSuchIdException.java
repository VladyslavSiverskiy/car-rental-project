package com.vsiver.spring.car_rent_project.exceptions;

public class NoReviewWithSuchIdException extends RuntimeException {
    public NoReviewWithSuchIdException(String s) {
        super(s);
    }
}
