package com.vsiver.spring.car_rent_project.dtos;

import lombok.Builder;
import lombok.Data;
import software.amazon.awssdk.services.s3.endpoints.internal.Value;

@Data
@Builder
public class UserDto {
    private Integer id;
    private String email;
    private String firstName;
    private String lastName;
    private String phoneNumber;
}
