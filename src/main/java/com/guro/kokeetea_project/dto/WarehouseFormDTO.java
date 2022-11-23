package com.guro.kokeetea_project.dto;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
public class WarehouseFormDTO {
    private Long id;

    @NotBlank(message = "이름은 필수 입력 값입니다.")
    private String name;

    @NotBlank(message = "지역은 필수 입력 값입니다.")
    private String location;

    @NotBlank(message = "연락처는 필수 입력 값입니다.")
    private String phone;

    @NotBlank(message = "이메일은 필수 입력 값입니다.")
    private String email;
}
