package com.vsiver.spring.car_rent_project.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.vsiver.spring.car_rent_project.entities.Car;
import com.vsiver.spring.car_rent_project.entities.EOrderState;
import com.vsiver.spring.car_rent_project.entities.Order;
import com.vsiver.spring.car_rent_project.entities.ScheduleEntities.ScheduleBeforeInProcess;
import com.vsiver.spring.car_rent_project.entities.ScheduleEntities.ScheduleExpiredOrderStatus;
import com.vsiver.spring.car_rent_project.entities.ScheduleEntities.ScheduleTimeOfPaymentChecking;
import com.vsiver.spring.car_rent_project.exceptions.NoCarWithSuchIdException;
import com.vsiver.spring.car_rent_project.exceptions.NoOrderWithSuchIdException;
import com.vsiver.spring.car_rent_project.repositories.CarRepository;
import com.vsiver.spring.car_rent_project.repositories.OrderRepository;
import jakarta.annotation.PostConstruct;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import redis.clients.jedis.Jedis;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.concurrent.ScheduledFuture;

@Service
@AllArgsConstructor
@Transactional
public class CarReservationService {

    private long tasksCounter;

    private Logger logger = LoggerFactory.getLogger(CarReservationService.class);

    private TaskScheduler scheduler;

    private List<ScheduledFuture<?>> scheduledTasks = new ArrayList<>();

    private CarRepository carRepository;

    private OrderRepository orderRepository;

    private ObjectMapper objectMapper;

    private EmailService emailService;

    private Jedis jedis;

    @Autowired
    public CarReservationService(
            TaskScheduler scheduler,
            CarRepository carRepository,
            OrderRepository orderRepository,
            EmailService emailService
    ) {
        jedis = new Jedis("localhost", 6379);
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        this.scheduler = scheduler;
        this.carRepository = carRepository;
        this.orderRepository = orderRepository;
        this.emailService = emailService;
    }

    @PostConstruct
    public void loadTasksFromRedisOnStartup() {

        Set<String> beforeInProcessTaskKeys = jedis.keys("task:before-in-process:*");
        Set<String> setExpiredTaskKeys = jedis.keys("task:set-expired-status:*");
        Set<String> setPaymentTaskKeys = jedis.keys("task:set-payment-time:*");

        for (String taskKey : beforeInProcessTaskKeys) {
            String taskJson = jedis.get(taskKey);
            if (taskJson != null && !taskJson.isEmpty()) {
                try {
                    ScheduleBeforeInProcess scheduleTask = objectMapper.readValue(taskJson, ScheduleBeforeInProcess.class);
                    scheduleTask.setCarReservationService(this);
                    scheduleTask.setScheduler(scheduler);
                    scheduleTask.executeTaskScheduling();
                    logger.info("Download task before from redis");
                } catch (JsonProcessingException e) {
                    logger.error("Error while deserializing task from JSON: " + e.getMessage());
                }
            }
        }

        for (String taskKey : setExpiredTaskKeys) {
            String taskJson = jedis.get(taskKey);
            if (taskJson != null && !taskJson.isEmpty()) {
                try {
                    ScheduleExpiredOrderStatus scheduleTask = objectMapper.readValue(taskJson, ScheduleExpiredOrderStatus.class);
                    scheduleTask.setCarReservationService(this);
                    scheduleTask.setScheduler(scheduler);
                    scheduleTask.executeTaskScheduling();
                } catch (JsonProcessingException e) {
                    logger.warn("Error while deserializing task from JSON: " + e.getMessage());
                }
            }
        }

        for (String taskKey : setPaymentTaskKeys) {
            String taskJson = jedis.get(taskKey);
            if (taskJson != null && !taskJson.isEmpty()) {
                try {
                    ScheduleTimeOfPaymentChecking scheduleTask = objectMapper.readValue(taskJson, ScheduleTimeOfPaymentChecking.class);
                    scheduleTask.setCarReservationService(this);
                    scheduleTask.setScheduler(scheduler);
                    scheduleTask.executeTaskScheduling();
                } catch (JsonProcessingException e) {
                    logger.warn("Error while deserializing task from JSON: " + e.getMessage());
                }
            }
        }

    }

    /**
     * changing the order state (from IS_RESERVED to IN_PROCESS) in 5 hours before reservation
     * @param reserveFromTime - date, when schedule make set order state IN_PROCESS
     * @param car             - car, where value of availableTo and available will be changed
     * @param order           - order to manipulate
     */
    public void changeCarStateBeforeInProcess(LocalDateTime reserveFromTime, Car car, Order order) {
        Instant instant = reserveFromTime.minusHours(5).atZone(ZoneId.systemDefault()).toInstant();
        logger.info("Reserved car with id " + car.getCarId() + ", order id " + order.getId());
        ScheduleBeforeInProcess scheduleTask = new ScheduleBeforeInProcess(
                instant,
                car,
                order,
                this,
                scheduler
        );
        ScheduledFuture<?> scheduledTask = scheduleTask.executeTaskScheduling();
        tasksCounter++;
        scheduledTasks.add(scheduledTask);
        try {
            jedis.set("task:before-in-process:" + tasksCounter, objectMapper.writeValueAsString(scheduleTask));
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Server error while processing entire JSON...");
        }
    }


