package com.app.blockaat.helper

import com.app.blockaat.wishlist.modelclass.WishListResponseModel

interface AddWishListInterface {
    fun onRemove(result: WishListResponseModel)
    fun onAdd(result: WishListResponseModel)
}