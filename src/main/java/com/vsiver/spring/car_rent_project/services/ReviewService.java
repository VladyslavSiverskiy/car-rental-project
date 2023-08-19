package com.vsiver.spring.car_rent_project.services;

import com.vsiver.spring.car_rent_project.entities.Review;
import org.springframework.stereotype.Service;

@Service
public interface ReviewService {

    Review create(Review review);
    Review update(Review review);
    Review findById(Integer id);
    Review deleteById(Integer id);
}
