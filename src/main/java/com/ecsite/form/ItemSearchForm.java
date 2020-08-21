package com.ecsite.form;

import lombok.Data;

@Data
public class ItemSearchForm {
    private String searchName;
    private String sort;
    private int currentPageNum;
}