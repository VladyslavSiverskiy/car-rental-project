package com.vsiver.spring.car_rent_project.entities.ScheduleEntities;

import com.vsiver.spring.car_rent_project.entities.Car;
import com.vsiver.spring.car_rent_project.entities.EOrderState;
import com.vsiver.spring.car_rent_project.entities.Order;
import com.vsiver.spring.car_rent_project.services.CarReservationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.TaskScheduler;

import java.time.Instant;
import java.util.concurrent.ScheduledFuture;

public class ScheduleExpiredOrderStatus extends ScheduleTask {

    private Long orderId;
    private Logger logger = LoggerFactory.getLogger(ScheduleBeforeInProcess.class);
    private CarReservationService carReservationService;
    private TaskScheduler scheduler;

    public ScheduleExpiredOrderStatus() {
    }

    public ScheduleExpiredOrderStatus(Instant instant, Long orderId) {
        super(instant);
        this.orderId = orderId;
    }

    public ScheduleExpiredOrderStatus(
            Instant instant,
            Order order,
            CarReservationService carReservationService,
            TaskScheduler taskScheduler
    ) {
        super(instant);
        this.orderId = order.getId();
        this.carReservationService = carReservationService;
        this.scheduler = taskScheduler;
    }


    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public void setCarReservationService(CarReservationService carReservationService) {
        this.carReservationService = carReservationService;
    }

    public void setScheduler(TaskScheduler scheduler) {
        this.scheduler = scheduler;
    }

    @Override
    public ScheduledFuture<?> executeTaskScheduling() {
        return scheduler.schedule(()->{
            logger.info("checking order with ID " + orderId);
            carReservationService.checkExpired(orderId);
        }, getInstant());
    }
}
