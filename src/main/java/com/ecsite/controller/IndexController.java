package com.ecsite.controller;

import java.util.List;
import java.util.Objects;

import com.ecsite.domain.Item;
import com.ecsite.form.ItemSearchForm;
import com.ecsite.service.ItemService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.data.web.SpringDataWebProperties.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/item-list")
public class IndexController {
    
    @Autowired
    private ItemService itemService;

    @ModelAttribute
    public ItemSearchForm setUpItemSearchForm(){
        return new ItemSearchForm();
    }

    @RequestMapping("")
    public String index(Model model){
        ItemSearchForm form = new ItemSearchForm();
        form.setSearchName("");
        form.setSort("asc");
        form.setCurrentPageNum(1);
        return movePageInList(model,form);
    }

    @RequestMapping("/search")
    public String itemSearch(ItemSearchForm form,Model model){
        form.setCurrentPageNum(1);
        return movePageInList(model,form);
    }

    @RequestMapping("/sort-cheap")
    public String itemSortCheap(ItemSearchForm form, Model model,String searchName,String currentPageNum){
        setSearchForm(form, searchName, "asc",currentPageNum);
        return movePageInList(model, form);
    }

    @RequestMapping("/sort-expensive")
    public String itemSortExpensive(ItemSearchForm form, Model model,String searchName,String currentPageNum){
        setSearchForm(form, searchName, "desc",currentPageNum);
        return movePageInList(model, form);
    }

    @RequestMapping("/firstPage")
    public String firstPage(ItemSearchForm form, Model model,String searchName,String sort,String currentPageNum){
        setSearchForm(form, searchName, sort,currentPageNum);
        //現在ページ数を先頭ページに設定する
        form.setCurrentPageNum(1);
        return movePageInList(model, form);
    }
 
    /**
     * 一覧画面で「前へ」リンク押下時に次ページを表示する
     * @param searchForm 検索用Formオブジェクト
     * @param model Modelオブジェクト
     * @return 一覧画面へのパス
     */
    @RequestMapping("/backPage")
    public String backPage(ItemSearchForm form, Model model,String searchName,String sort,String currentPageNum){
        setSearchForm(form, searchName, sort,currentPageNum);
        //現在ページ数を前ページに設定する
        form.setCurrentPageNum(form.getCurrentPageNum() - 1);
        return movePageInList(model, form);
    }
 
    /**
     * 一覧画面で「次へ」リンク押下時に次ページを表示する
     * @param searchForm 検索用Formオブジェクト
     * @param model Modelオブジェクト
     * @return 一覧画面へのパス
     */
    @RequestMapping("/nextPage")
    public String nextPage(ItemSearchForm form, Model model,String searchName,String sort,String currentPageNum){
        setSearchForm(form, searchName, sort,currentPageNum);
        //現在ページ数を次ページに設定する
        form.setCurrentPageNum(form.getCurrentPageNum() + 1);
        return movePageInList(model, form);
    }
 
    /**
     * 一覧画面で「最後へ」リンク押下時に次ページを表示する
     * @param searchForm 検索用Formオブジェクト
     * @param model Modelオブジェクト
     * @return 一覧画面へのパス
     */
    @RequestMapping("/lastPage")
    public String lastPage(ItemSearchForm form, Model model,String searchName,String sort,String currentPageNum){
        setSearchForm(form, searchName, sort,currentPageNum);
        //現在ページ数を最終ページに設定する
        form.setCurrentPageNum(itemService.getAllPageNum(form));
        return movePageInList(model, form);
    }

    private String movePageInList(Model model, ItemSearchForm searchForm){
        //現在ページ数, 総ページ数を設定
        model.addAttribute("searchName", searchForm.getSearchName());
        model.addAttribute("sort",searchForm.getSort());
        model.addAttribute("currentPageNum", searchForm.getCurrentPageNum());
        model.addAttribute("allPageNum", itemService.getAllPageNum(searchForm));
        //ページング用オブジェクトを生成し、現在ページのユーザーデータリストを取得
        Pageable pageable = itemService.getPageable(searchForm.getCurrentPageNum());
        List<Item> itemList = itemService.findAll(searchForm, pageable);
        //ユーザーデータリストを更新
        model.addAttribute("itemList", itemList);
        return "item/item-list";
    }


    
    /** 
     * パラメータで受け取った情報をItemSearchFormに詰めるメソッド
     * 
     * @param form
     * @param searchName
     * @param currentPageNum
     */
    private void setSearchForm(ItemSearchForm form,String searchName,String sort,String currentPageNum){
        if(Objects.isNull(searchName)){
            form.setSearchName("");
        }else{
            form.setSearchName(searchName);
        }
        if(Objects.isNull(sort)){
            form.setSort("asc");
        }else{
            form.setSort(sort);
        }
        form.setCurrentPageNum(Integer.parseInt(currentPageNum));
    }
}