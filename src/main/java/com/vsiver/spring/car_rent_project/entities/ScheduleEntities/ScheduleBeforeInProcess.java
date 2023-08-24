package com.vsiver.spring.car_rent_project.entities.ScheduleEntities;

import com.vsiver.spring.car_rent_project.entities.Car;
import com.vsiver.spring.car_rent_project.entities.Order;
import com.vsiver.spring.car_rent_project.exceptions.NoCarWithSuchIdException;
import com.vsiver.spring.car_rent_project.services.CarReservationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.SchedulingException;
import org.springframework.scheduling.TaskScheduler;

import java.time.Instant;
import java.util.concurrent.ScheduledFuture;

public class ScheduleBeforeInProcess extends ScheduleTask{

    private Integer carId;
    private Long orderId;
    private final Logger logger = LoggerFactory.getLogger(ScheduleBeforeInProcess.class);
    private CarReservationService carReservationService;
    private TaskScheduler scheduler;

    public ScheduleBeforeInProcess() {
    }

    public ScheduleBeforeInProcess(Instant instant, Integer carId, Long orderId) {
        super(instant);
        this.carId = carId;
        this.orderId = orderId;
    }

    public ScheduleBeforeInProcess(
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

    public void setScheduler(TaskScheduler scheduler) {
        this.scheduler = scheduler;
    }

    public void setCarId(Integer carId) {
        this.carId = carId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public Integer getCarId() {
        return carId;
    }

    public Long getOrderId() {
        return orderId;
    }

    public void setCarReservationService(CarReservationService carReservationService) {
        this.carReservationService = carReservationService;
    }

    @Override
    public ScheduledFuture<?> executeTaskScheduling() {
        return scheduler.schedule(() -> {
            logger.info("Schedule task, car id is " + carId + " . Car will be updated at " + getInstant());
            try {
                carReservationService.updateCarState(carId, orderId);
            } catch (NoCarWithSuchIdException e) {
                logger.error("Scheduling exception: " + e.getMessage());
                throw new SchedulingException(e.getMessage());
            }
        }, getInstant());
    }
}
