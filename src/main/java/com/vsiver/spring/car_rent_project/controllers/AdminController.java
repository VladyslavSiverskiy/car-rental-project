package com.vsiver.spring.car_rent_project.controllers;


import com.vsiver.spring.car_rent_project.dtos.CarDto;
import com.vsiver.spring.car_rent_project.dtos.InfoMessage;
import com.vsiver.spring.car_rent_project.exceptions.NoCarWithSuchIdException;
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

    @PostMapping("/cars")
    public ResponseEntity<CarDto> addCar(@RequestBody CarDto carDto){
        return ResponseEntity.ok(carService.saveOrUpdateCar(carDto));
    }

    @PutMapping("/cars")
    public ResponseEntity<CarDto> updateCar(@RequestBody CarDto carDto){
        return ResponseEntity.ok(carService.saveOrUpdateCar(carDto));
    }

    @DeleteMapping("/cars/{carId}")
    public ResponseEntity<InfoMessage> deleteCar(@PathVariable Integer carId) throws NoCarWithSuchIdException {
        InfoMessage infoMessage = new InfoMessage();
        carService.deleteCarById(carId);
        infoMessage.setInfo("Car with id " + carId + " was deleted");
        return ResponseEntity.ok(infoMessage);
    }
}
