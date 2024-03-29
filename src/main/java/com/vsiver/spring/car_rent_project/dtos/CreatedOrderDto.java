package com.vsiver.spring.car_rent_project.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.net.URI;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CreatedOrderDto {
    private String orderId;
    private URI approvalLink;
}
