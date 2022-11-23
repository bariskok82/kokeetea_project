package com.guro.kokeetea_project.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class StatOfRequestByMonthIngredient {
    private Long ingredientId;
    private String ingredientName;
    private Long categoryId;
    private String categoryName;
    private Long value;
}