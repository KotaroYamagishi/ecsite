package com.ecsite.service;

import java.util.List;

import com.ecsite.domain.Item;
import com.ecsite.repository.ItemRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ItemService {
    
    @Autowired
    private ItemRepository itemRepository;

    public List<Item> findAll() {
        return itemRepository.findAll();
    }

    public Item findById(Integer itemId){
        return itemRepository.findById(itemId);
    }

    public List<Item> findBySearchName(String searchName){
        return itemRepository.findBySearchName(searchName);
    }
}