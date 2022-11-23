package com.guro.kokeetea_project.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class IngredientInfoDTO {
    private Long id;
    private String name;
    private Long price;
    private String categoryName;
}
