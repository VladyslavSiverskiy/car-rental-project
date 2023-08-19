package com.vsiver.spring.car_rent_project.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ReviewDto {
    private Integer id;
    private Integer carId;
    private Integer userId;
    private Double rate;
    private String description;
    private LocalDateTime creationDate;
}
