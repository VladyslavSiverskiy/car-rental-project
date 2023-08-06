package com.vsiver.spring.car_rent_project.controllers;

import com.vsiver.spring.car_rent_project.dtos.CarDto;
import com.vsiver.spring.car_rent_project.dtos.CreatedOrderDto;
import com.vsiver.spring.car_rent_project.dtos.RequestOrderDto;
import com.vsiver.spring.car_rent_project.exceptions.CarOutOfStockException;
import com.vsiver.spring.car_rent_project.exceptions.IncorrectRentTimeException;
import com.vsiver.spring.car_rent_project.exceptions.NoCarWithSuchIdException;
import com.vsiver.spring.car_rent_project.exceptions.NoUserWithSuchIdException;
import com.vsiver.spring.car_rent_project.services.*;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/api/v1/public")
public class PublicController {

    @Autowired
    private S3Service s3Service;
    @Autowired
    private CarService carService;
    @Autowired
    private EmailService emailService;

    //TODO:Remove order service from here
    @Autowired
    private OrderService orderService;

    @Autowired
    private PaymentService paymentService;

    @GetMapping("/cars/{carId}/picture")
    public String downloadPicture(@PathVariable Integer carId) throws IOException {
        var resp = s3Service.downloadObject(
                "car-app-bucket",
                "cars/car" + carId + "/main.jpg"
        );

        return Base64.getEncoder().encodeToString(resp);
    }


    @GetMapping("/cars")
    public List<CarDto> getAllCars() {
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
        BigDecimal orderSum = BigDecimal.valueOf(15000);

        orderService.orderCar(1, 3, dateFrom, dateTo, orderSum);
    }

    @GetMapping("/orders/capture")
    public RedirectView captureOrder(@RequestParam String token){
        //FIXME(Never Do this either put it in proper scope or in DB)
        String orderId = token;
        System.out.println(orderId);
        paymentService.captureOrder(token);
        return new RedirectView("http://localhost:3000/");
    }

    @PostMapping("/orders/create")
    public String createOrder(@RequestBody RequestOrderDto requestOrder, HttpServletRequest request) throws IOException {
        URI returnUrl = buildReturnUrl(request);
        System.out.println(returnUrl);
        System.out.println(requestOrder.getTotalAmount());
        if(Objects.isNull(requestOrder.getTotalAmount())) throw new IllegalArgumentException("Amount is null");

        CreatedOrderDto createdOrder = paymentService.createOrder(requestOrder.getTotalAmount(), returnUrl);
        System.out.println(createdOrder);
        return "" + createdOrder.getApprovalLink();
    }



    @GetMapping("/email")
    public void testEmail() {
        emailService.sendEmail("siverskijvladislav@gmail.com", "Звіт з практики", "Надсилаю тобі звіт");
    }

    private URI buildReturnUrl(HttpServletRequest request) {
        try {
            URI requestUri = URI.create(request.getRequestURL().toString());
            return new URI(requestUri.getScheme(),
                    requestUri.getUserInfo(),
                    requestUri.getHost(),
                    requestUri.getPort(),
                    "/api/v1/public/orders/capture",
                    null, null);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

}
