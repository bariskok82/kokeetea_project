package com.guro.kokeetea_project.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "category")
@Getter
@Setter
public class Category {
    @Id
    @Column(name = "category_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String name;

    @OneToMany(mappedBy = "category")
    private List<Ingredient> ingredient = new ArrayList<>();

    public void addIngredient(Ingredient ingredient) {
        if (!this.ingredient.contains(ingredient)) {
            this.ingredient.add(ingredient);
        }
        if (ingredient.getCategory() != this) {
            ingredient.setCategory(this);
        }
    }

    @Column
    private Boolean isValid;
}
