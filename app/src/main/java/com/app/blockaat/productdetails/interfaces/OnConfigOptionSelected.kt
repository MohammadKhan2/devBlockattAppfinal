package com.app.blockaat.productdetails.interfaces

import com.app.blockaat.productdetails.model.ProductDetailAttributeModel

interface OnConfigOptionSelected {

    fun onConfigOptionChange(productAttr: ProductDetailAttributeModel,position: Int)
}