package com.ecsite.form;

import java.util.List;

import javax.validation.constraints.NotBlank;

import lombok.Data;

@Data
public class ItemDetailForm {
   private Integer price;
   private List<Integer> toppingList;
   @NotBlank(message = "数量を選択してください")
   private Integer quantity; 
}