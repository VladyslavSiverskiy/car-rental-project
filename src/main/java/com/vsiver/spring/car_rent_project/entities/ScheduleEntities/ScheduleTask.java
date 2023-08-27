package com.vsiver.spring.car_rent_project.entities.ScheduleEntities;

import java.time.Instant;
import java.util.concurrent.ScheduledFuture;


public abstract class ScheduleTask {
    private Instant instant;

    protected ScheduleTask() {
    }

    protected ScheduleTask(Instant instant) {
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
