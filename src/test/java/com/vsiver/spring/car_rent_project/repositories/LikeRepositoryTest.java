package com.vsiver.spring.car_rent_project.repositories;

import com.vsiver.spring.car_rent_project.entities.Car;
import com.vsiver.spring.car_rent_project.entities.Like;
import com.vsiver.spring.car_rent_project.user.Role;
import com.vsiver.spring.car_rent_project.user.User;
import com.vsiver.spring.car_rent_project.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;


@DataJpaTest
@Transactional
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class LikeRepositoryTest {

    @Autowired
    private LikeRepository likeRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CarRepository carRepository;

    @BeforeEach
    void setUpBeforeEach() {
        userRepository.deleteAll();
        userRepository.flush();
        carRepository.deleteAll();
        carRepository.flush();
        likeRepository.deleteAll();
        likeRepository.flush();
    }

    @Test
    void findByUserIdAndCarId() {
        Car car = new Car();
        car.setCarModel("Some car");
        car = carRepository.save(car);

        User user = new User();
        user.setEmail("john.doe@mail.com");
        user.setPass("password");
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setRole(Role.USER);
        user = userRepository.save(user);

        Like like = new Like();
        like.setCar(car);
        like.setUser(user);

        likeRepository.save(like);
        assertNotNull(likeRepository.findByUserIdAndCarId(user.getId(), car.getCarId()));
    }
}