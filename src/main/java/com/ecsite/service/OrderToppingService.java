package com.ecsite.service;

import java.util.List;

import com.ecsite.domain.OrderTopping;
import com.ecsite.repository.OrderToppingRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class OrderToppingService {
    
    @Autowired
    private OrderToppingRepository orderToppingRepository;

    
    /** 
     * orderItemidでorderToppingListを取得
     * 
     * @param itemId
     * @return List<OrderTopping>
     */
    public List<OrderTopping> findByOrderItemId(Integer itemId){
        return (List<OrderTopping>) orderToppingRepository.findByOrderItemId(itemId);
    }

    
    /** 
     * orderItemIdとToppingIdをもとにorderToppingを一つ取得
     * 
     * @param orderTopping
     * @return OrderTopping
     */
    public OrderTopping findByOrderItemIdAndToppingId(OrderTopping orderTopping){
        return orderToppingRepository.findByOrderItemIdAndToppingId(orderTopping);
    }
}