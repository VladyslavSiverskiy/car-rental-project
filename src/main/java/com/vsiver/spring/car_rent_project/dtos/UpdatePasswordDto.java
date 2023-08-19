package com.vsiver.spring.car_rent_project.dtos;

import lombok.Data;

@Data
public class UpdatePasswordDto {

    private Integer userId;
    private String oldPassword;
    private String newPassword;
}
