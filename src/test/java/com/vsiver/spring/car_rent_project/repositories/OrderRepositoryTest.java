package com.vsiver.spring.car_rent_project.repositories;

import com.vsiver.spring.car_rent_project.entities.Car;
import com.vsiver.spring.car_rent_project.entities.EOrderState;
import com.vsiver.spring.car_rent_project.entities.Order;
import com.vsiver.spring.car_rent_project.user.Role;
import com.vsiver.spring.car_rent_project.user.User;
import com.vsiver.spring.car_rent_project.user.UserRepository;
import org.checkerframework.checker.units.qual.C;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;


@DataJpaTest
@Transactional
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class OrderRepositoryTest {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CarRepository carRepository;

    @Test
    void findAllByUserId() {
        Car car = new Car();
        car.setCarModel("Some car");
        carRepository.save(car);

        User user = new User();
        user.setEmail("john.doe@mail.com");
        user.setPass("password");
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setRole(Role.USER);
        user = userRepository.save(user);

        Order order = new Order();
        order.setOrderState(EOrderState.IN_PROCESS);
        order.setUser(user);
        order.setCar(car);
        order.setOrderSum(BigDecimal.TEN);
        order.setRentFrom(LocalDateTime.now());
        order.setRentTo(LocalDateTime.now().plusHours(6));
        order.setPaymentReference("some-ref");
        order.setPayed(true);

        Order order2 = new Order();
        order2.setOrderState(EOrderState.IN_PROCESS);
        order2.setUser(user);
        order2.setCar(car);
        order2.setOrderSum(BigDecimal.TEN);
        order2.setRentFrom(LocalDateTime.now());
        order2.setRentTo(LocalDateTime.now().plusHours(6));
        order2.setPaymentReference("some-ref");
        order2.setPayed(true);

        orderRepository.save(order);
        orderRepository.save(order2);
        System.out.println(orderRepository.findAll());
        assertEquals(2, orderRepository.findAllByUserId(user.getId()).size());
    }
}