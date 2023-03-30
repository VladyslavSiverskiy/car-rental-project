package com.vsiver.spring.car_rent_project.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name = "cars")
public class Car {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "car_id")
    public Integer carId;

    @NotNull
    @Column(name = "model")
    public String carModel;

    @ManyToOne
    @JoinColumn(name = "category_id")
    public Category category;

    @Column(name = "gearbox_type")
    @Enumerated(EnumType.STRING)
    public EGearboxTypes gearboxType;

    @Column(name = "seats_count")
    public Integer seatsCount;

    @Column(name = "fuel_type")
    @Enumerated(EnumType.STRING)
    public EFuelType fuelType;

    @Column(name = "avg_fuel_consumption")
    public Double averageFuelConsumption;

    @Column(name = "day_rent_price")
    public Double dayRentPrice;

    @Column(name = "location_info")
    public String locationInfo;

    @Column(name = "in_stock")
    public Boolean inStock;


}
