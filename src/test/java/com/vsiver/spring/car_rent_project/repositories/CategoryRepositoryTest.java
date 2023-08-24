package com.vsiver.spring.car_rent_project.repositories;

import com.vsiver.spring.car_rent_project.entities.Category;
import com.vsiver.spring.car_rent_project.entities.ECategories;
import jakarta.validation.constraints.AssertTrue;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@Transactional
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class CategoryRepositoryTest {

    @Autowired
    private CategoryRepository categoryRepository;

    @Test
    void findByCategoryNameTest() {
        Category category = new Category();
        category.setCategoryName(ECategories.SUV);
        categoryRepository.save(category);
        String categoryName = "SUV";
        Optional<Category> categoryOptional =
                categoryRepository.findByCategoryName(ECategories.valueOf(categoryName));
        assertEquals(ECategories.SUV, categoryOptional.get().getCategoryName());
    }

    @Test
    void throwIllegalArgumentExceptionWhenCategoryDoesntExistTest() {
        String categoryName = "DOESNT_EXIST";
        assertThrows(IllegalArgumentException.class, () -> {
            categoryRepository.findByCategoryName(ECategories.valueOf(categoryName));
        });
    }
}