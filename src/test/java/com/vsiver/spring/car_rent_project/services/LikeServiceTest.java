package com.vsiver.spring.car_rent_project.services;

import com.vsiver.spring.car_rent_project.entities.Car;
import com.vsiver.spring.car_rent_project.entities.Like;
import com.vsiver.spring.car_rent_project.repositories.LikeRepository;
import com.vsiver.spring.car_rent_project.user.User;
import com.vsiver.spring.car_rent_project.user.UserRepository;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.ApplicationContext;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest
class LikeServiceTest {

    @MockBean
    LikeRepository likeRepository;
    @Autowired
    LikeService likeService;

    @Autowired
    ApplicationContext context;


    @Test
    void retrieveAllLikesByUserId() {
        List<Like> likes = new ArrayList<>();
        Car car = new Car();
        car.setCarId(1);
        User firstUser = new User();
        firstUser.setId(1);
        User secondUser = new User();
        secondUser.setId(2);
        Like like1 = new Like();
        like1.setLikeId(4L);
        like1.setUser(firstUser);
        like1.setCar(car);
        Like like2 = new Like();
        like2.setLikeId(5L);
        like2.setUser(firstUser);
        like2.setCar(car);
        Like like3 = new Like();
        like3.setLikeId(6L);
        like3.setUser(secondUser);
        like3.setCar(car);
        likes.add(like1);
        likes.add(like2);

        Mockito.when(likeRepository.findAllByUserId(firstUser.getId()))
                .thenReturn(likes);
        List<Like> actual = likeService.retrieveAllLikesByUserId(firstUser.getId());
        List<Like> expected = List.of(like1, like2);
        assertEquals(expected, actual);
    }
}