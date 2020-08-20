package com.ecsite.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("")
public class LoginController {
    
    @RequestMapping("/toLogin")
    public String toLogin(){
        return "user/login";
    }
}