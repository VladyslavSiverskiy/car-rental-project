package com.vsiver.spring.car_rent_project.controllers;

import com.vsiver.spring.car_rent_project.config.JwtService;
import com.vsiver.spring.car_rent_project.dtos.CreatedOrderDto;
import com.vsiver.spring.car_rent_project.dtos.OrderDto;
import com.vsiver.spring.car_rent_project.dtos.RequestOrderDto;
import com.vsiver.spring.car_rent_project.dtos.UserDto;
import com.vsiver.spring.car_rent_project.exceptions.CarOutOfStockException;
import com.vsiver.spring.car_rent_project.exceptions.IncorrectRentTimeException;
import com.vsiver.spring.car_rent_project.exceptions.NoCarWithSuchIdException;
import com.vsiver.spring.car_rent_project.exceptions.NoUserWithSuchIdException;
import com.vsiver.spring.car_rent_project.services.*;
import com.vsiver.spring.car_rent_project.user.User;
import com.vsiver.spring.car_rent_project.utils.CustomMappers;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@RestController
@AllArgsConstructor
@RequestMapping("api/v1/user")
public class UserController {
    private S3Service s3Service;
    private OrderService orderService;
    private PaymentService paymentService;
    private JwtService jwtService;
    private UserService userService;
    private LikeService likeService;

    @PostMapping("/data")
    public ResponseEntity<UserDto> getUserDataByJwtToken(@RequestBody String jwtToken) {
        User user = userService.getUserByEmail(jwtService.extractUsername(jwtToken));
        UserDto userDto = UserDto.builder()
                .id(user.getId())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .phoneNumber(user.getPhoneNumber())
                .build();
        return ResponseEntity.ok(userDto);
    }

    /**
     * Method, that create like in database
     *
     * @param userId - user who set like
     * @param carId  - car which was liked
     * @return
     */
    @GetMapping("{userId}/like-car/{carId}")
    public ResponseEntity<List<Integer>> likeCar(@PathVariable Integer userId, @PathVariable Integer carId) {
        userService.likeCar(userId, carId);
        return ResponseEntity.ok(likeService.retrieveAllLikesByUserId(userId)
                .stream()
                .map(like -> like.getCar().getCarId())
                .toList());
    }

    /**
     * @param userId - id of user, who wants to remove like
     * @param carId - car, where like should be removed
     * @return
     */
    @GetMapping("{userId}/like-car/{carId}/delete")
    public ResponseEntity<List<Integer>> removeLike(@PathVariable Integer userId, @PathVariable Integer carId) {
        userService.removeLike(userId, carId);
        return ResponseEntity.ok(likeService.retrieveAllLikesByUserId(userId)
                .stream()
                .filter(Objects::nonNull)
                .map(like -> like.getCar().getCarId())
                .toList());
    }


    /**
     * Method, which returns list of car ID's, which user previously liked
     *
     * @param userId
     * @return
     */
    @GetMapping("{userId}/likes")
    public ResponseEntity<List<Integer>> getUserCarLikes(@PathVariable Integer userId) {
        return ResponseEntity.ok(likeService.retrieveAllLikesByUserId(userId)
                .stream()
                .map(like -> like.getCar().getCarId())
                .toList()
        );
    }

    /** Create PayPal order
     * @param requestOrder
     * @param request
     * @return
     * @throws IOException
     */
    @PostMapping("/orders/create")
    public ResponseEntity<CreatedOrderDto> createOrder(@RequestBody RequestOrderDto requestOrder, HttpServletRequest request) throws IOException, NoCarWithSuchIdException, NoUserWithSuchIdException, CarOutOfStockException, IncorrectRentTimeException {
        URI returnUrl = buildReturnUrl(request);
        System.out.println(requestOrder);
        requestOrder.setRentFrom(requestOrder.getRentFrom().plusHours(3));
        requestOrder.setRentTo(requestOrder.getRentTo().plusHours(3));
        if (Objects.isNull(requestOrder.getTotalAmount())) throw new IllegalArgumentException("Amount is null");
        CreatedOrderDto createdOrder = paymentService.createOrder(requestOrder.getTotalAmount(), returnUrl);
        orderService.createProgramOrder(
                requestOrder.getRentFrom(),
                requestOrder.getRentTo(),
                BigDecimal.valueOf(requestOrder.getTotalAmount()),
                createdOrder.getOrderId(),
                createdOrder.getApprovalLink().toString(),
                requestOrder.getCarId(),
                requestOrder.getUserId()
        );
        CreatedOrderDto createdOrderDto = CreatedOrderDto.builder()
                .orderId(createdOrder.getOrderId())
                .approvalLink(createdOrder.getApprovalLink())
                .build();
        return ResponseEntity.ok(createdOrderDto);
    }



    /** returns list of orders of User by passing userId
     * @param userId
     * @return
     */
    @GetMapping("/orders/all/{userId}")
    public ResponseEntity<List<OrderDto>> retrieveOrders(@PathVariable Integer userId) {
         return ResponseEntity.ok(
                orderService.getOrdersByUserId(userId)
                        .stream()
                        .map(CustomMappers::mapOrderToOrderDto)
                        .collect(Collectors.toList())
        );
    }

    @PostMapping(value = "/profile/avatar/{userId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Boolean> uploadCarPicture(
            @RequestParam("file") MultipartFile file,
            @PathVariable Integer userId
    ) throws IOException {
        s3Service.putObject(
                "car-app-bucket",
                "users/user" + userId + "/avatar.jpg",
                file.getBytes()
        );
        return ResponseEntity.ok(true);
    }

    private URI buildReturnUrl(HttpServletRequest request) {
        try {
            URI requestUri = URI.create(request.getRequestURL().toString());
            return new URI(requestUri.getScheme(),
                    requestUri.getUserInfo(),
                    requestUri.getHost(),
                    requestUri.getPort(),
                    "/api/v1/public/orders/capture",
                    null, null);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }
}
