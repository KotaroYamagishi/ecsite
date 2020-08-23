package com.ecsite.repository;

import java.util.Collection;

import com.ecsite.domain.OrderItem;

import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface OrderItemRepository {
    // insert時に同一商品があるかチェックするため
    Collection<OrderItem> findByItemIdAndOrderIdAndSize(OrderItem orderItem);
    // 1回目のinsert
    void firstInsert(OrderItem orderItem);
    // 同一商品であった場合、商品のquantityのみ変更
    void quantityUpdated(OrderItem orderItem);

    Collection<OrderItem> findByOrderId(Integer orderId);
}