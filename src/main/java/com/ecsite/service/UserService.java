package com.ecsite.service;

import java.util.Objects;

import javax.servlet.http.HttpSession;

import com.ecsite.domain.User;
import com.ecsite.repository.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;

@Service
@Transactional
public class UserService {
    
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Autowired
    private HttpSession session;

    // 新規会員登録
    public void create(User user){
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.insert(user);
    }
    // 同一メールアドレスがないかチェック
    public User findByEmail(String email){
        return userRepository.findByEmail(email);
    }

    public User findByEmailAndPassWord(User user){
        return userRepository.findByEmailAndPassWord(user);
    }

    public void isLogin(Model model){
        User user= (User) session.getAttribute("user");
        if(Objects.isNull(user)||Objects.nonNull(user.getName())){
            model.addAttribute("user",user);
        }
    }

}