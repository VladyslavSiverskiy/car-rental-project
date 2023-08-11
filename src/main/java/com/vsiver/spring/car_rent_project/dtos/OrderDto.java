package com.vsiver.spring.car_rent_project.dtos;

import com.vsiver.spring.car_rent_project.entities.Car;
import com.vsiver.spring.car_rent_project.entities.EOrderState;
import com.vsiver.spring.car_rent_project.user.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;



@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OrderDto {

    private Long orderId;
    private Long userId;
    private String carModel;
    private EOrderState orderState; //here save program order state
    private LocalDateTime rentFrom;
    private LocalDateTime rentTo;
    private BigDecimal orderSum;
    private String paymentReference;
    private String payPalOrderId;
    private LocalDateTime creationTime;
    private Boolean isPayed; //here save PayPal status
}
