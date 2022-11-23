package com.guro.kokeetea_project.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class StatisticsDTO {
    private List<StatOfRequestByMonth> countByMonth;
    private Integer countByMonthMaxIndex;
    private List<StatOfRequestByMonthIngredient> statOfRequestByMonthIngredient;
    private List<StatOfRequestByMonthStore> statOfRequestByMonthStore;
}
