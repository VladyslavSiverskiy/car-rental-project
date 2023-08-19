package com.vsiver.spring.car_rent_project.repositories;

import com.vsiver.spring.car_rent_project.entities.Review;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReviewRepository extends JpaRepository<Review, Integer> {
}
