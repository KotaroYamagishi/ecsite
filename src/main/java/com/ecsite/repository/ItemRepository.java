package com.ecsite.repository;

import java.util.Collection;
import java.util.List;

import com.ecsite.domain.Item;
import com.ecsite.form.ItemSearchForm;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.boot.autoconfigure.data.web.SpringDataWebProperties.Pageable;
import org.springframework.stereotype.Repository;

// repositoryの代わり
@Mapper
@Repository
public interface ItemRepository{

    Collection<Item> findAll(
        @Param("itemSearchForm")ItemSearchForm itemSearchForm
        ,@Param("pageable")Pageable pageable);

    @Select("select * from items where id=#{id}")
    Item findById(Integer itemId);

    @Select("select * from items where name LIKE %:searchName%")
    List<Item> findBySearchName(@Param("searchName")String searchName);
}