    public void setExpiredOrderStatusIfTimeLast(LocalDateTime reserveToTime, Order order) {
        if (Objects.isNull(order)) throw new IllegalArgumentException("Object can`t be null");
        Instant instant = reserveToTime.atZone(ZoneId.systemDefault()).toInstant();
        ScheduleExpiredOrderStatus scheduleExpiredOrderStatus = new ScheduleExpiredOrderStatus(
                instant,
                order,
                this,
                scheduler
        );
        ScheduledFuture<?> scheduledTask = scheduleExpiredOrderStatus.executeTaskScheduling();
        scheduledTasks.add(scheduledTask);
        tasksCounter++;
        try {
            jedis.set("task:set-expired-status:" + tasksCounter,
                    objectMapper.writeValueAsString(scheduleExpiredOrderStatus));
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Server error while processing entire JSON...");
        }
    }

    public void setTimeOfPaymentChecking(LocalDateTime rentFrom, Car car, Order order) {
        Instant instant = rentFrom.atZone(ZoneId.systemDefault()).toInstant();
        logger.info("Scheduled payment checking time: " + instant
                    + ", for car with id " + car.getCarId());
        ScheduleTimeOfPaymentChecking scheduleTimeOfPaymentChecking = new ScheduleTimeOfPaymentChecking(
                instant,
                car,
                order,
                this,
                scheduler
        );
        ScheduledFuture<?> scheduledTask = scheduleTimeOfPaymentChecking.executeTaskScheduling();
        scheduledTasks.add(scheduledTask);
        tasksCounter++;
        try {
            jedis.set("task:set-payment-time:" + tasksCounter,
                    objectMapper.writeValueAsString(scheduleTimeOfPaymentChecking));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            throw new RuntimeException("Server error while processing entire JSON...");
        }
    }

    public void checkExpired(Long orderId) {
        Order order;
        Optional<Order> orderOptional = orderRepository.findById(orderId);
        if (orderOptional.isPresent()) {
            order = orderOptional.get();
        } else {
            throw new NoOrderWithSuchIdException("Order with ID " + orderId + " not found.");
        }
        if (!order.getOrderState().equals(EOrderState.FINISHED)) {
            logger.warn("Order with id " + order.getId() + " is overdue!");
            order.setPayed(true);
            order.setOrderState(EOrderState.OVERDUE);
            orderRepository.save(order);
            emailService.sendEmail(order.getUser().getEmail()
                    , "Order is overdue",
                    "Dear customer! Your order with ID " + orderId + " is overdue! Our manager will call you soon.");
        }
    }

    @Transactional
    public void updateCarState(Integer carId, Long orderId) throws NoCarWithSuchIdException {
        Car car;
        Order order;
        Optional<Car> carOptional = carRepository.findById(carId);
        if (carOptional.isPresent()) {
            car = carOptional.get();
        } else {
            throw new NoCarWithSuchIdException("Car with ID " + carId + " not found.");
        }
        Optional<Order> orderOptional = orderRepository.findById(orderId);
        if (orderOptional.isPresent()) {
            order = orderOptional.get();
        } else {
            throw new NoOrderWithSuchIdException("Order with ID " + orderId + " not found.");
        }
        car.setInStock(false);
        carRepository.save(car);
        orderRepository.save(order);
    }


    public void checkIfOrderIsPayed(Long orderId, Integer carId) {
        Order order;
        Optional<Order> orderOptional = orderRepository.findById(orderId);
        if (orderOptional.isPresent()) {
            order = orderOptional.get();
        } else {
            throw new NoOrderWithSuchIdException("Order with ID " + orderId + " not found.");
        }

        if (order.isPayed()) {
            logger.info("Car with id " + carId + " set IN_PROCESS status");
            setOrderInProcess(order);
        } else {
            logger.info("Car with id " + carId + " set EXPIRED status");
            closeOrder(order, order.getCar());
        }
    }


    /**
     * When user paid before rentTo time
     *
     * @param order
     */
    private void setOrderInProcess(Order order) {
        order.setOrderState(EOrderState.IN_PROCESS);
        order.setPayed(true);
        orderRepository.save(order);
        orderRepository.flush();
        emailService.sendEmail(order.getUser().getEmail()
                , "Order is expired",
                "Dear customer! Your order with ID " + order.getId() + " is expired! " +
                "Enjoy your reservation!.");
    }

    /**
     * Order is closed only if manager finished it, or payment was expired
     */
    private void closeOrder(Order order, Car car) {
        order.setOrderState(EOrderState.EXPIRED);
        order.setPaymentReference("");
        car.setInStock(true);
        car.setAvailableTo(null);
        orderRepository.save(order);
        carRepository.save(car);
        orderRepository.flush();
        emailService.sendEmail(order.getUser().getEmail()
                , "Order is expired",
                "Dear customer! Your order with ID " + order.getId() + " is expired! " +
                "Make re-order of car and pay before the beginning of reservation.");
    }
}
