package com.ecsite.repository;

import java.util.Collection;

import com.ecsite.domain.OrderTopping;

import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface OrderToppingRepository {
    // order_item_idをもとにordertoppingテーブルの情報を取得（list）
    // 2回目以降のinsertで同じ商品があるかどうか確かめる時など
    Collection<OrderTopping> findByOrderItemId(Integer orderItemId);

    OrderTopping findByOrderItemIdAndToppingId(OrderTopping orderTopping);

    void insert(OrderTopping orderTopping);

    void delete(Integer orderItemId);
}