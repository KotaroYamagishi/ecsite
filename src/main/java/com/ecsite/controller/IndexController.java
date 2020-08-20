package com.ecsite.controller;

import java.util.List;

import com.ecsite.domain.Item;
import com.ecsite.form.ItemSearchForm;
import com.ecsite.service.ItemService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/item-list")
public class IndexController {
    
    @Autowired
    private ItemService itemService;

    @ModelAttribute
    public ItemSearchForm setUpItemSearchForm(){
        return new ItemSearchForm();
    }

    @RequestMapping("")
    public String index(Model model){
        if(!(model.containsAttribute("itemList"))){
            List<Item> itemList=itemService.findAll();
            model.addAttribute("itemList",itemList);
        }
        return "item/item-list";
    }

    @RequestMapping("/search")
    public String itemSearch(ItemSearchForm form,Model model){
        List<Item> itemList=itemService.findBySearchName(form.getSearchName());
        model.addAttribute("itemList",itemList);
        return index(model);
    }
}