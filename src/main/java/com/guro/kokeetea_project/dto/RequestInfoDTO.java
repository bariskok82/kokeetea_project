package com.guro.kokeetea_project.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RequestInfoDTO {
    private Long id;
    private String ingredientName;
    private Long amount;
    private String storeName;
    private String warehouseName;
    private String status;
    private String date;
    private Boolean canConfirm = false;
    private Boolean canComplete = false;
    private Boolean canReject = false;
    private Boolean canCancel = false;
}
