package com.vsiver.spring.car_rent_project.dtos;

import com.vsiver.spring.car_rent_project.entities.EOrderState;
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
    private Boolean isPayed;
}
