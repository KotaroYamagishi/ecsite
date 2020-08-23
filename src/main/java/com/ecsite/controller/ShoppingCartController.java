package com.ecsite.controller;

import com.ecsite.form.ItemDetailForm;
import com.ecsite.service.ShoppingCartService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@Validated
@RequestMapping("/shoppingcart")
public class ShoppingCartController {

    @Autowired
    private ShoppingCartService shoppingCartService;
    
    @ModelAttribute
    public ItemDetailForm setUpForm(){
        return new  ItemDetailForm();
    }

    @RequestMapping("")
    public String toShoppingCart(){
        return "shoppingcart/cart_list";
    }

    @RequestMapping("/addCart")
    public String addShoppingCart(@Validated ItemDetailForm form,BindingResult result,Model model){
        if(result.hasErrors()){
            return "forward:/item-detail/showDetail";
        }
        
        return "shoppingcart/cart_list";
    }
}