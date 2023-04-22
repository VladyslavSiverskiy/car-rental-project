package com.vsiver.spring.car_rent_project.exceptions;

public class CarOutOfStockException extends Exception{
    public CarOutOfStockException(String message) {
        super(message);
    }
}
