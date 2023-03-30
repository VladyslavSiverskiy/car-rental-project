package com.vsiver.spring.car_rent_project.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Car {

    public String model;
    public String categoryName;
    public String gearboxType;
    public Integer seatsCount;
    public Double engineVolume;
    public String fuelType;
    public Double avgFuelConsumption;
    public Double dayRentalPrice;
    public String locationInfo;
    public Boolean inStock;
}
