package com.guro.kokeetea_project.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StoreInfoDTO {
    private Long id;
    private String name;
    private String location;
    private String phone;
    private String email;
    private String warehouseName;
}
