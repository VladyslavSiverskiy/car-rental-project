package com.vsiver.spring.car_rent_project.entities;

public enum EOrderState {
    IS_RESERVED, //customer ordered car but haven't paid for rent
    IN_PROCESS, //customer paid for the car and now use it
    FINISHED, //manager approved that car is returned
    EXPIRED, //customer didn't return the car before the reservation is ended
}
