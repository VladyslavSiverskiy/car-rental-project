package com.vsiver.spring.car_rent_project.services;

import com.vsiver.spring.car_rent_project.entities.Car;
import com.vsiver.spring.car_rent_project.entities.EOrderState;
import com.vsiver.spring.car_rent_project.entities.Order;
import com.vsiver.spring.car_rent_project.exceptions.NoCarWithSuchIdException;
import com.vsiver.spring.car_rent_project.repositories.CarRepository;
import com.vsiver.spring.car_rent_project.repositories.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.concurrent.ScheduledFuture;

@Component
public class CarReservationService {

    private TaskScheduler scheduler;
    private ScheduledFuture<?> scheduledTask;

    @Autowired
    private CarRepository carRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    public CarReservationService(TaskScheduler scheduler) {
        this.scheduler = scheduler;
    }

    @Transactional
    void updateCarState(Integer carId, Long orderId) throws NoCarWithSuchIdException {
        Car car = carRepository.findById(carId).orElseThrow(()->new NoCarWithSuchIdException("Can`t find such car"));
        car.setInStock(false);
        Order order = orderRepository.findById(orderId).get();
        order.setOrderState(EOrderState.IN_PROCESS);
        carRepository.save(car);
        orderRepository.save(order);
        System.out.println("Saved");
    }

    public void changeCarStateInProcess(LocalDateTime reserveFromTime, Integer carId, Long orderId) {
        Instant instant = reserveFromTime.minusHours(5).atZone(ZoneId.systemDefault()).toInstant();
        System.out.println("Reserved car with id " + carId + ", order id " + orderId);
        System.out.println(instant);
        if (scheduledTask != null) {
            scheduledTask.cancel(false);
            System.out.println("scheduled task " + scheduledTask);
        }
        scheduledTask = scheduler.schedule(() -> {
            // Reserve car here
            System.out.println("Schedule task, car id is " + carId + " . Time is " + new Date());
            try {
                updateCarState(carId, orderId);
            } catch (NoCarWithSuchIdException e) {
                throw new RuntimeException(e.getMessage());
            }
        }, instant);
    }

    public void setExpiredOrderStatusIfTimeLast(LocalDateTime reserveToTime, Long orderId){
        Instant instant = reserveToTime.atZone(ZoneId.systemDefault()).toInstant();
        if (scheduledTask != null) {
            scheduledTask.cancel(false);
            System.out.println("scheduled task " + scheduledTask);
        }
        scheduledTask = scheduler.schedule(() -> {
            Order order = orderRepository.findById(orderId).get();
            if(!order.getOrderState().equals(EOrderState.FINISHED)) {
                order.setOrderState(EOrderState.EXPIRED);
                //TODO:sent notification on email;
            }
        }, instant);
    }
}
