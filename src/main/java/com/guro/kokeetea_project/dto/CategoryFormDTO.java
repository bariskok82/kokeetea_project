package com.guro.kokeetea_project.dto;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
public class CategoryFormDTO {
    private Long id;

    @NotBlank(message = "이름은 필수 입력 값입니다.")
    private String name;
}
