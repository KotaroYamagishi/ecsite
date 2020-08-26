package com.ecsite.controller;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import javax.servlet.http.HttpSession;

import com.ecsite.domain.Item;
import com.ecsite.domain.Order;
import com.ecsite.domain.OrderItem;
import com.ecsite.domain.OrderTopping;
import com.ecsite.domain.Topping;
import com.ecsite.domain.User;
import com.ecsite.form.ItemDetailForm;
import com.ecsite.form.OrderConfirmForm;
import com.ecsite.service.ItemService;
import com.ecsite.service.ShoppingCartService;
import com.ecsite.service.ToppingService;

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
    @Autowired
    private ItemService itemService;
    @Autowired
    private ToppingService toppingService;
    @Autowired
    private HttpSession session;

    @ModelAttribute
    public ItemDetailForm setUpItemDetailForm() {
        return new ItemDetailForm();
    }

    @ModelAttribute
    public OrderConfirmForm setUpOrderConfirmForm() {
        return new OrderConfirmForm();
    }

    @RequestMapping("")
    public String index(Model model) {
        Order order = setUserIdAndStatus0();
        showShoppingCart(order, model);
        return "shoppingcart/cart_list";
    }

    // 商品を追加し、ショッピングカートを表示する機能
    @RequestMapping("/addCart")
    public String addShoppingCart(@Validated ItemDetailForm form, BindingResult result, Model model) {
        if (result.hasErrors()) {
            return "forward:/item-detail/showDetail";
        }
        Order order = setUserIdAndStatus0();
        OrderItem orderItem = new OrderItem();
        order.setTotalPrice(Integer.parseInt(form.getTotalPrice()));
        orderItem.setItemId(Integer.parseInt(form.getId()));
        orderItem.setQuantity(Integer.parseInt(form.getQuantity()));
        orderItem.setSize(form.getSize());
        List<OrderTopping> orderToppingList = new ArrayList<>();
        form.getToppingList().forEach(toppingId -> {
            OrderTopping orderTopping = new OrderTopping();
            orderTopping.setToppingId(toppingId);
            orderTopping.setTopping(toppingService.findById(toppingId));
            orderToppingList.add(orderTopping);
        });
        orderItem.setOrderToppingList(orderToppingList);
        shoppingCartService.addShoppingCart(order, orderItem);
        // shoppingcartの中身を表示する処理
        showShoppingCart(order, model);
        return "shoppingcart/cart_list";
    }

    @RequestMapping("/delete")
    public String delete(Model model) {
        Order order = setUserIdAndStatus0();
        showShoppingCart(order, model);
        return "shoppingcart/cart_list";
    }

    @RequestMapping("/toOrder")
    public String toOrder(Model model) {
        Order order = setUserIdAndStatus0();
        showShoppingCart(order, model);
        // deliveryTimeに初期値を設定する（今の時間）
        return "order/order_confirm";
    }

    @RequestMapping("/order")
    public String order(@Validated OrderConfirmForm form, BindingResult result, Model model) {
        if (result.hasErrors()) {
            return index(model);
        }
        Timestamp orderTimestamp = setDateTime(form);
        Integer payMethod = Integer.parseInt(form.getPaymentMethod());
        Order order = setUserIdAndStatus0();
        order.setDestinationName(form.getDestinationName());
        order.setDestinationEmail(form.getDestinationEmail());
        order.setDestinationZipcode(form.getDestinationZipcode());
        order.setDestinationAddress(form.getDestinationAddress());
        order.setDestinationTel(form.getDestinationTel());
        order.setDeliveryTime(orderTimestamp);
        order.setPaymentMethod(payMethod);
        order.setOrderDate(new Date());
        Integer status = null;
        // 支払い方法で入金済みか未入金か判断
        if (payMethod == 1) {
            status = 1;
        } else if (payMethod == 2) {
            status = 2;
        }
        shoppingCartService.applyOrder(order, status);

        return "order/order_finished";
    }

    // shoppingcartの中身を表示する処理
    private void showShoppingCart(Order order, Model model) {
        Order findOrder = shoppingCartService.findOrdersAndOrderItemAndOrderTopping(order);
        if (Objects.isNull(findOrder)) {
            model.addAttribute("emptyMessage", "ショッピングカートは空です");
        } else {
            List<OrderItem> orderItemList = findOrder.getOrderItemList();
            orderItemList.forEach(orderItem -> {
                Item item = itemService.findById(orderItem.getItemId());
                orderItem.setItem(item);
                Integer price = null;
                Integer toppingQuantity = 0;
                Integer toppingPrice = 0;
                List<OrderTopping> orderToppingList = orderItem.getOrderToppingList();
                if (Objects.nonNull(orderToppingList)) {
                    toppingQuantity = orderToppingList.size();
                    // orderToppingに情報を詰める
                    orderToppingList.forEach(orderTopping -> {
                        // thymeleafの中でサイズ別に値段を表示する
                        Topping topping = toppingService.findById(orderTopping.getToppingId());
                        orderTopping.setTopping(topping);
                    });
                }
                if (Objects.equals(orderItem.getSize(), 'M')) {
                    price = item.getPriceM();
                    orderItem.setPrice(price);
                    toppingPrice = 200 * toppingQuantity;
                } else {
                    price = item.getPriceL();
                    orderItem.setPrice(price);
                    toppingPrice = 300 * toppingQuantity;
                }
                Integer totalPrice = (price + toppingPrice) * orderItem.getQuantity();
                orderItem.setTotalPrice(totalPrice);
            });
            // 消費税と税込合計金額を計算しmodelへ
            Integer totalPrice = findOrder.getTotalPrice();
            Integer taxPrice = (int) (totalPrice * 0.1);
            Integer includingTaxTotalPrice = totalPrice + taxPrice;
            model.addAttribute("orderItemList", orderItemList);
            model.addAttribute("taxPrice", taxPrice);
            model.addAttribute("includingTaxTotalPrice", includingTaxTotalPrice);
        }
    }

    // sessionに入ったuserIdとstatusに0をorderにsetするメソッド
    // sessionにuserが入ってなかったら仮のidを発行してあげる必要あり
    private Order setUserIdAndStatus0() {
        Order order = new Order();
        User user = (User) session.getAttribute("user");
        //未ログインユーザーだった場合
        if (Objects.isNull(user)) {
            String id = UUID.randomUUID().toString().replaceAll("[^0-9]", "");
            user = new User();
            user.setId(Integer.parseInt(id));
            user.setName("ゲスト");
            session.setAttribute("user", user);
        }
        order.setUserId(user.getId());
        order.setStatus(0);
        return order;
    }

    // orderConfirmFormで受け取った日時をtimestamp型に変換するメソッド
    private Timestamp setDateTime(OrderConfirmForm form) {
        String delivery = form.getDeliveryTime() + " " + form.getDelivaryTimeHour();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh");
        Date parsedDate = null;
        try {
            parsedDate = dateFormat.parse(delivery);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Timestamp timestamp = new java.sql.Timestamp(parsedDate.getTime());
        return timestamp;
    }
}