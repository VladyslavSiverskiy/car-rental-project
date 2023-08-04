package com.vsiver.spring.car_rent_project;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMethod;

import java.time.LocalDateTime;

@SpringBootApplication
@EnableScheduling
public class CarRentProjectApplication {

    public static void main(String[] args) {
        System.out.println(LocalDateTime.now());
        SpringApplication.run(CarRentProjectApplication.class, args);
    }

}
