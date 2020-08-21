package com.ecsite.service;

import java.util.Collection;
import java.util.List;

import com.ecsite.domain.Item;
import com.ecsite.form.ItemSearchForm;
import com.ecsite.repository.ItemRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.data.web.SpringDataWebProperties.Pageable;
import org.springframework.boot.autoconfigure.data.web.SpringDataWebProperties.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ItemService {
    
    @Autowired
    private ItemRepository itemRepository;

    private String listPageSize="6";

    public List<Item> findAll(ItemSearchForm itemSearchForm,Pageable pageable) {
        List<Item> itemList= (List<Item>) itemRepository.findAll(itemSearchForm, pageable);
        return itemList;
    }

    public Item findById(Integer itemId){
        return itemRepository.findById(itemId);
    }

    public Pageable getPageable(int pageNumber){
        Pageable pageable = new Pageable() {
            public int getPageNumber() {
                //現在ページ数を返却
                return pageNumber;
            }
            public int getPageSize() {
                //1ページに表示する行数を返却
                //listPageSizeは、本プログラムの先頭に定義している
                return Integer.parseInt(listPageSize);
            }
            public int getOffset() {
                //表示開始位置を返却
                //例えば、1ページに2行表示する場合の、2ページ目の表示開始位置は
                //(2-1)*2+1=3 で計算される
                return ((pageNumber - 1) * Integer.parseInt(listPageSize) + 1);
            }
            public Sort getSort() {
                //ソートは使わないのでnullを返却
                return null;
            }
        };
        return pageable;
    }

    public int getAllPageNum(ItemSearchForm itemSearchForm) {
        //1ページに表示する行数を取得
        int listPageSizeNum = Integer.parseInt(listPageSize);
        //一覧画面に表示する全データを取得
        //第二引数のpageableにnullを設定することで、一覧画面に表示する全データが取得できる（つまり、ただのitemListが取得できる）
        List<Item> itemList = (List<Item>) itemRepository.findAll(itemSearchForm, null);
        //全ページ数を計算
        //例えば、1ページに2行表示する場合で、全データ件数が5の場合、
        //(5+2-1)/2=3 と計算される
        int allPageNum = (itemList.size() + listPageSizeNum - 1) / listPageSizeNum;
        // もし、allPageが0の時、1をreturnするという意味
        // 三項演算子
        return allPageNum == 0 ? 1 : allPageNum;
    }


}