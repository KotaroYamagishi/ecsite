package com.ecsite.repository;

import java.util.Collection;

import com.ecsite.domain.OrderItem;

import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface OrderItemRepository {
    // insert時に同一商品があるかチェックするため
    OrderItem findByItemIdAndOrderIdAndSize(OrderItem orderItem);
    // 1回目のinsert
    void firstInsert(OrderItem orderItem);
    // 同一商品であった場合、商品のquantityのみ変更
    void quantityUpdate(OrderItem orderItem);

    void orderIdUpdate(OrderItem orderItem);

    Collection<OrderItem> findByOrderId(Integer orderId);

    OrderItem findById(Integer id);

    void delete(Integer orderId);
}