package com.vsiver.spring.car_rent_project.entities.ScheduleEntities;

import com.vsiver.spring.car_rent_project.entities.Car;
import com.vsiver.spring.car_rent_project.entities.Order;
import com.vsiver.spring.car_rent_project.services.CarReservationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.TaskScheduler;

import java.time.Instant;
import java.util.concurrent.ScheduledFuture;

public class ScheduleTimeOfPaymentChecking extends ScheduleTask {

    private Integer carId;
    private Long orderId;
    private CarReservationService carReservationService;
    private TaskScheduler scheduler;

    public ScheduleTimeOfPaymentChecking() {
    }

    public ScheduleTimeOfPaymentChecking(Instant instant, Integer carId, Long orderId) {
        super(instant);
        this.carId = carId;
        this.orderId = orderId;
    }

    public ScheduleTimeOfPaymentChecking(
            Instant instant,
            Car car,
            Order order,
            CarReservationService carReservationService,
            TaskScheduler taskScheduler
    ) {
        super(instant);
        this.carId = car.getCarId();
        this.orderId = order.getId();
        this.carReservationService = carReservationService;
        this.scheduler = taskScheduler;
    }

    public Integer getCarId() {
        return carId;
    }

    public void setCarId(Integer carId) {
        this.carId = carId;
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
        return scheduler.schedule(() ->  carReservationService.checkIfOrderIsPayed(orderId, carId), getInstant());
    }
}
