package com.ecsite.repository;

import com.ecsite.domain.User;

import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserRepository {
    // emailとpasswordをもとにログインユーザーがいるかどうか
    User findByEmailAndPassWord(User user);
    // insert時に同一メールアドレスがないかチェック
    User findByEmail(String email);
    
    void insert(User user);
}