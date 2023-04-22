package com.vsiver.spring.car_rent_project.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "cars")
public class Car {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "car_id")
    private Integer carId;

    @NotNull
    @Column(name = "model")
    private String carModel;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;

    @Column(name = "year_of_manufacturing")
    private Integer yearOfManufacturing;

    @Column(name = "gearbox_type")
    @Enumerated(EnumType.STRING)
    private EGearboxTypes gearboxType;

    @Column(name = "seats_count")
    private Integer seatsCount;

    @Column(name = "engine_volume")
    private Double engineVolume;

    @Column(name = "fuel_type")
    @Enumerated(EnumType.STRING)
    private EFuelType fuelType;

    @Column(name = "avg_fuel_consumption")
    private Double averageFuelConsumption;

    @Column(name = "day_rent_price")
    private Double dayRentPrice;

    @Column(name = "location_info")
    private String locationInfo;

    @Column(name = "in_stock")
    private Boolean inStock;

    @Column(name = "available_to")
    private LocalDateTime availableTo;

    @OneToMany(mappedBy = "car")
    List<Order> carOrderList;

    @OneToMany(mappedBy = "car")
    List<Review> carReviews;
    public Car() {

    }

    public void setCarOrderList(List<Order> carOrderList) {
        this.carOrderList = carOrderList;
    }

    public List<Review> getCarReviews() {
        return carReviews;
    }

    public void setCarReviews(List<Review> carReviews) {
        this.carReviews = carReviews;
    }

    public LocalDateTime getAvailableTo() {
        return availableTo;
    }

    public void setAvailableTo(LocalDateTime availableTo) {
        this.availableTo = availableTo;
    }

    public List<Order> getCarOrderList() {
        return carOrderList;
    }

    public Integer getCarId() {
        return carId;
    }

    public void setCarId(Integer carId) {
        this.carId = carId;
    }

    public String getCarModel() {
        return carModel;
    }

    public void setCarModel(String carModel) {
        this.carModel = carModel;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public EGearboxTypes getGearboxType() {
        return gearboxType;
    }

    public void setGearboxType(EGearboxTypes gearboxType) {
        this.gearboxType = gearboxType;
    }

    public Integer getSeatsCount() {
        return seatsCount;
    }

    public void setSeatsCount(Integer seatsCount) {
        this.seatsCount = seatsCount;
    }

    public Double getEngineVolume() {
        return engineVolume;
    }

    public void setEngineVolume(Double engineVolume) {
        this.engineVolume = engineVolume;
    }

    public Integer getYearOfManufacturing() {
        return yearOfManufacturing;
    }

    public void setYearOfManufacturing(Integer yearOfManufacturing) {
        this.yearOfManufacturing = yearOfManufacturing;
    }

    public EFuelType getFuelType() {
        return fuelType;
    }

    public void setFuelType(EFuelType fuelType) {
        this.fuelType = fuelType;
    }

    public Double getAverageFuelConsumption() {
        return averageFuelConsumption;
    }

    public void setAverageFuelConsumption(Double averageFuelConsumption) {
        this.averageFuelConsumption = averageFuelConsumption;
    }

    public Double getDayRentPrice() {
        return dayRentPrice;
    }

    public void setDayRentPrice(Double dayRentPrice) {
        this.dayRentPrice = dayRentPrice;
    }

    public String getLocationInfo() {
        return locationInfo;
    }

    public void setLocationInfo(String locationInfo) {
        this.locationInfo = locationInfo;
    }

    public Boolean getInStock() {
        return inStock;
    }

    public void setInStock(Boolean inStock) {
        this.inStock = inStock;
    }

    @Override
    public String toString() {
        return "Car{" +
               "carId=" + carId +
               ", carModel='" + carModel + '\'' +
               ", yearOfManufacturing=" + yearOfManufacturing +
               ", gearboxType=" + gearboxType +
               ", seatsCount=" + seatsCount +
               ", engineVolume=" + engineVolume +
               ", fuelType=" + fuelType +
               ", averageFuelConsumption=" + averageFuelConsumption +
               ", dayRentPrice=" + dayRentPrice +
               ", locationInfo='" + locationInfo + '\'' +
               ", inStock=" + inStock +
               '}';
    }
}
