package com.ecsite.domain;

import java.util.List;

import lombok.Data;

@Data
public class Item {
    
    private Integer id;
    private String name;
    private String description;
    private Integer priceM;
    private Integer priceL;
    private String imagePath;
    private Boolean deleted;
    private List<Topping> toppingList;

}