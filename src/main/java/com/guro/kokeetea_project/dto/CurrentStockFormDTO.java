package com.guro.kokeetea_project.dto;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;

@Getter
@Setter
public class CurrentStockFormDTO {
    private Long id;
    private String ingredientName;
    @NotNull
    @PositiveOrZero
    private Long amount;
    private String sourceUrl;
}
