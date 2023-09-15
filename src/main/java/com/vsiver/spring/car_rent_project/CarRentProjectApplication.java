package com.vsiver.spring.car_rent_project;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class CarRentProjectApplication {
    public static void main(String[] args) {
        SpringApplication.run(CarRentProjectApplication.class, args);
    }

}
