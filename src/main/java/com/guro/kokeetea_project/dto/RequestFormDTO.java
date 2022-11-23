package com.guro.kokeetea_project.dto;

import javax.validation.constraints.NotNull;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RequestFormDTO {
    private Long id;

    @NotNull(message = "재료는 필수 입력 값입니다.")
    private Long ingredientId;

    @NotNull(message = "수량은 필수 입력 값입니다.")
    private Long amount;

    private Long storeId;
}
