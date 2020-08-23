package com.ecsite.service;

import com.ecsite.domain.User;
import com.ecsite.repository.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class UserService {
    
    @Autowired
    private UserRepository userRepository;

    // 新規会員登録
    public void create(User user){
        userRepository.insert(user);
    }
    // 同一メールアドレスがないかチェック
    public User findByEmail(String email){
        return userRepository.findByEmail(email);
    }

    public User findByEmailAndPassWord(User user){
        return userRepository.findByEmailAndPassWord(user);
    }


}