package com.vsiver.spring.car_rent_project.repositories;

import com.vsiver.spring.car_rent_project.entities.Order;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findAllByUserId(Integer userId);
    Order findByPayPalOrderId(String payPalOrderId);
}
