package com.vsiver.spring.car_rent_project.services;

import com.vsiver.spring.car_rent_project.entities.Like;
import com.vsiver.spring.car_rent_project.repositories.LikeRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class LikeService {

    private LikeRepository likeRepository;

    public List<Like> retrieveAllLikesByUserId(Integer userId){
        return likeRepository.findAllByUserId(userId);
    }
}
