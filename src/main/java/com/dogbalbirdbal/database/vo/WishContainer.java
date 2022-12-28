package com.dogbalbirdbal.database.vo;

import java.util.ArrayList;
import java.util.List;

public class WishContainer {


    List<WishBox> wishBoxContainer = new ArrayList<>();


    public void addWishBox(WishBox wishBox ) {
        this.wishBoxContainer.add(wishBox);
    }

    public List<WishBox> getWishBoxContainer() {
        return wishBoxContainer;
    }

    public void setWishBoxContainer(List<WishBox> wishBoxList) {
        this.wishBoxContainer = wishBoxList;
    }
}
