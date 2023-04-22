package com.vsiver.spring.car_rent_project.controllers;

import com.vsiver.spring.car_rent_project.dtos.CarDto;
import com.vsiver.spring.car_rent_project.exceptions.CarOutOfStockException;
import com.vsiver.spring.car_rent_project.exceptions.IncorrectRentTimeException;
import com.vsiver.spring.car_rent_project.exceptions.NoCarWithSuchIdException;
import com.vsiver.spring.car_rent_project.exceptions.NoUserWithSuchIdException;
import com.vsiver.spring.car_rent_project.services.CarService;
import com.vsiver.spring.car_rent_project.services.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@RestController
@RequestMapping("/api/v1/public")
public class PublicController {

    @Autowired
    private CarService carService;

    //TODO:Remove order service from here
    @Autowired
    private OrderService orderService;

    @GetMapping("/cars")
    public List<CarDto> getAllCars(){
        return carService.getAllCars();
    }

    @GetMapping("/cars/{carId}")
    public ResponseEntity<CarDto> getCarById(@PathVariable Integer carId) throws NoCarWithSuchIdException {
        CarDto carDto = carService.findById(carId);
        return ResponseEntity.ok(carDto);
    }


    @GetMapping("/cars/test/{from}/{to}")
    public void getCarById(@PathVariable String from, @PathVariable String to) throws CarOutOfStockException,
            NoUserWithSuchIdException,
            NoCarWithSuchIdException, IncorrectRentTimeException {
        //2023-04-12T23:00:10
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
        LocalDateTime dateFrom = LocalDateTime.parse(from, formatter);
        LocalDateTime dateTo = LocalDateTime.parse(to, formatter);


        orderService.orderCar(1,3, dateFrom, dateTo);
    }

}
