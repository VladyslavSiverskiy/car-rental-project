package com.vsiver.spring.car_rent_project.services;

import com.vsiver.spring.car_rent_project.entities.Like;
import com.vsiver.spring.car_rent_project.exceptions.NoCarWithSuchIdException;
import com.vsiver.spring.car_rent_project.exceptions.NoSuchUserException;
import com.vsiver.spring.car_rent_project.repositories.CarRepository;
import com.vsiver.spring.car_rent_project.repositories.LikeRepository;
import com.vsiver.spring.car_rent_project.user.User;
import com.vsiver.spring.car_rent_project.user.UserRepository;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Objects;


@Service
@AllArgsConstructor
public class UserService {

    private UserRepository userRepository;
    private LikeRepository likeRepository;
    private CarRepository carRepository;

    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User with email " + email + " doesn't exist"));
    }

    @SneakyThrows
    public boolean likeCar(Integer userId, Integer carId) {
        Like like = likeRepository.findByUserIdAndCarId(userId, carId);
        if(Objects.isNull(like)){
            like = new Like();
            like.setUser(
                    userRepository.findById(userId)
                            .orElseThrow(() -> new NoSuchUserException("User with id " + userId + " doesn't exist"))
            );
            like.setCar(carRepository.findById(carId)
                    .orElseThrow(() -> new NoCarWithSuchIdException("No car with id " + carId)));
            likeRepository.save(like);
        }
        return true;
    }

    public boolean removeLike(Integer userId, Integer carId) {
        System.out.println(userId + " " + carId);
        Like like = likeRepository.findByUserIdAndCarId(userId, carId);
        System.out.println(like);
        likeRepository.delete(like);
        return true;
    }

}
