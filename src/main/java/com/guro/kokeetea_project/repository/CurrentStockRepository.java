package com.guro.kokeetea_project.repository;

import com.guro.kokeetea_project.entity.CurrentStock;
import com.guro.kokeetea_project.entity.Ingredient;
import com.guro.kokeetea_project.entity.Warehouse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CurrentStockRepository extends JpaRepository<CurrentStock, Long> {
    @Query("select cs from CurrentStock cs where cs.warehouse = :warehouse and cs.ingredient.isValid = true and cs.warehouse.isValid = true")
    List<CurrentStock> listValidCurrentStock(@Param("warehouse") Warehouse warehouse);

    @Query("select cs from CurrentStock cs where cs.ingredient = :ingredient and cs.ingredient.isValid = true and cs.warehouse.isValid = true")
    List<CurrentStock> listValidCurrentStock(@Param("ingredient") Ingredient ingredient);

    @Query("select cs from CurrentStock cs where cs.ingredient = :ingredient and cs.warehouse = :warehouse and cs.ingredient.isValid = true and cs.warehouse.isValid = true")
    CurrentStock findValidCurrentStock(@Param("ingredient") Ingredient ingredient, @Param("warehouse") Warehouse warehouse);

    void deleteByIngredient(Ingredient ingredient);

    void deleteByWarehouse(Warehouse warehouse);
}
