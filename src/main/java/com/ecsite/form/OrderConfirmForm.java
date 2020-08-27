package com.ecsite.form;


import javax.validation.constraints.NotBlank;

import lombok.Data;

@Data
public class OrderConfirmForm {
    @NotBlank(message = "名前を入力してください")
    private String destinationName;
    @NotBlank(message = "メールアドレスを入力してください")
    private String destinationEmail;
    @NotBlank(message = "郵便番号を入力してください")
    private String destinationZipcode;
    @NotBlank(message = "住所を入力してください")
    private String destinationAddress;
    @NotBlank(message = "電話番号を入力してください")
    private String destinationTel;
    @NotBlank(message = "配達日時を入力してください")
    private String deliveryTime;
    private String delivaryTimeHour;
    private String paymentMethod;

}