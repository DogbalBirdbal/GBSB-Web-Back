package com.dogbalbirdbal.database.vo;

import com.dogbalbirdbal.database.vo.PlaceInfo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 하나의 Wish Box 는 여러개의 @{@link com.dogbalbirdbal.database.vo.WishList } 를 가지고 있다
 */
public class WishBox implements Serializable {


    List<WishList> wishLists = new ArrayList<>();

    public void addWishList(WishList wishList) {

        this.wishLists.add(wishList);

    }

    public List<WishList> getWishLists() {
        return wishLists;
    }

    public void setWishLists(List<WishList> wishLists) {
        this.wishLists = wishLists;
    }
}
