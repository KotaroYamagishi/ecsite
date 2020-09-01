package com.ecsite.service;

import java.util.List;

import com.ecsite.domain.Topping;
import com.ecsite.repository.ToppingRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ToppingService {
    
    @Autowired
    private ToppingRepository toppingRepository;

    
    /** 
     * toppingの情報を全て取得
     * @return List<Topping>
     */
    public List<Topping> findAll(){
        return toppingRepository.findAll();
    }

    
    /** 
     * toppingのidをもとにtoppingの情報を取得
     * @param id
     * @return Topping
     */
    public Topping findById(Integer id){
        return toppingRepository.findById(id);
    }
}