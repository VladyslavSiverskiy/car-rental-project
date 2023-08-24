package com.vsiver.spring.car_rent_project.entities.ScheduleEntities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.concurrent.ScheduledFuture;


public abstract class ScheduleTask {
    private Instant instant;


    public ScheduleTask() {
    }

    public ScheduleTask(Instant instant) {
        this.instant = instant;
    }

    public Instant getInstant() {
        return instant;
    }

    public void setInstant(Instant instant) {
        this.instant = instant;
    }


    public abstract ScheduledFuture<?> executeTaskScheduling();
}
