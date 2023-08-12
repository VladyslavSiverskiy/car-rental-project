package com.vsiver.spring.car_rent_project.services;

import com.vsiver.spring.car_rent_project.entities.Car;
import com.vsiver.spring.car_rent_project.entities.EOrderState;
import com.vsiver.spring.car_rent_project.entities.Order;
import com.vsiver.spring.car_rent_project.exceptions.NoCarWithSuchIdException;
import com.vsiver.spring.car_rent_project.exceptions.NoOrderWithSuchIdException;
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
import java.util.Optional;
import java.util.concurrent.ScheduledFuture;

@Component
public class CarReservationService {

    private TaskScheduler scheduler;
    private ScheduledFuture<?> scheduledTask;
    private CarRepository carRepository;
    private OrderRepository orderRepository;

    @Autowired
    public CarReservationService(TaskScheduler scheduler, CarRepository carRepository, OrderRepository orderRepository) {
        this.scheduler = scheduler;
        this.carRepository = carRepository;
        this.orderRepository = orderRepository;
    }

    @Transactional
    void updateCarState(Car car, Order order) throws NoCarWithSuchIdException {
        car.setInStock(false);
        carRepository.save(car);
        orderRepository.save(order);
        System.out.println("Saved");
    }


    /**
     * Method, which schedules changing of the order state (from IS_RESERVED to IN_PROCESS)
     *
     * @param reserveFromTime - date, when schedule make set order state IN_PROCESS
     * @param car - car, where value of availableTo and available will be changed
     * @param order - order to manipulate
     */
    public void changeCarStateBeforeInProcess(LocalDateTime reserveFromTime, Car car, Order order) {
        //за 5 годин до початку оренди метод робить машину не доступною
        Instant instant = reserveFromTime.minusHours(5).atZone(ZoneId.systemDefault()).toInstant();
        System.out.println("Reserved car with id " + car.getCarId() + ", order id " + order.getId());
        System.out.println(instant);
        if (scheduledTask != null) {
            scheduledTask.cancel(false);
            System.out.println("scheduled task " + scheduledTask);
        }
        scheduledTask = scheduler.schedule(() -> {
            // Reserve car here
            System.out.println("Schedule task, car id is " + car.getCarId() + " . Time is " + new Date());
            try {
                updateCarState(car, order);
            } catch (NoCarWithSuchIdException e) {
                throw new RuntimeException(e.getMessage());
            }
        }, instant);
    }

    public void setExpiredOrderStatusIfTimeLast(LocalDateTime reserveToTime, Long orderId) {
        Instant instant = reserveToTime.atZone(ZoneId.systemDefault()).toInstant();
        if (scheduledTask != null) {
            scheduledTask.cancel(false);
            System.out.println("scheduled task " + scheduledTask);
        }
        scheduledTask = scheduler.schedule(() -> {
            Order order = orderRepository.findById(orderId).get();
            if (!order.getOrderState().equals(EOrderState.FINISHED)) {
                order.setOrderState(EOrderState.EXPIRED);
                //TODO:sent notification on email;
            }
        }, instant);
    }

    public void setTimeOfPaymentChecking(LocalDateTime rentFrom, Car car, Order order) {
        System.out.println(rentFrom);
        Instant instant = rentFrom.atZone(ZoneId.systemDefault()).toInstant();
        System.out.println(instant);
        if (scheduledTask != null) {
            scheduledTask.cancel(false);
            System.out.println("Scheduled payment checking time: " + scheduledTask);
        }
        scheduledTask = scheduler.schedule(() -> {
            System.out.println(order);
            System.out.println(car);
            checkIfOrderIsPayed(order, car);
        }, instant);
    }

    private void checkIfOrderIsPayed(Order order, Car car){
        if(order.isPayed()){
            System.out.println("Setting in process");
            setOrderInProcess(order);
        }else{
            System.out.println("Setting expired");
            closeOrder(order,car);
        }
    }


    /** When user paid before rentTo time
     *
     * @param order
     */
    private void setOrderInProcess(Order order){
        order.setOrderState(EOrderState.IN_PROCESS);
        //TODO: sent that order is active
        orderRepository.save(order);
    }

    /**
     * Order is closed only if manager finished it, or payment was expired
     */
    private void closeOrder(Order order, Car car){
        order.setOrderState(EOrderState.EXPIRED);
        order.setPaymentReference("");
        car.setInStock(true);
        car.setAvailableTo(null);
        orderRepository.save(order);
        carRepository.save(car);
        //TODO:sent notification on email
    }
}
