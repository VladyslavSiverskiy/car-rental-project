package com.vsiver.spring.car_rent_project.services.implementations;

import com.vsiver.spring.car_rent_project.entities.Review;
import com.vsiver.spring.car_rent_project.exceptions.NoReviewWithSuchIdException;
import com.vsiver.spring.car_rent_project.repositories.ReviewRepository;
import com.vsiver.spring.car_rent_project.services.ReviewService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.Optional;

@Service
@AllArgsConstructor
public class ReviewServiceImplementation implements ReviewService {

    private final ReviewRepository reviewRepository;

    @Override
    public Review create(Review review) {
        if (Objects.isNull(review)) throw new IllegalArgumentException("Review cannot be null");
        return reviewRepository.save(review);
    }

    @Override
    public Review update(Review review) {
         return create(review);
    }

    @Override
    public Review findById(Integer id) {
        Optional<Review> reviewOptional = reviewRepository.findById(id);
        if (reviewOptional.isPresent()) {
            return reviewOptional.get();
        } else {
            throw new NoReviewWithSuchIdException("Review with ID " + id + " doesn't exist");
        }
    }

    @Override
    public Review deleteById(Integer id) {
        return null;
    }
}
