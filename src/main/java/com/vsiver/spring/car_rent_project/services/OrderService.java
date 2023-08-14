package com.vsiver.spring.car_rent_project.services;

import com.vsiver.spring.car_rent_project.entities.Car;
import com.vsiver.spring.car_rent_project.entities.EOrderState;
import com.vsiver.spring.car_rent_project.entities.Order;
import com.vsiver.spring.car_rent_project.exceptions.*;
import com.vsiver.spring.car_rent_project.repositories.CarRepository;
import com.vsiver.spring.car_rent_project.repositories.OrderRepository;
import com.vsiver.spring.car_rent_project.user.User;
import com.vsiver.spring.car_rent_project.user.UserRepository;
import lombok.AllArgsConstructor;
import org.aspectj.weaver.ast.Or;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class OrderService {

    private OrderRepository orderRepository;
    private CarRepository carRepository;
    private UserRepository userRepository;
    private CarReservationService carReservationService;

    private Logger logger = LoggerFactory.getLogger(OrderService.class);

    @Autowired
    public OrderService(OrderRepository orderRepository, CarRepository carRepository, UserRepository userRepository, CarReservationService carReservationService) {
        this.orderRepository = orderRepository;
        this.carRepository = carRepository;
        this.userRepository = userRepository;
        this.carReservationService = carReservationService;
    }

    /**
     * Admin send request when customer will return car to parking location and order status in database is changed
     * */
    public boolean submitOrder(Long orderId) throws NoCarWithSuchIdException {
        Order order = orderRepository.findById(orderId).orElseThrow(() -> new NoOrderWithSuchIdException("Order with such id doesn`t exist"));
        order.setOrderState(EOrderState.FINISHED);
        Car car = carRepository.findById(order.getCar().getCarId()).orElseThrow(()->new NoCarWithSuchIdException("No car with such id"));
        car.setInStock(true);
        //TODO: глянути в order, чи є машина ще зарезрвована
//        car.setAvailableTo();
        carRepository.save(car);
        orderRepository.save(order);
        return true;
    }

    /**
     * Method which create order for car rent (there is also Order class from PayPal in application
     * it is different, be careful, some methods return data that fill this order)
     *
     * @param rentFrom
     * @param rentTo
     * @param amount - sum of order
     * @param paymentOrderId - order receipt from payment system
     *
     * @return created Order
     */
    public Order createProgramOrder(
            LocalDateTime rentFrom,
            LocalDateTime rentTo,
            BigDecimal amount,
            String paymentOrderId,
            String paymentLink,
            Integer carId,
            Integer userId
    ) throws NoCarWithSuchIdException, NoUserWithSuchIdException, CarOutOfStockException, IncorrectRentTimeException {
        Order order = new Order();

        Car car = carRepository.findById(carId).orElseThrow(() -> new NoCarWithSuchIdException("No car with such id!"));
        User user = userRepository.findById(userId).orElseThrow(() -> new NoUserWithSuchIdException("No user with such id!"));

        if (!car.getInStock()) {
            throw new CarOutOfStockException("Car with such id is unavailable");
        }
        if (rentFrom.compareTo(rentTo) >= 0) {
            throw new IncorrectRentTimeException("Rent end time can`t be faster than rent beginning time");
        }

        order.setCar(car);
        order.setUser(user);
        order.setRentFrom(rentFrom);
        order.setRentTo(rentTo);
        order.setOrderSum(amount);
        order.setOrderState(EOrderState.IS_RESERVED);
        order.setCreationTime(LocalDateTime.now());
        order.setPayPalOrderId(paymentOrderId);
        order.setPaymentReference(paymentLink);
        order.setPayed(false);

        order = orderRepository.save(order);

        int res = rentFrom.compareTo(LocalDateTime.now().plusHours(5)); // - 1 менше - значить IN_PROCESS
        if (res != 1) {
            car.setInStock(false);
//            order.setOrderState(EOrderState.IN_PROCESS);//TODO: зробити коли настав час замовлення та оплата здійснена, якщо ні то скинути повідомлення на пошту
            //змінити InStock на true після завершення
        } else {//резервування
            car.setAvailableTo(rentFrom);
            //заблокувати машину дані в момент 5 год
            //зробить update
            carReservationService.changeCarStateBeforeInProcess(rentFrom, car, order);
            //на rent to запланувати перевірку isPayed (якщо оплачено - зробити in process, якщо ні - закрити замовлення і кинути на пошту лист)
            //TODO: якщо оплата не поступила до rentTo (подивитись метод, змінити його)
        }
        carReservationService.setTimeOfPaymentChecking(rentFrom, car, order);
        carReservationService.setExpiredOrderStatusIfTimeLast(rentTo, order);

        carRepository.save(car);
        return order;
    }

    public List<Order> getOrdersByUserId(Integer userId){
        return orderRepository.findAllByUserId(userId);
    }

    public Order getOrderByPaymentServiceId(String paymentServiceId){
        return orderRepository.findByPayPalOrderId(paymentServiceId);
    }

    /**
     * Set isPayed status on true
     */
    public void approvePayment(Order order) {
        order.setPayed(true);
        orderRepository.save(order);
        orderRepository.flush();
    }

    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }
}

