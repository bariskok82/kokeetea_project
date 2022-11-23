package com.guro.kokeetea_project.repository;

import com.guro.kokeetea_project.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;

import com.guro.kokeetea_project.entity.Ingredient;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface IngredientRepository extends JpaRepository<Ingredient, Long> {
    @Query("select i from Ingredient i where i.isValid = true")
    List<Ingredient> listIngredient();

    @Query("select count(i) from Ingredient i where i.isValid = true")
    Long countIngredient();

    void deleteByCategory(Category category);
}
