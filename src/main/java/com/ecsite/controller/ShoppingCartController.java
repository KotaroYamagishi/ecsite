package com.ecsite.controller;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Random;

import javax.servlet.http.HttpSession;

import com.ecsite.domain.Order;
import com.ecsite.domain.OrderItem;
import com.ecsite.domain.OrderTopping;
import com.ecsite.domain.User;
import com.ecsite.form.ItemDetailForm;
import com.ecsite.form.OrderConfirmForm;
import com.ecsite.service.ItemService;
import com.ecsite.service.OrdersService;
import com.ecsite.service.SendMailService;
import com.ecsite.service.ShoppingCartService;
import com.ecsite.service.ToppingService;
import com.ecsite.service.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.thymeleaf.context.Context;

@Controller
@Validated
@RequestMapping("/shoppingcart")
public class ShoppingCartController {

    @Autowired
    private ShoppingCartService shoppingCartService;
    @Autowired
    private OrdersService ordersService;
    @Autowired
    private ToppingService toppingService;
    @Autowired
    private UserService userService;
    @Autowired
    private SendMailService sendMailService;
    @Autowired
    private HttpSession session;

    
    /** 
     * @return ItemDetailForm
     */
    @ModelAttribute
    public ItemDetailForm setUpItemDetailForm() {
        return new ItemDetailForm();
    }

    
    /** 
     * @return OrderConfirmForm
     */
    @ModelAttribute
    public OrderConfirmForm setUpOrderConfirmForm() {
        return new OrderConfirmForm();
    }

    
    /** 
     * 初期表示
     * @param model
     * @return String
     */
    @RequestMapping("")
    public String index(Model model) {
        Order order = setUserIdAndStatus0();
        showShoppingCart(order, model);
        return "shoppingcart/cart_list";
    }

    
    /** 
     * 商品を追加し、ショッピングカートを表示する機能
     * @param form
     * @param result
     * @param model
     * @return String
     */
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

    
    /** 
     * 商品削除
     * @param model
     * @param orderItemId
     * @return String
     */
    @RequestMapping("/delete")
    public String delete(Model model, String orderItemId) {
        shoppingCartService.deleteOrderItemsAndOrdertoppings(Integer.parseInt(orderItemId));
        Order order = setUserIdAndStatus0();
        showShoppingCart(order, model);
        return "shoppingcart/cart_list";
    }

    
    /** 
     * 注文確認画面表示
     * @param model
     * @return String
     */
    @RequestMapping("/toOrder")
    public String toOrder(Model model) {
        Order order = setUserIdAndStatus0();
        model.addAttribute("nowDate",new Date());
        showShoppingCart(order, model);
        // deliveryTimeに初期値を設定する（今の時間）
        return "order/order_confirm";
    }

    
    /** 
     * 注文時
     * @param form
     * @param result
     * @param model
     * @return String
     */
    @RequestMapping("/order")
    public String order(@Validated OrderConfirmForm form, BindingResult result, Model model) {
        if (result.hasErrors()) {
            return toOrder(model);
        }
        Date nowDate=new Date();
        Timestamp orderTimestamp = setDateTime(form);
        Date orderDate=new Date(orderTimestamp.getTime());
        if(orderDate.before(nowDate)) {
            model.addAttribute("errorMessage", "この時間は選択できません");
            return toOrder(model);
        }
        Integer payMethod = Integer.parseInt(form.getPaymentMethod());
        Order order = setUserIdAndStatus0();
        order.setDestinationName(form.getDestinationName());
        order.setDestinationEmail(form.getDestinationEmail());
        order.setDestinationZipcode(form.getDestinationZipcode());
        order.setDestinationAddress(form.getDestinationAddress());
        order.setDestinationTel(form.getDestinationTel());
        order.setDeliveryTime(orderTimestamp);
        order.setPaymentMethod(payMethod);
        order.setOrderDate(nowDate);
        Integer status = null;
        // 支払い方法で入金済みか未入金か判断
        if (payMethod == 1) {
            status = 1;
        } else if (payMethod == 2) {
            status = 2;
        }
        ordersService.applyOrder(order, status);

        Order sendMailOrder=new Order();
        sendMailOrder.setUserId(order.getUserId());

        List<Order> ordersList = ordersService.findOrdersAndOrderItemAndOrderTopping(sendMailOrder);
        Collections.reverse(ordersList);
		Order orderWhatBoughtLatest = ordersList.get(0);
		Context context = new Context();

        User user = (User) session.getAttribute("user");
		context.setVariable("name", user.getName());
		// context.setVariable("deliveryTime", ldtForMail);
		context.setVariable("orderList", orderWhatBoughtLatest);

		sendMailService.sendMail(context, order); 
        // ユーザーがログインしているかどうか
        userService.isLogin(model);
        
        return "order/order_finished";
    }

    
    /** 
     * shoppingcartの中身を表示する処理
     * @param order
     * @param model
     */
    private void showShoppingCart(Order order, Model model) {
        List<Order> findOrderList = ordersService.findOrdersAndOrderItemAndOrderTopping(order);
        Order findOrder =new Order();
        if(findOrderList.size()==0){
            findOrder =null;
        }else{
            findOrder =findOrderList.get(0);
        }
        if (Objects.isNull(findOrder)) {
            model.addAttribute("emptyMessage", "ショッピングカートは空です");
        } else if (Objects.nonNull(findOrder) && Objects.equals(findOrder.getTotalPrice(), 0)) {
            User user= (User) session.getAttribute("user");
            if(Objects.isNull(user.getName())){
                session.removeAttribute("user");
            }
            shoppingCartService.deleteOrders(findOrder.getId());
            model.addAttribute("emptyMessage", "ショッピングカートは空です");
        } else {
            List<OrderItem> orderItemList = findOrder.getOrderItemList();
            orderItemList.forEach(orderItem -> {
                shoppingCartService.orderItemSetItemAndTopping(orderItem);
            });
            // 消費税と税込合計金額を計算しmodelへ
            Integer totalPrice = findOrder.getTotalPrice();
            Integer taxPrice = (int) (totalPrice * 0.1);
            Integer includingTaxTotalPrice = totalPrice + taxPrice;
            model.addAttribute("orderItemList", orderItemList);
            model.addAttribute("taxPrice", taxPrice);
            model.addAttribute("includingTaxTotalPrice", includingTaxTotalPrice);
        }
        // ユーザーがログインしているかどうか
        userService.isLogin(model);
    }

    
    /** 
     * sessionに入ったuserIdとstatusに0をorderにsetするメソッド
     * @return Order
     */
    private Order setUserIdAndStatus0() {
        Order order = new Order();
        User user = (User) session.getAttribute("user");
        // 未ログインユーザーだった場合
        if (Objects.isNull(user)) {
            Random random = new Random();
            Integer temporaryUserId = random.nextInt(10000000);
            user = new User();
            user.setId(temporaryUserId);
            session.setAttribute("user", user);
        }
        order.setUserId(user.getId());
        order.setStatus(0);
        return order;
    }

    
    /** 
     * orderConfirmFormで受け取った日時をtimestamp型に変換するメソッド
     * @param form
     * @return Timestamp
     */
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