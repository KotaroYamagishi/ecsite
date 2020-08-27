package com.ecsite.form;

import javax.validation.constraints.NotBlank;

import lombok.Data;

@Data
public class InsertUserForm {
    @NotBlank(message = "名前を入力してください")
    private String name;
    @NotBlank(message = "メールアドレスを入力してください")
    private String email;
    @NotBlank(message = "パスワードを入力してください")
    private String password;
    @NotBlank(message = "郵便番号を入力してください")
    private String zipcode;
    @NotBlank(message = "住所を入力してください")
    private String address;
    @NotBlank(message = "電話番号を入力してください")
    private String telephone;
    @NotBlank(message = "確認用パスワードを入力してください")
    private String checkpassword;
}