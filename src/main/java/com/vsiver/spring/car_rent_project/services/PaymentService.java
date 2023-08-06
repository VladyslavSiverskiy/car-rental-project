package com.vsiver.spring.car_rent_project.services;

import com.vsiver.spring.car_rent_project.dtos.CreatedOrderDto;

import java.io.IOException;
import java.net.URI;

public interface PaymentService {
    CreatedOrderDto createOrder(Double totalAmount, URI returnUrl) throws IOException;

    void captureOrder(String token);
}
