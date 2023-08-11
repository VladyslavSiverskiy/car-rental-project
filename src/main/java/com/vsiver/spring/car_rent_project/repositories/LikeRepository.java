package com.vsiver.spring.car_rent_project.repositories;

import com.vsiver.spring.car_rent_project.entities.Like;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface LikeRepository extends JpaRepository<Like, Long> {
    List<Like> findAllByUserId(Integer userId);

    @Query("SELECT l FROM Like l WHERE l.user.id = :userId AND l.car.carId = :carId")
    Like findByUserIdAndCarId(Integer userId, Integer carId);
}
