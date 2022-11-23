package com.guro.kokeetea_project.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class IngredientStockInfoDTO {
    private Long id;
    private String name;
    private List<CurrentStockInfoDTO> currentStockInfoDTOList;
}
