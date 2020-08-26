package com.ecsite.domain;

import java.util.List;

import lombok.Data;

@Data
public class OrderItem {
    
    private Integer id;
    private Integer itemId;
    private Integer orderId;
    private Integer quantity;
    private Character size;
    private Integer price;
    private Integer totalPrice;
    private Item item;
    private List<OrderTopping> orderToppingList;

}