package com.ecsite.controller;

import java.util.Objects;

import javax.servlet.http.HttpSession;

import com.ecsite.domain.User;
import com.ecsite.form.LoginForm;
import com.ecsite.service.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("")
public class LoginController {

    @Autowired
    private UserService userService;

    @Autowired
    private HttpSession session;

    @ModelAttribute
    public LoginForm setUpLoginForm(){
        return new LoginForm();
    }
    
    @RequestMapping("/toLogin")
    public String toLogin(){
        return "user/login";
    }

    @RequestMapping("/login/success")
    public String login(LoginForm form, Model model){
        User user=new User();
        user.setEmail(form.getEmail());
        user.setPassword(form.getPassword());
        User loginUser=userService.findByEmailAndPassWord(user);
        if(Objects.isNull(loginUser)){
            model.addAttribute("errorMessage","ユーザーが存在しません");
            return "user/login";
        }
        session.setAttribute("user", loginUser);
        return "forward:/item-list";
    }
}