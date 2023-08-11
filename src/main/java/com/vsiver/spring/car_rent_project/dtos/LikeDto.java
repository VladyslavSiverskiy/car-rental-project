package com.vsiver.spring.car_rent_project.dtos;

import lombok.Builder;
import lombok.Data;


@Data
@Builder
public class LikeDto {
    private Integer likeId;
    private Integer carId;
    private Integer userId;
}
