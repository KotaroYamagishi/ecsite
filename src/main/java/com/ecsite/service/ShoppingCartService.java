package com.ecsite.service;

import java.util.List;
import java.util.Objects;

import com.ecsite.domain.Item;
import com.ecsite.domain.Order;
import com.ecsite.domain.OrderItem;
import com.ecsite.domain.OrderTopping;
import com.ecsite.domain.Topping;
import com.ecsite.repository.OrderItemRepository;
import com.ecsite.repository.OrderToppingRepository;
import com.ecsite.repository.OrdersRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ShoppingCartService {
    
    @Autowired
    private OrdersRepository ordersRepository;
    @Autowired
    private ItemService itemService;
    @Autowired
    private ToppingService toppingService;
    @Autowired
    private OrderItemRepository orderItemRepository;

    @Autowired
    private OrderToppingRepository orderToppingRepository;


    public List<Order> findOrdersAndOrderItemAndOrderTopping(Order order) {
        return ordersRepository.findOrdersAndOrderItemAndOrderTopping(order);
    }

    public void applyOrder(Order order,Integer status){
        ordersRepository.applyOrder(order,status);
    }

    public void updateUserId(Order order){
        ordersRepository.updateUserId(order);
    }

    public void OrderIdUpdate(OrderItem orderItem){
        orderItemRepository.orderIdUpdate(orderItem);
    }

    public void totalPriceUpdate(Order order){
        ordersRepository.totalPriceUpdate(order);
    }

    public OrderItem findByItemIdAndOrderIdAndSize(OrderItem orderItem){
        return orderItemRepository.findByItemIdAndOrderIdAndSize(orderItem);
    }

    public List<OrderTopping> findByOrderItemId(Integer itemId){
        return (List<OrderTopping>) orderToppingRepository.findByOrderItemId(itemId);
    }

    public void quantityUpdate(OrderItem orderItem){
        orderItemRepository.quantityUpdate(orderItem);
    }

    public OrderTopping findByOrderItemIdAndToppingId(OrderTopping orderTopping){
        return orderToppingRepository.findByOrderItemIdAndToppingId(orderTopping);
    }

    // orderにはuserId,status,totalPrice
    // orderItemにはitemId,quantity,size,初回insert時はorderのinsert時に取得する自動採番の値をorderIdにsetする
    public void addShoppingCart(Order order,OrderItem orderItem){
        // useridとstatus=0は詰めた状態で
        Order findOrder=ordersRepository.findOrdersByuserId(order);
        if(Objects.isNull(findOrder)){
            // 初回のinsert
            ordersRepository.insert(order);
            orderItem.setOrderId(order.getId());
            orderItemAndOrderItemToppingFirstInsert(orderItem);
        }else{
            // 2回目以降のinsert
            Integer beforeTotalPrice=findOrder.getTotalPrice();
            findOrder.setTotalPrice(beforeTotalPrice +order.getTotalPrice());
            // ordersテーブルのtotalPriceを更新
            ordersRepository.totalPriceUpdate(findOrder);
            // orderItemテーブルに同一な商品があるかどうか
            Integer orderId=findOrder.getId();
            orderItem.setOrderId(orderId);
            OrderItem findOrderItem= (OrderItem) orderItemRepository.findByItemIdAndOrderIdAndSize(orderItem);
            if(Objects.isNull(findOrderItem)){
                orderItemAndOrderItemToppingFirstInsert(orderItem);
            }else{
                List<OrderTopping> orderToppingList= (List<OrderTopping>) orderToppingRepository
                        .findByOrderItemId(findOrderItem.getId());
                // 1回目のinsertとして処理
                if(Objects.isNull(orderToppingList)){
                    orderItemAndOrderItemToppingFirstInsert(orderItem);
                }else{
                    // orderItem2回目のquantityUpdateとして処理
                    Integer beforeQuantity=orderItem.getQuantity();
                    orderItem.setQuantity(beforeQuantity+findOrderItem.getQuantity());
                    orderItemRepository.quantityUpdate(orderItem);
                }
                
            }
        }
    }

    public void deleteOrders(Integer orderId){
        ordersRepository.delete(orderId);
    }

    public void deleteOrderItemsAndOrdertoppings(Integer orderItemId){
        OrderItem orderItem=orderItemRepository.findById(orderItemId);
        List<OrderTopping> orderToppingList = (List<OrderTopping>) orderToppingRepository
                .findByOrderItemId(orderItemId);
        orderItem.setOrderToppingList(orderToppingList);
        Order order=ordersRepository.findByIdAndStatus(orderItem.getOrderId(),0);
        orderItemSetItemAndTopping(orderItem);
        Integer beforeTotalPrice=order.getTotalPrice();
        order.setTotalPrice(beforeTotalPrice-orderItem.getTotalPrice());
        ordersRepository.totalPriceUpdate(order);
        orderToppingRepository.delete(orderItemId);
        orderItemRepository.delete(orderItemId);
    }

    // orderItemクラスの中にあるItemプロパティと、その中にあるorderToppingListのToppingプロパティを詰める
    public void orderItemSetItemAndTopping(OrderItem orderItem){
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
    }


    private void orderItemAndOrderItemToppingFirstInsert(OrderItem orderItem){
        orderItemRepository.firstInsert(orderItem);
        Integer orderItemId=orderItem.getId();
            if((Objects.nonNull(orderItem.getOrderToppingList()))){
                List<OrderTopping> orderToppingList=orderItem.getOrderToppingList();
                orderToppingList.forEach(orderTopping ->{
                    orderTopping.setOrderItemId(orderItemId);
                    orderToppingRepository.insert(orderTopping);
                });
            }
    }
}