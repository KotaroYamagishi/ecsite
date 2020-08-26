package com.ecsite.service;

import java.util.List;
import java.util.Objects;

import com.ecsite.domain.Order;
import com.ecsite.domain.OrderItem;
import com.ecsite.domain.OrderTopping;
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
    private OrderItemRepository orderItemRepository;

    @Autowired
    private OrderToppingRepository orderToppingRepository;

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
                    orderItemRepository.quantityUpdated(orderItem);
                }
                
            }
        }
    }

    public Order findOrdersAndOrderItemAndOrderTopping(Order order) {
        return ordersRepository.findOrdersAndOrderItemAndOrderTopping(order);
    }

    public void applyOrder(Order order,Integer status){
        ordersRepository.applyOrder(order,status);
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