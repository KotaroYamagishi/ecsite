package com.ecsite.controller;

import java.util.List;

import javax.servlet.http.HttpSession;

import com.ecsite.domain.Order;
import com.ecsite.domain.User;
import com.ecsite.service.ShoppingCartService;
import com.ecsite.service.ShoppingHistoryService;
import com.ecsite.service.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/shoppingcart-history")
public class ShoppingHistoryController {

    @Autowired
    private ShoppingCartService shoppingCartService;


    @Autowired
    private UserService userService;

    @Autowired
    private HttpSession session;
    
    @RequestMapping("")
    public String showShoppingcart(Model model){
        Order order=new Order();
        User user=(User) session.getAttribute("user");
        order.setUserId(user.getId());
        order.setStatus(0);
        Order orderList = shoppingCartService.findOrdersAndOrderItemAndOrderTopping(order);
        return "";
    }
}