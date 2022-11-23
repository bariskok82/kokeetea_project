package com.guro.kokeetea_project.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class StatOfRequestByMonth {
    private Integer year;
    private Integer month;
    private Long value;
}