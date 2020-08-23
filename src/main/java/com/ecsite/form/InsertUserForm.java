package com.ecsite.form;

import lombok.Data;

@Data
public class InsertUserForm {
    
    private String name;
    private String email;
    private String password;
    private String zipcode;
    private String address;
    private String telephone;
    private String checkpassword;
}