package com.vsiver.spring.car_rent_project.entities;


import com.vsiver.spring.car_rent_project.user.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

@Entity
@Table(name = "orders")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_id")
    private Long id;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "car_id")
    private Car car;

    @Enumerated(EnumType.STRING)
    @Column(name = "order_state")
    private EOrderState orderState;

    @NotNull
    @Column(name = "rent_from")
    private LocalDateTime rentFrom;

    @NotNull
    @Column(name = "rent_to")
    private LocalDateTime rentTo;

    public Order() {

    }

    public Order(Long id, User user, Car car, EOrderState orderState, LocalDateTime rentFrom, LocalDateTime rentTo) {
        this.id = id;
        this.user = user;
        this.car = car;
        this.orderState = orderState;
        this.rentFrom = rentFrom;
        this.rentTo = rentTo;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Car getCar() {
        return car;
    }

    public void setCar(Car car) {
        this.car = car;
    }

    public EOrderState getOrderState() {
        return orderState;
    }

    public void setOrderState(EOrderState orderState) {
        this.orderState = orderState;
    }

    public LocalDateTime getRentFrom() {
        return rentFrom;
    }

    public void setRentFrom(LocalDateTime rentFrom) {
        this.rentFrom = rentFrom;
    }

    public LocalDateTime getRentTo() {
        return rentTo;
    }

    public void setRentTo(LocalDateTime rentTo) {
        this.rentTo = rentTo;
    }
}
