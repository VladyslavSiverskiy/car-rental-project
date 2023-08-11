package com.vsiver.spring.car_rent_project.services;

import com.vsiver.spring.car_rent_project.dtos.OrderDto;
import com.vsiver.spring.car_rent_project.entities.Car;
import com.vsiver.spring.car_rent_project.entities.EOrderState;
import com.vsiver.spring.car_rent_project.entities.Order;
import com.vsiver.spring.car_rent_project.exceptions.*;
import com.vsiver.spring.car_rent_project.repositories.CarRepository;
import com.vsiver.spring.car_rent_project.repositories.OrderRepository;
import com.vsiver.spring.car_rent_project.user.User;
import com.vsiver.spring.car_rent_project.user.UserRepository;
import com.vsiver.spring.car_rent_project.utils.CustomMappers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import software.amazon.awssdk.services.s3.endpoints.internal.Value;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Transactional
@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private CarRepository carRepository;
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CarReservationService carReservationService;

    public boolean orderCar(Integer carId, Integer userId, LocalDateTime rentFrom, LocalDateTime rentTo, BigDecimal orderSum) throws NoCarWithSuchIdException, NoUserWithSuchIdException, CarOutOfStockException, IncorrectRentTimeException {
        //TODO: set to car when it will be able
        Car car = carRepository.findById(carId).orElseThrow(() -> new NoCarWithSuchIdException("No car with such id!"));
        User user = userRepository.findById(userId).orElseThrow(() -> new NoUserWithSuchIdException("No user with such id!"));
        if (!car.getInStock()) {
            throw new CarOutOfStockException("Car with such id is unavailable");
        }
        if (rentFrom.compareTo(rentTo) >= 0) {
            throw new IncorrectRentTimeException("Rent end time can`t be faster than rent beginning time");
        }
        int res = rentFrom.compareTo(LocalDateTime.now().plusHours(5)); // - 1 менше - значить IN_PROCESS
        Order order = new Order();
        if (res != 1) {
            car.setInStock(false);
            order.setOrderState(EOrderState.IN_PROCESS);
            //змінити InStock на true після завершення
        } else {//резервування
            car.setAvailableTo(rentFrom);
            order.setOrderState(EOrderState.IS_RESERVED);

            //змінити дані в момент 5 год
        }
        order.setRentFrom(rentFrom);
        order.setRentTo(rentTo);
        order.setUser(user);
        order.setCar(car);
        order.setOrderSum(orderSum);
        orderRepository.save(order);

        if (res == 1) {
            carReservationService.changeCarStateInProcess(rentFrom, car.getCarId(), order.getId());
        }
        carReservationService.setExpiredOrderStatusIfTimeLast(rentTo, order.getId());
        return false;
    }

    /**
     * Admin send request when customer will return car to parking location and order status in database is changed
     * */
    public boolean submitOrder(Long orderId) throws NoCarWithSuchIdException {
        Order order = orderRepository.findById(orderId).orElseThrow(() -> new NoOrderWithSuchIdException("Order with such id doesn`t exist"));
        order.setOrderState(EOrderState.FINISHED);
        Car car = carRepository.findById(order.getCar().getCarId()).orElseThrow(()->new NoCarWithSuchIdException("No car with such id"));
        car.setInStock(true);
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

        int res = rentFrom.compareTo(LocalDateTime.now().plusHours(5)); // - 1 менше - значить IN_PROCESS
        if (res != 1) {
            car.setInStock(false);
            order.setOrderState(EOrderState.IS_RESERVED);
//            order.setOrderState(EOrderState.IN_PROCESS);//TODO: зробити коли настав час замовлення та оплата здійснена, якщо ні то скинути повідомлення на пошту
            //змінити InStock на true після завершення
        } else {//резервування
            car.setAvailableTo(rentFrom);
            //змінити дані в момент 5 год
            carReservationService.changeCarStateInProcess(rentFrom, car.getCarId(), order.getId());
        }
        //TODO: якщо оплата не поступила до rentTo (подивитись метод, змінити його)
        carReservationService.setExpiredOrderStatusIfTimeLast(rentTo, order.getId());


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
        carRepository.save(car);
        System.out.println(order);
        return order;
    }

    /** change selected order state to IS_RESERVED or IN_ACTION, set payed to yes
     * @param order
     * @return
     */
    public Order changeOrderStateToReserved(Order order){
        return null;
    }

    public List<Order> getOrdersByUserId(Integer userId){
        return orderRepository.findAllByUserId(userId);
    }

    public Order getOrderByPaymentServiceId(String paymentServiceId){
        return orderRepository.findByPayPalOrderId(paymentServiceId);
    }
}
