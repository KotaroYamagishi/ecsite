package com.ecsite.service;

import java.util.List;

import com.ecsite.domain.Order;
import com.ecsite.repository.OrdersRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class OrdersService {
    
    @Autowired
    private OrdersRepository ordersRepository;

    
    /** 
     * userIdとstatusを元に商品情報を全て取得
     * 引数はuserIdとstatusにした方がわかりやすかったかも
     * 
     * @param order
     * @return List<Order>
     */
    public List<Order> findOrdersAndOrderItemAndOrderTopping(Order order) {
        return ordersRepository.findOrdersAndOrderItemAndOrderTopping(order);
    }

    
    /** 
     * 注文に関する情報をupdateする
     *
     * @param order
     * @param status
     */
    public void applyOrder(Order order,Integer status){
        ordersRepository.applyOrder(order,status);
    }

    
    /** 
     * ordersテーブルのuseridをupdateする
     * 
     * @param order
     */
    public void updateUserId(Order order){
        ordersRepository.updateUserId(order);
    }

    
    /** 
     * ordersテーブルのtotalPriceをupdate
     * 
     * @param order
     */
    public void totalPriceUpdate(Order order){
        ordersRepository.totalPriceUpdate(order);
    }
}