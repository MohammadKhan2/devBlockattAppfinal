package com.app.blockaat.tv.interfaces

import com.app.blockaat.productlisting.model.ProductListingFilterValueModel

interface OnFilterValueSelected {
    fun OnFilterValueSelected(filterValue: ProductListingFilterValueModel)
}