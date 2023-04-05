package com.vsiver.spring.car_rent_project.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name = "categories")
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "category_id")
    private Integer categoryId;

    @NotNull
    @Column(name = "category_name")
    @Enumerated(EnumType.STRING)
    private ECategories categoryName;

    public Category() {
    }

    public Category(Integer categoryId, ECategories categoryName) {
        this.categoryId = categoryId;
        this.categoryName = categoryName;
    }

    public Integer getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Integer categoryId) {
        this.categoryId = categoryId;
    }

    public ECategories getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(ECategories categoryName) {
        this.categoryName = categoryName;
    }
}
