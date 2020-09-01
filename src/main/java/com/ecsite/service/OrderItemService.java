package com.ecsite.service;

import com.ecsite.domain.OrderItem;
import com.ecsite.repository.OrderItemRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class OrderItemService {

    @Autowired
    private OrderItemRepository orderItemRepository;
    
    
    /** 
     * orderItemの情報をupdateするメソッド
     * 
     * @param orderItem
     */
    public void OrderIdUpdate(OrderItem orderItem){
        orderItemRepository.orderIdUpdate(orderItem);
    }

    
    /** 
     * 条件位合ったorderItemを取得
     * 
     * @param orderItem
     * @return OrderItem
     */
    public OrderItem findByItemIdAndOrderIdAndSize(OrderItem orderItem){
        return orderItemRepository.findByItemIdAndOrderIdAndSize(orderItem);
    }

    
    /** 
     * orderItemの数量を更新
     * 
     * @param orderItem
     */
    public void quantityUpdate(OrderItem orderItem){
        orderItemRepository.quantityUpdate(orderItem);
    }
}