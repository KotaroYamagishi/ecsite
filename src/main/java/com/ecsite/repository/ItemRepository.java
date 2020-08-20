package com.ecsite.repository;

import java.util.List;

import com.ecsite.domain.Item;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

// repositoryの代わり
@Mapper
public interface ItemRepository {
    @Select("select * from items order by id")
    List<Item> findAll();

    @Select("select * from items where id=#{id}")
    Item findById(Integer itemId);

    @Select("select * from items where name LIKE CONCAT('%', #{searchName}, '%')")
    List<Item> findBySearchName(String searchName);
}