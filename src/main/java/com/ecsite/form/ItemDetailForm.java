package com.ecsite.form;

import java.util.List;

import javax.validation.constraints.NotNull;

import lombok.Data;

@Data
public class ItemDetailForm {
   private String id;
   private Character size;
   private List<Integer> toppingList;
   @NotNull(message = "数量を選択してください")
   private String quantity; 
   private String totalPrice;
}