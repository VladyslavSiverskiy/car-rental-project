package com.vsiver.spring.car_rent_project.services;

import com.vsiver.spring.car_rent_project.entities.Car;
import com.vsiver.spring.car_rent_project.entities.EOrderState;
import com.vsiver.spring.car_rent_project.entities.Order;
import com.vsiver.spring.car_rent_project.exceptions.*;
import com.vsiver.spring.car_rent_project.repositories.CarRepository;
import com.vsiver.spring.car_rent_project.repositories.OrderRepository;
import com.vsiver.spring.car_rent_project.user.User;
import com.vsiver.spring.car_rent_project.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

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
            System.out.println("Reserving now");
            car.setInStock(false);
//            order.setOrderState(EOrderState.IN_PROCESS);//TODO: зробити коли настав час замовлення та оплата здійснена, якщо ні то скинути повідомлення на пошту
            //змінити InStock на true після завершення
        } else {//резервування
            System.out.println("reserving later");
            car.setAvailableTo(rentFrom);
            //заблокувати машину дані в момент 5 год
            //зробить update
            carReservationService.changeCarStateBeforeInProcess(rentFrom, car, order);
            //на rent to запланувати перевірку isPayed (якщо оплачено - зробити in process, якщо ні - закрити замовлення і кинути на пошту лист)
            //TODO: якщо оплата не поступила до rentTo (подивитись метод, змінити його)
        }
        System.out.println("Will be changed");
        System.out.println(order);
        System.out.println(car);
        carReservationService.setTimeOfPaymentChecking(rentFrom, car, order);

        System.out.println(order);
        carRepository.save(car);
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
