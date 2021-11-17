package com.app.blockaat.wishlist.interfaces;

import com.app.blockaat.wishlist.modelclass.WishListDataModel;

import java.util.ArrayList;

public interface UpdateWishlistItemEvent {
    void onWishlistUpdateClicked(ArrayList<WishListDataModel> data, String type);
}
