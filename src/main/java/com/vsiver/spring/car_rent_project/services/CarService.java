package com.vsiver.spring.car_rent_project.services;


import com.vsiver.spring.car_rent_project.entities.Category;
import com.vsiver.spring.car_rent_project.entities.ECategories;
import com.vsiver.spring.car_rent_project.repositories.CarRepository;
import com.vsiver.spring.car_rent_project.repositories.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CarService {

    @Autowired
    private CarRepository carRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    public void saveCar(){
        Category category1 = new Category();
        category1.setCategoryName(ECategories.ECONOMY);

        Category category2 = new Category();
        category1.setCategoryName(ECategories.COMFORT);

        Category category3 = new Category();
        category1.setCategoryName(ECategories.BUSINESS);

        Category category4 = new Category();
        category1.setCategoryName(ECategories.SUV);
        categoryRepository.save(category1);
        categoryRepository.save(category2);
        categoryRepository.save(category3);
        categoryRepository.save(category4);
    }
}
