package com.vsiver.spring.car_rent_project.controllers;

import com.vsiver.spring.car_rent_project.dtos.CarDto;
import com.vsiver.spring.car_rent_project.entities.Order;
import com.vsiver.spring.car_rent_project.exceptions.NoCarWithSuchIdException;
import com.vsiver.spring.car_rent_project.services.*;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;
import software.amazon.awssdk.services.s3.model.NoSuchKeyException;

import java.io.IOException;
import java.util.Base64;
import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/public")
public class PublicController {

    private S3Service s3Service;
    private CarService carService;
    private EmailService emailService;
    private PaymentService paymentService;
    private OrderService orderService;


    @GetMapping("/profile/{userId}/avatar")
    public String downloadAvatar(@PathVariable Integer userId) throws IOException {
        byte[] resp;
        try {
            resp = s3Service.downloadObject(
                    "car-app-bucket",
                    "users/user" + userId + "/avatar.jpg"
            );
        }catch (NoSuchKeyException e){
            return null;
        }
        return Base64.getEncoder().encodeToString(resp);
    }

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

    /** Submit PayPal order
     * @param token
     * @return
     */
    @GetMapping("/orders/capture")
    public RedirectView captureOrder(@RequestParam String token) {
        orderService.getOrderByPaymentServiceId(token);
        paymentService.captureOrder(token);
        Order order = orderService.getOrderByPaymentServiceId(token);
        orderService.approvePayment(order);
        return new RedirectView("http://localhost:3000/");
    }

    @GetMapping("/email")
    public void testEmail() {
        emailService.sendEmail("siverskijvladislav@gmail.com", "Звіт з практики", "Надсилаю тобі звіт");
    }



}
