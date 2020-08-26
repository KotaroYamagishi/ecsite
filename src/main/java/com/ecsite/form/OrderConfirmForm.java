package com.ecsite.form;


import lombok.Data;

@Data
public class OrderConfirmForm {
    private String destinationName;
    private String destinationEmail;
    private String destinationZipcode;
    private String destinationAddress;
    private String destinationTel;
    private String deliveryTime;
    private String delivaryTimeHour;
    private String paymentMethod;

}