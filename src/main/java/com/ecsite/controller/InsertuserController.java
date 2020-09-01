package com.ecsite.controller;

import java.util.Objects;

import com.ecsite.domain.User;
import com.ecsite.form.InsertUserForm;
import com.ecsite.service.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("toInsert")
public class InsertUserController {
    
    @Autowired
    private UserService userService;

    
    /** 
     * @return InsertUserForm
     */
    @ModelAttribute
    public InsertUserForm setUpItemSearchForm() {
        return new InsertUserForm();
    }

    

    
    /** 
     * 初期表示
     * @return String
     */
    @RequestMapping("")
    public String toInsert(){
        return "user/register_user";
    }

    
    /** 
     * 新規会員登録する
     * @param form
     * @param result
     * @param model
     * @return String
     */
    @RequestMapping("/user-insert")
    public String userInsert(@Validated InsertUserForm form,BindingResult result,Model model){
        if(result.hasErrors()){
            return "user/register_user";
        }
        User user=new User();
        user.setName(form.getName());
        user.setEmail(form.getEmail());
        user.setPassword(form.getPassword());
        user.setZipcode(form.getZipcode());
        user.setAddress(form.getAddress());
        user.setTelephone(form.getTelephone());

        User checkUser=userService.findByEmail(form.getEmail());
        if(Objects.nonNull(checkUser)){
            model.addAttribute("errorMessage", "既に登録済みのメールアドレスです");
            return "user/register_user";
        }
        userService.create(user);
        return "forward:/toLogin";
    }
}