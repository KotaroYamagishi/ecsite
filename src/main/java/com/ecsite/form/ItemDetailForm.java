package com.ecsite.form;

import java.util.List;

import javax.validation.constraints.NotNull;

import lombok.Data;

@Data
public class ItemDetailForm {
   private String size;
   private List<Integer> toppingList;
   @NotNull(message = "数量を選択してください")
   private Integer quantity; 
}