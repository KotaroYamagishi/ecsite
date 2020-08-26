package com.ecsite.repository;


import com.ecsite.domain.Order;

import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface OrdersRepository {
    // 初回insert時のみorderテーブルに使うメソッド
    // statusは0をset
    void insert(Order order);

//  カートに商品を追加した時にtotalpriceをupdateするメソッド 
    void totalPriceUpdate(Order order);

    // 注文する時のメソッド
    void applyOrder(Order order,Integer status);

    // useridをもとにユーザーがそのアカウントで既にshoppingcartに商品を入れているかを確認するメソッド 
    Order findOrdersByuserId(Order order);

    // orderオブジェクトのuser_idとstatusをもとにorders,order-items,order-toppingを取得
    Order findOrdersAndOrderItemAndOrderTopping(Order order);
}