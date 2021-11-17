package com.app.blockaat.cart.interfaces;

import com.app.blockaat.cart.model.GetCartListDataModel;

public interface UpdateCartItemEvent {
    void onCartUpdateClicked(GetCartListDataModel data, String type);
}
