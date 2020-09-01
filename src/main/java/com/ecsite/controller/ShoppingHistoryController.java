package com.ecsite.controller;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;
import java.util.stream.Collector.Characteristics;

import javax.servlet.http.HttpSession;

import com.ecsite.domain.Order;
import com.ecsite.domain.OrderItem;
import com.ecsite.domain.User;
import com.ecsite.service.OrdersService;
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
    private OrdersService ordersService;

    @Autowired
    private UserService userService;

    @Autowired
    private HttpSession session;

    
    /** 
     * 商品履歴を表示
     * @param model
     * @return String
     */
    @RequestMapping("")
    public String showShoppingcartHistory(Model model) {
        Order order = new Order();
        User user = (User) session.getAttribute("user");
        order.setUserId(user.getId());
        List<Order> orderHistoryList = ordersService.findOrdersAndOrderItemAndOrderTopping(order);
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
            List<Order> reverseOrderHistoryList=orderHistoryList.stream().collect(reverse());
            model.addAttribute("orderList", reverseOrderHistoryList);
        }

        userService.isLogin(model);
        return "shoppingcart/cart_history";
    }

    
    /** 
     * @return Collector<T, List<T>, List<T>>
     */
    public static <T> Collector<T, List<T>, List<T>> reverse() {

        return new Collector<T, List<T>, List<T>>() {
            @Override
            public Supplier<List<T>> supplier() {
                return ArrayList::new;
            }

            @Override
            public BiConsumer<List<T>, T> accumulator() {
                return (l, e) -> l.add(0, e);
            }

            @Override
            public BinaryOperator<List<T>> combiner() {
                return (l, subl) -> {
                    l.addAll(0, subl);
                    return l;
                };
            }

            @Override
            public Function<List<T>, List<T>> finisher() {
                // 最終的な集計処理は必要ないので、そのままリストを返す
                return l -> l;
            }

            @Override
            public Set<Characteristics> characteristics() {
                // 並列処理可
                return EnumSet.of(Characteristics.CONCURRENT);
            }
        };
    }
}