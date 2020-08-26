package com.ecsite.service;

import java.util.List;

import com.ecsite.domain.Order;
import com.ecsite.repository.OrdersRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ShoppingHistoryService {
    
    @Autowired
    private OrdersRepository ordersRepository;

}