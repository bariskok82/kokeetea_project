package com.guro.kokeetea_project.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RequestDetailDTO {
    private Long id;
    private String sourceUrl;
    
    private String date;
    private String status;
    private Boolean canConfirm = false;
    private Boolean canComplete = false;
    private Boolean canReject = false;
    private Boolean canCancel = false;

    private String ingredientName;
    private String categoryName;
    private Long ingredientPrice;
    private Long amount;
    private Long totalPrice;

    private String storeName;
    private String storeLocation;
    private String storePhone;
    private String storeEmail;

    private String warehouseName;
    private String warehouseLocation;
    private String warehousePhone;
    private String warehouseEmail;
}
