package com.ecsite.repository;

import java.util.List;

import com.ecsite.domain.Topping;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface ToppingRepository {

    @Select("select * from toppings order by id")
    List<Topping> findAll();

    @Select("select * from toppings where id=#{id}")
    Topping findById(int id);
}