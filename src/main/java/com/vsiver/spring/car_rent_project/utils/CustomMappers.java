package com.vsiver.spring.car_rent_project.utils;


import com.vsiver.spring.car_rent_project.dtos.CarDto;
import com.vsiver.spring.car_rent_project.dtos.OrderDto;
import com.vsiver.spring.car_rent_project.entities.*;
import com.vsiver.spring.car_rent_project.repositories.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CustomMappers {

    @Autowired
    private CategoryRepository categoryRepository;

    public CarDto mapCarToCarDto(Car car){
        return CarDto.fromCar(car);
    }


    public Car mapCarDtoToCar(CarDto carDto){
        System.out.println(carDto);
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

    public static OrderDto mapOrderToOrderDto(Order order){
        return OrderDto.builder()
                .orderId(order.getId())
                .carModel(order.getCar().getCarModel())
                .userId(Long.valueOf(order.getUser().getId()))
                .orderState(order.getOrderState())
                .creationTime(order.getCreationTime())
                .rentFrom(order.getRentFrom())
                .rentTo(order.getRentTo())
                .orderSum(order.getOrderSum())
                .isPayed(order.isPayed())
                .payPalOrderId(order.getPayPalOrderId())
                .paymentReference(order.getPaymentReference())
                .build();
    }
}
