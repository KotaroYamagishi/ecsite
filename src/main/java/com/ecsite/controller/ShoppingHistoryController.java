package com.ecsite.controller;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import javax.servlet.http.HttpSession;

import com.ecsite.domain.Order;
import com.ecsite.domain.OrderItem;
import com.ecsite.domain.User;
import com.ecsite.service.ShoppingCartService;
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
    public String showShoppingcartHistory(Model model) {
        Order order = new Order();
        User user = (User) session.getAttribute("user");
        order.setUserId(user.getId());
        List<Order> orderHistoryList = shoppingCartService.findOrdersAndOrderItemAndOrderTopping(order);
        if (orderHistoryList.size() == 0) {
            model.addAttribute("emptyMessage", "履歴はありません");
        } else {
            orderHistoryList.forEach(orderHistory -> {
                List<OrderItem> orderItemList = orderHistory.getOrderItemList();
                orderItemList.forEach(orderItem -> {
                    shoppingCartService.orderItemSetItemAndTopping(orderItem);
                });
                Date orderDate=orderHistory.getOrderDate();
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日（E）kk:mm:ss");
                orderHistory.setFormatOrderDate(sdf.format(orderDate));
            });
            model.addAttribute("orderList", orderHistoryList);
        }

        userService.isLogin(model);
        return "shoppingcart/cart_history";
    }
}