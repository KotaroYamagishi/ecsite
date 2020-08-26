package com.ecsite.domain;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

import lombok.Data;

@Data
public class Order {
    
    private Integer id;
    private Integer userId;
    private Integer status;
    private Integer totalPrice;
    // 注文日
    private Date orderDate;
    private String destinationName;
    private String destinationEmail;
    private String destinationZipcode;
    private String destinationAddress;
    private String destinationTel;
    // 配達日時
    private Timestamp deliveryTime;
    private Integer paymentMethod;
    private User user;
    private List<OrderItem> orderItemList;

}