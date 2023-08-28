package com.vsiver.spring.car_rent_project.dtos;

import com.vsiver.spring.car_rent_project.entities.Car;
import com.vsiver.spring.car_rent_project.utils.CustomMappers;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

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

    public static CarDto fromCar(Car car) {
        CarDtoBuilder builder = builder();
        builder.carId(car.getCarId())
                .model(car.getCarModel())
                .description(car.getDescription())
                .categoryName(car.getCategory() != null ? car.getCategory().getCategoryName().name() : null)
                .gearboxType(car.getGearboxType() != null ? car.getGearboxType().name() : null)
                .seatsCount(car.getSeatsCount())
                .engineVolume(car.getEngineVolume())
                .fuelType(car.getFuelType() != null ? car.getFuelType().name() : null)
                .avgFuelConsumption(car.getAverageFuelConsumption())
                .dayRentalPrice(car.getDayRentPrice())
                .inStock(car.getInStock())
                .locationInfo(car.getLocationInfo())
                .yearOfManufacturing(car.getYearOfManufacturing())
                .availableTo(car.getAvailableTo());

        if (car.getCarReviews() != null) {
            List<ReviewDto> reviewDto = car.getCarReviews().stream()
                    .map(CustomMappers::mapReviewToReviewDto)
                    .toList();
            builder.reviews(reviewDto);
        }

        return builder.build();
    }
}
