package com.vsiver.spring.car_rent_project.services;

import com.vsiver.spring.car_rent_project.entities.Car;
import com.vsiver.spring.car_rent_project.entities.EOrderState;
import com.vsiver.spring.car_rent_project.entities.Order;
import com.vsiver.spring.car_rent_project.exceptions.NoCarWithSuchIdException;
import com.vsiver.spring.car_rent_project.repositories.CarRepository;
import com.vsiver.spring.car_rent_project.repositories.OrderRepository;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.SchedulingException;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ScheduledFuture;

@Service
@AllArgsConstructor
@Transactional
public class CarReservationService {

    private Logger logger = LoggerFactory.getLogger(CarReservationService.class);
    private TaskScheduler scheduler;
    private List<ScheduledFuture<?>> scheduledTasks = new ArrayList<>();
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

        ScheduledFuture<?> scheduledTask = scheduler.schedule(() -> {
            // Reserve car here
            logger.info("Schedule task, car id is " + car.getCarId() + " . Car will be updated at " + instant);
            try {
                updateCarState(car, order);
            } catch (NoCarWithSuchIdException e) {
                logger.error("Scheduling exception: " + e.getMessage());
                throw new SchedulingException(e.getMessage());
            }
        }, instant);

        scheduledTasks.add(scheduledTask);
    }

    public void setExpiredOrderStatusIfTimeLast(LocalDateTime reserveToTime, Order order) {
        if(Objects.isNull(order)) throw new IllegalArgumentException("Object can`t be null");
        Instant instant = reserveToTime.atZone(ZoneId.systemDefault()).toInstant();
        ScheduledFuture<?> scheduledTask = scheduler.schedule(() -> {
            System.out.println("Check order");
            if (!order.getOrderState().equals(EOrderState.FINISHED)) {
                logger.warn("Order with id " + order.getId() + " is overdue!");
                order.setPayed(true);
                order.setOrderState(EOrderState.OVERDUE);
                orderRepository.save(order);
                //TODO:sent notification on email;
            }
        }, instant);
        scheduledTasks.add(scheduledTask);
    }

    public void setTimeOfPaymentChecking(LocalDateTime rentFrom, Car car, Order order) {
        Instant instant = rentFrom.atZone(ZoneId.systemDefault()).toInstant();
        logger.info("Scheduled payment checking time: " + instant
                    + ", for car with id " + car.getCarId());
        ScheduledFuture<?> scheduledTask = scheduler.schedule(() -> {
            checkIfOrderIsPayed(order, car);
        }, instant);
        scheduledTasks.add(scheduledTask);
    }

    private void checkIfOrderIsPayed(Order order, Car car){
        order = orderRepository.findById(order.getId()).get();
        if(order.isPayed()){
            logger.info("Car with id " + car.getCarId() + " set IN_PROCESS status");
            setOrderInProcess(order);
        }else{
            logger.info("Car with id " + car.getCarId() + " set EXPIRED status");
            closeOrder(order,car);
        }
    }


    /** When user paid before rentTo time
     *
     * @param order
     */
    private void setOrderInProcess(Order order){
        order.setOrderState(EOrderState.IN_PROCESS);
        order.setPayed(true);
        //TODO: sent that order is active
        orderRepository.save(order);
        orderRepository.flush();
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
        orderRepository.flush();
        //TODO:sent notification on email
    }
}
