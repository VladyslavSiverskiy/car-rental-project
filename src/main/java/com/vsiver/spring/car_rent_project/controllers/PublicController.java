package com.vsiver.spring.car_rent_project.controllers;

import com.vsiver.spring.car_rent_project.dtos.CarDto;
import com.vsiver.spring.car_rent_project.exceptions.NoCarWithSuchIdException;
import com.vsiver.spring.car_rent_project.services.CarService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/public")
public class PublicController {

    @Autowired
    private CarService carService;

    @GetMapping("/cars")
    public List<CarDto> getAllCars(){
        return carService.getAllCars();
    }

    @GetMapping("/cars/{carId}")
    public ResponseEntity<CarDto> getCarById(@PathVariable Integer carId) throws NoCarWithSuchIdException {
        CarDto carDto = carService.findById(carId);
        return ResponseEntity.ok(carDto);
    }
}
