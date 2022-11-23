package com.guro.kokeetea_project.repository;

import com.guro.kokeetea_project.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    @Query("select c from Category c where c.isValid = true")
    List<Category> listCategory();

    @Query("select count(c) from Category c where c.isValid = true")
    Long countCategory();
}
