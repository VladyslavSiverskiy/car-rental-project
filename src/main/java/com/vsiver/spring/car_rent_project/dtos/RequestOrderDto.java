package com.vsiver.spring.car_rent_project.dtos;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class RequestOrderDto {
    private Double totalAmount;
    private LocalDateTime rentFrom;
    private LocalDateTime rentTo;
    private Integer userId;
    private Integer carId;
}
