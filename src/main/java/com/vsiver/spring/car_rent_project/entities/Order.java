package com.vsiver.spring.car_rent_project.entities;


import com.vsiver.spring.car_rent_project.user.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
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
    private EOrderState orderState; //here save program order state

    @NotNull
    @Column(name = "rent_from")
    private LocalDateTime rentFrom;

    @NotNull
    @Column(name = "rent_to")
    private LocalDateTime rentTo;
    @NotNull
    @Column(name = "order_sum")
    private BigDecimal orderSum;

    @NotNull
    @Column(name = "payment_reference")
    private String paymentReference;

    @Column(name = "payment_order_id")
    private String payPalOrderId;

    @Column(name = "created_at")
    private LocalDateTime creationTime;

    @NotNull
    @Column(name = "is_payed")
    private Boolean isPayed;

    public Order() {

    }

    public String getPaymentReference() {
        return paymentReference;
    }

    public void setPaymentReference(String paymentReference) {
        this.paymentReference = paymentReference;
    }

    public String getPayPalOrderId() {
        return payPalOrderId;
    }

    public void setPayPalOrderId(String payPalOrderId) {
        this.payPalOrderId = payPalOrderId;
    }

    public LocalDateTime getCreationTime() {
        return creationTime;
    }

    public void setCreationTime(LocalDateTime creationTime) {
        this.creationTime = creationTime;
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

    public BigDecimal getOrderSum() {
        return orderSum;
    }

    public void setOrderSum(BigDecimal orderSum) {
        this.orderSum = orderSum;
    }

    public boolean isPayed() {
        return isPayed;
    }

    public void setPayed(boolean payed) {
        isPayed = payed;
    }

    @Override
    public String toString() {
        return "Order{" +
               "id=" + id +
               ", user=" + user.getId() +
               ", car=" + car.getCarId() +
               ", orderState=" + orderState +
               ", rentFrom=" + rentFrom +
               ", rentTo=" + rentTo +
               ", orderSum=" + orderSum +
               ", paymentReference='" + paymentReference + '\'' +
               ", payPalOrderId='" + payPalOrderId + '\'' +
               ", creationTime=" + creationTime +
               ", isPayed=" + isPayed +
               '}';
    }
}
