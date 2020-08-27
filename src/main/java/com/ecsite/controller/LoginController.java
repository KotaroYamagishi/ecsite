package com.ecsite.controller;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.servlet.http.HttpSession;

import com.ecsite.domain.Order;
import com.ecsite.domain.OrderItem;
import com.ecsite.domain.OrderTopping;
import com.ecsite.domain.User;
import com.ecsite.form.LoginForm;
import com.ecsite.security.LoginUserDetails;
import com.ecsite.service.ShoppingCartService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("")
public class LoginController {

    @Autowired
    private ShoppingCartService shoppingCartService;

    @Autowired
    private HttpSession session;

    @ModelAttribute
    public LoginForm setUpLoginForm() {
        return new LoginForm();
    }

    @RequestMapping("/toLogin")
    public String toLogin() {
        return "user/login";
    }

    // もし、guestユーザーが存在しなければ
    // sessionにログインしたアカウントをそのままset
    // もし、guestユーザーが存在すれば（itemをカートに入れる瞬間）orderItemが一つは入っている
    // sessionに入ってあるguestのuserIdとstatus=0でordersを探し、
    // 未ログインの状態でカートに何か入れていた場合、ログイン情報のアカウントとすり合わせしないといけない
    // userDetailsのuserIdにsetし直し、update
    // 最終的には、gusetユーザーのordersテーブルのuserIdのみ更新
    // userDetailsを"user"でsessionにsetし直すことで、"user"を正しいuserに更新する
    @RequestMapping("/login/success")
    public String login(@AuthenticationPrincipal LoginUserDetails userDetails, Model model) {
        User loginUser = userDetails.getUser();
        User guestUser = (User) session.getAttribute("user");
        if (Objects.isNull(guestUser)) {
            session.setAttribute("user", loginUser);
        } else {
            Order go = new Order();
            go.setUserId(guestUser.getId());
            go.setStatus(0);
            Order guestUserOrder = findOrder(go);
            Order lo = new Order();
            lo.setUserId(loginUser.getId());
            lo.setStatus(0);
            Order loginUserOrder = findOrder(lo);
            // 未ログインユーザーがアイテムを入れている＆＆loginユーザーがまだカートにitemを入れていない
            if (Objects.isNull(loginUserOrder)) {
                guestUserOrder.setUserId(loginUser.getId());
                shoppingCartService.updateUserId(guestUserOrder);
            } else {
                List<OrderItem> loginOrderItemList = loginUserOrder.getOrderItemList();
                // ここのif文いらんかも
                if (Objects.isNull(loginOrderItemList)) {
                    guestUserOrder.setUserId(loginUser.getId());
                    shoppingCartService.updateUserId(guestUserOrder);
                } else {
                    List<OrderItem> guestOrderItemList = guestUserOrder.getOrderItemList();
                    guestOrderItemList.forEach(guestOrderItem -> {
                        guestOrderItem.setOrderId(loginUserOrder.getId());
                        OrderItem findOrderItem = shoppingCartService.findByItemIdAndOrderIdAndSize(guestOrderItem);
                        if (Objects.isNull(findOrderItem)) {
                            shoppingCartService.OrderIdUpdate(guestOrderItem);
                        } else {
                            List<OrderTopping> orderToppingList = shoppingCartService
                                    .findByOrderItemId(findOrderItem.getId());
                            if (Objects.isNull(orderToppingList)) {
                                shoppingCartService.OrderIdUpdate(guestOrderItem);
                            } else {
                                List<OrderTopping> guestOrderToppingList = shoppingCartService
                                        .findByOrderItemId(guestOrderItem.getId());
                                List<Integer> guestToppingIdList = guestOrderToppingList.stream()
                                        .map(got -> got.getToppingId()).collect(Collectors.toList());
                                List<Integer> orderToppingIdList = orderToppingList.stream()
                                        .map(got -> got.getToppingId()).collect(Collectors.toList());
                                if (Objects.equals(guestToppingIdList, orderToppingIdList)) {
                                    Integer beforeQuantity = findOrderItem.getQuantity();
                                    findOrderItem.setQuantity(beforeQuantity + guestOrderItem.getQuantity());
                                    shoppingCartService.quantityUpdate(findOrderItem);
                                    shoppingCartService.deleteOrderItemsAndOrdertoppings(guestOrderItem.getId());
                                } else {
                                    shoppingCartService.OrderIdUpdate(guestOrderItem);
                                }
                            }
                        }
                    });
                    // totalpriceを足さな
                    Integer beforeTotalPrice = loginUserOrder.getTotalPrice();
                    loginUserOrder.setTotalPrice(beforeTotalPrice + guestUserOrder.getTotalPrice());
                    shoppingCartService.totalPriceUpdate(loginUserOrder);
                    // ordertableにloginユーザーと未ログインユーザの重複を避けるため
                    shoppingCartService.deleteOrders(guestUserOrder.getId());
                }
            }
            session.setAttribute("user", loginUser);
        }
        return "forward:/item-list";
    }

    @RequestMapping("/logout/success")
    public String logout() {
        session.removeAttribute("user");
        return "forward:/item-list";
    }

    private Order findOrder(Order order) {
        List<Order> findOrderList = shoppingCartService.findOrdersAndOrderItemAndOrderTopping(order);
        Order findOrder = new Order();
        if (findOrderList.size() == 0) {
            findOrder = null;
        } else {
            findOrder = findOrderList.get(0);
        }
        return findOrder;
    }
}