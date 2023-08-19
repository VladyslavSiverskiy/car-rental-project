package com.vsiver.spring.car_rent_project.services;

import com.vsiver.spring.car_rent_project.dtos.UserDto;
import com.vsiver.spring.car_rent_project.exceptions.NoUserWithSuchIdException;
import com.vsiver.spring.car_rent_project.user.User;

import java.util.List;

public interface UserService {

    User readUserById(Integer userId) throws NoUserWithSuchIdException;
    List<User> readAll();
    User updateUser(UserDto user);

    boolean deleteUser(Integer userId);

}
