package com.vsiver.spring.car_rent_project.controllers;


import com.vsiver.spring.car_rent_project.dtos.Car;
import com.vsiver.spring.car_rent_project.dtos.InfoMessage;
import com.vsiver.spring.car_rent_project.services.CarService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/admin")
public class AdminController {

    @Autowired
    private CarService carService;

    @GetMapping("/cars")
    public List<Car> getAllCars(){
        return null;
    }

    @PostMapping("/cars")
    public ResponseEntity<Car> addCar(@RequestBody Car car){
        carService.saveCar();
        return null;
    }

    @PutMapping("/cars")
    public ResponseEntity<Car> updateCar(@RequestBody Car car){
        return null;
    }

    @DeleteMapping("/cars/{carId}")
    public ResponseEntity<InfoMessage> deleteCar(@PathVariable Integer carId){
        return null;
    }
}
