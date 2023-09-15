package com.vsiver.spring.car_rent_project.controllers;


import com.vsiver.spring.car_rent_project.dtos.CarDto;
import com.vsiver.spring.car_rent_project.dtos.InfoMessage;
import com.vsiver.spring.car_rent_project.dtos.OrderDto;
import com.vsiver.spring.car_rent_project.exceptions.NoCarWithSuchIdException;
import com.vsiver.spring.car_rent_project.services.S3Service;
import com.vsiver.spring.car_rent_project.services.CarService;
import com.vsiver.spring.car_rent_project.services.OrderService;
import com.vsiver.spring.car_rent_project.utils.CustomMappers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@RestController
//@CrossOrigin(origins = "http://localhost:3000")
@RequestMapping("/api/v1/admin")
public class AdminController {


    @Autowired
    private S3Service s3Service;
    @Autowired
    private CarService carService;

    @Autowired
    private OrderService orderService;

    private final Logger somelogger = LoggerFactory.getLogger(AdminController.class);

    @GetMapping("/check")
    public ResponseEntity<Boolean> checkAdminState() {
        return ResponseEntity.ok(true);
    }


    @PostMapping(value = "/cars/upload/{carId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Boolean> uploadCarPicture(
            @RequestParam("file") MultipartFile file,
            @PathVariable Integer carId
    ) throws IOException {
        s3Service.putObject(
                "car-app-bucket",
                "cars/car" + carId + "/main.jpg",
                file.getBytes()
        );
        return ResponseEntity.ok(true);
    }

    @PostMapping("/cars")
    public ResponseEntity<CarDto> addCar(@RequestBody CarDto carDto) {
        logger.info("Attempt to create car in DB.");
        return ResponseEntity.ok(carService.saveOrUpdateCar(carDto));
    }

    @PutMapping("/cars")
    public ResponseEntity<CarDto> updateCar(@RequestBody CarDto carDto) {
        logger.info("Attempt to update car with ID " + carDto.getCarId() +  " in DB.");
        return ResponseEntity.ok(carService.saveOrUpdateCar(carDto));
    }

    @DeleteMapping("/cars/{carId}")
    public ResponseEntity<InfoMessage> deleteCar(@PathVariable Integer carId) throws NoCarWithSuchIdException {
        InfoMessage infoMessage = new InfoMessage();
        carService.deleteCarById(carId);
        s3Service.deleteFolder("car-app-bucket", "cars/car" + carId + "/");
        infoMessage.setInfo("Car with id " + carId + " was deleted");
        return ResponseEntity.ok(infoMessage);
    }

    @GetMapping("/orders")
    public ResponseEntity<List<OrderDto>> retrieveAllOrders(){
        return ResponseEntity.ok(orderService.getAllOrders()
                .stream()
                .map(CustomMappers::mapOrderToOrderDto)
                .sorted(descOrderDateComparator())
                .toList()
        );
    }

    private Comparator<OrderDto> descOrderDateComparator(){
        return Comparator.comparing(OrderDto::getCreationTime);
    }

    /**
     * Submit order (car was returned)
     */
    @GetMapping("/orders/submit/{orderId}")
    public ResponseEntity<InfoMessage> submitOrder(@PathVariable Long orderId) throws NoCarWithSuchIdException {
        orderService.submitOrder(orderId);
        InfoMessage infoMessage = new InfoMessage();
        infoMessage.setInfo("Order with id " + orderId + " was submitted");
        return ResponseEntity.ok(infoMessage);
    }
}
