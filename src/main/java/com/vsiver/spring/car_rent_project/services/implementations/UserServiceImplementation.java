package com.vsiver.spring.car_rent_project.services.implementations;

import com.vsiver.spring.car_rent_project.dtos.UserDto;
import com.vsiver.spring.car_rent_project.entities.Like;
import com.vsiver.spring.car_rent_project.exceptions.NoCarWithSuchIdException;
import com.vsiver.spring.car_rent_project.exceptions.NoSuchUserException;
import com.vsiver.spring.car_rent_project.exceptions.NoUserWithSuchIdException;
import com.vsiver.spring.car_rent_project.repositories.CarRepository;
import com.vsiver.spring.car_rent_project.repositories.LikeRepository;
import com.vsiver.spring.car_rent_project.services.UserService;
import com.vsiver.spring.car_rent_project.user.User;
import com.vsiver.spring.car_rent_project.user.UserRepository;
import com.vsiver.spring.car_rent_project.utils.CustomMappers;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;


@Service
@AllArgsConstructor
public class UserServiceImplementation implements UserService {

    private UserRepository userRepository;
    private LikeRepository likeRepository;
    private CarRepository carRepository;

    @Override
    public User readUserById(Integer id) throws NoUserWithSuchIdException {
        return userRepository.findById(id).orElseThrow(()->new NoUserWithSuchIdException("No user with id " + id));
    }

    @Override
    public List<User> readAll() {
        return null;
    }

    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User with email " + email + " doesn't exist"));
    }

    @Override
    public User updateUser(UserDto userDto){
        User user = CustomMappers.mapUserDtoToUser(userDto);
        System.out.println(user);
        return userRepository.save(user);
    }

    @Override
    public boolean deleteUser(Integer userId) {
        return false;
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
