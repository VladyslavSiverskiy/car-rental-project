package com.vsiver.spring.car_rent_project.services;


import com.vsiver.spring.car_rent_project.dtos.CarDto;
import com.vsiver.spring.car_rent_project.entities.*;
import com.vsiver.spring.car_rent_project.exceptions.NoCarWithSuchIdException;
import com.vsiver.spring.car_rent_project.repositories.CarRepository;
import com.vsiver.spring.car_rent_project.repositories.CategoryRepository;
import com.vsiver.spring.car_rent_project.utils.CustomMappers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;


@Service
@Transactional
public class CarService {

    @Autowired
    private CustomMappers customMappers;
    @Autowired
    private CarRepository carRepository;

    public List<CarDto> getAllCars() {
        return carRepository.findAll().stream().map(car -> customMappers.mapCarToCarDto(car))
                .collect(Collectors.toList());
    }

    public CarDto saveOrUpdateCar(CarDto carDto) {
        Car car = customMappers.mapCarDtoToCar(carDto);
        System.out.println(car);
        carRepository.save(car);
        return customMappers.mapCarToCarDto(car);
    }

    public void deleteCarById(Integer carId) throws NoCarWithSuchIdException {
        Car car = carRepository.findById(carId).orElseThrow(() -> new NoCarWithSuchIdException("No car with such id"));
        carRepository.delete(car);
    }

    public CarDto findById(Integer carId) throws NoCarWithSuchIdException {
        Car car = carRepository.findById(carId).orElseThrow(() -> new NoCarWithSuchIdException("No car with such id"));
        return customMappers.mapCarToCarDto(car);
    }
}
