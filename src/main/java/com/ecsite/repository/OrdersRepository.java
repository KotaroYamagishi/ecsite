package com.ecsite.repository;


import java.util.List;

import com.ecsite.domain.Order;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface OrdersRepository {
    // 初回insert時のみorderテーブルに使うメソッド
    // statusは0をset
    void insert(Order order);

//  カートに商品を追加した時にtotalpriceをupdateするメソッド 
    void totalPriceUpdate(Order order);

    // 注文する時のメソッド(注文時)
    void applyOrder(Order order,Integer status);

    // ゲストユーザーからログインユーザーに変更する時
    void updateUserId(Order order);

    // useridをもとにユーザーがそのアカウントで既にshoppingcartに商品を入れているかを確認するメソッド 
    Order findOrdersByuserId(Order order);

    // orderオブジェクトのuser_idとstatusをもとにorders,order-items,order-toppingsを取得
    List<Order> findOrdersAndOrderItemAndOrderTopping(Order order);

    Order findByIdAndStatus(
        @Param("id")Integer id
        ,@Param("status")Integer status);

    void delete(Integer id);
}