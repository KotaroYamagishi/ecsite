package com.ecsite.controller;

import java.util.List;

import com.ecsite.domain.Item;
import com.ecsite.domain.Topping;
import com.ecsite.form.ItemDetailForm;
import com.ecsite.service.ItemService;
import com.ecsite.service.ToppingService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/item-detail")
public class ItemDetailController {
    
    @Autowired
    private ItemService itemService;

    @Autowired
    private ToppingService toppingService;

    @ModelAttribute
    public ItemDetailForm setUpForm(){
        return new  ItemDetailForm();
    }

    @RequestMapping("/showDetail")
    public String itemDetail(Model model,String itemId){
        Item item=itemService.findById(Integer.parseInt(itemId));
        List<Topping> toppingList=toppingService.findAll();
        item.setToppingList(toppingList);
        model.addAttribute("item",item);
        return "item/item_detail";
    }

    //　値を確認する用
    @RequestMapping("/add-cart")
    public String addCart(ItemDetailForm form){
        return "cart_list";
    }
}