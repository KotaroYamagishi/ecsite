package com.ecsite.repository;

import com.ecsite.domain.Order;

import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface OrdersRepository {
    // 初回insert時のみorderテーブルに使うメソッド
    // statusは0をset
    Integer insert(Order order);

//  カートに商品を追加した時にtotalpriceをupdateするメソッド 
    void totalPriceUpdate(Order order);

    // useridをもとにユーザーがそのアカウントで既にshoppingcartに商品を入れているかを確認するメソッド 
    Order findOrdersByuserId(Order order);
}