package com.vsiver.spring.car_rent_project.utils;


import com.vsiver.spring.car_rent_project.dtos.CarDto;
import com.vsiver.spring.car_rent_project.dtos.ReviewDto;
import com.vsiver.spring.car_rent_project.entities.*;
import com.vsiver.spring.car_rent_project.repositories.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class CustomMappers {

    @Autowired
    private CategoryRepository categoryRepository;

    public CarDto mapCarToCarDto(Car car){
        return CarDto.fromCar(car);

/*      CarDto carDto = new CarDto();
        carDto.setCarId(car.getCarId());
        carDto.setModel(car.getCarModel());
        carDto.setDescription(car.getDescription());
        if(car.getCategory() != null) carDto.setCategoryName(car.getCategory().getCategoryName().name());
        if(car.getGearboxType() != null) carDto.setGearboxType(car.getGearboxType().name());
        carDto.setSeatsCount(car.getSeatsCount());
        carDto.setEngineVolume(car.getEngineVolume());
        if (car.getFuelType() != null) carDto.setFuelType(car.getFuelType().name());
        carDto.setAvgFuelConsumption(car.getAverageFuelConsumption());
        carDto.setDayRentalPrice(car.getDayRentPrice());
        carDto.setInStock(car.getInStock());
        carDto.setLocationInfo(car.getLocationInfo());
        carDto.setYearOfManufacturing(car.getYearOfManufacturing());
        carDto.setAvailableTo(car.getAvailableTo());
        if(car.getCarReviews() == null) return carDto;
        List<ReviewDto> reviewDto = car.getCarReviews().stream()
                .map(review -> new ReviewDto(
                        review.getId(),
                        review.getUser().getFirstName(),
                        review.getUser().getLastName(),
                        review.getDescription(),
                        review.getRate()
                )).collect(Collectors.toList());
        carDto.setReviews(reviewDto);
        return carDto;*/
    }


    public Car mapCarDtoToCar(CarDto carDto){
        Category category = null;
        if (carDto.getCategoryName().equalsIgnoreCase("SUV")) {
            category = categoryRepository.findByCategoryName(ECategories.SUV).get();
        } else if (carDto.getCategoryName().equalsIgnoreCase("BUSINESS")) {
            category = categoryRepository.findByCategoryName(ECategories.BUSINESS).get();
        } else if (carDto.getCategoryName().equalsIgnoreCase("ECONOMY")) {
            category = categoryRepository.findByCategoryName(ECategories.ECONOMY).get();
        } else if (carDto.getCategoryName().equalsIgnoreCase("COMFORT")){
            category = categoryRepository.findByCategoryName(ECategories.COMFORT).get();
        }

        EGearboxTypes gearboxType = carDto
                .getGearboxType()
                .equalsIgnoreCase("MANUAL") ?
                EGearboxTypes.MANUAL :
                EGearboxTypes.AUTOMATIC;

        EFuelType fuelType = null;
        if (carDto.getFuelType().equalsIgnoreCase("PETROL")){
            fuelType = EFuelType.PETROL;
        } else if(carDto.getFuelType().equalsIgnoreCase("ELECTRIC")){
            fuelType = EFuelType.ELECTRIC;
        } else if(carDto.getFuelType().equalsIgnoreCase("DIESEL")){
            fuelType = EFuelType.DIESEL;
        }
        System.out.println(fuelType);
        Car car = new Car();

        car.setCarId(carDto.getCarId());
        car.setCarModel(carDto.getModel());
        car.setCategory(category);
        car.setDescription(carDto.getDescription());
        car.setFuelType(fuelType);
        car.setEngineVolume(carDto.getEngineVolume());
        car.setDayRentPrice(carDto.getDayRentalPrice());
        car.setSeatsCount(carDto.getSeatsCount());
        car.setAverageFuelConsumption(carDto.getAvgFuelConsumption());
        car.setGearboxType(gearboxType);
        car.setYearOfManufacturing(carDto.getYearOfManufacturing());
        car.setLocationInfo(carDto.getLocationInfo());
        car.setInStock(carDto.getInStock());
        car.setAvailableTo(carDto.getAvailableTo());
        return car;
    }
}
