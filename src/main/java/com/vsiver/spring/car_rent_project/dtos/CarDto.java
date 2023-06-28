package com.vsiver.spring.car_rent_project.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CarDto {

    private Integer carId;
    private String model;
    private String categoryName;
    private String description;
    private String gearboxType;
    private Integer seatsCount;
    private Double engineVolume;
    private String fuelType;
    private Double avgFuelConsumption;
    private Integer yearOfManufacturing;
    private Double dayRentalPrice;
    private String locationInfo;
    private Boolean inStock;
    private LocalDateTime availableTo;
    private List<ReviewDto> reviews;

}
