package com.vsiver.spring.car_rent_project.services;

import com.vsiver.spring.car_rent_project.entities.Car;
import com.vsiver.spring.car_rent_project.entities.EOrderState;
import com.vsiver.spring.car_rent_project.entities.Order;
import com.vsiver.spring.car_rent_project.exceptions.*;
import com.vsiver.spring.car_rent_project.repositories.CarRepository;
import com.vsiver.spring.car_rent_project.repositories.OrderRepository;
import com.vsiver.spring.car_rent_project.user.User;
import com.vsiver.spring.car_rent_project.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Transactional
@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private CarRepository carRepository;
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CarReservationService carReservationService;

    public boolean orderCar(Integer carId, Integer userId, LocalDateTime rentFrom, LocalDateTime rentTo, BigDecimal orderSum) throws NoCarWithSuchIdException, NoUserWithSuchIdException, CarOutOfStockException, IncorrectRentTimeException {
        //TODO: set to car when it will be able
        Car car = carRepository.findById(carId).orElseThrow(() -> new NoCarWithSuchIdException("No car with such id!"));
        User user = userRepository.findById(userId).orElseThrow(() -> new NoUserWithSuchIdException("No user with such id!"));
        if (!car.getInStock()) {
            throw new CarOutOfStockException("Car with such id is unavailable");
        }
        if (rentFrom.compareTo(rentTo) >= 0) {
            throw new IncorrectRentTimeException("Rent end time can`t be faster than rent beginning time");
        }
        int res = rentFrom.compareTo(LocalDateTime.now().plusHours(5)); // - 1 менше - значить IN_PROCESS
        Order order = new Order();
        if (res != 1) {
            car.setInStock(false);
            order.setOrderState(EOrderState.IN_PROCESS);
            //змінити InStock на true після завершення
        } else {//резервування
            car.setAvailableTo(rentFrom);
            order.setOrderState(EOrderState.IS_RESERVED);

            //змінити дані в момент 5 год
        }
        order.setRentFrom(rentFrom);
        order.setRentTo(rentTo);
        order.setUser(user);
        order.setCar(car);
        order.setOrderSum(orderSum);
        orderRepository.save(order);

        if (res == 1) {
            carReservationService.changeCarStateInProcess(rentFrom, car.getCarId(), order.getId());
        }
        carReservationService.setExpiredOrderStatusIfTimeLast(rentTo, order.getId());
        return false;
    }

    /**
     * Admin send request when customer will return car to parking location and order status in database is changed
     * */
    public boolean submitOrder(Long orderId) throws NoCarWithSuchIdException {
        Order order = orderRepository.findById(orderId).orElseThrow(() -> new NoOrderWithSuchIdException("Order with such id doesn`t exist"));
        order.setOrderState(EOrderState.FINISHED);
        Car car = carRepository.findById(order.getCar().getCarId()).orElseThrow(()->new NoCarWithSuchIdException("No car with such id"));
        car.setInStock(true);
        carRepository.save(car);
        orderRepository.save(order);
        return true;
    }
}
