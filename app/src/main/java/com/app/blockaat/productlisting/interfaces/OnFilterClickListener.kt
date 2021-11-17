package com.app.blockaat.productlisting.interfaces

import com.app.blockaat.navigation.NavigationActivity
import com.app.blockaat.productlisting.model.ProductListingFilterModel

interface OnFilterClickListener {

    fun onFilterClicked(arrListFilterData: ArrayList<ProductListingFilterModel?>?,adapterFilter: NavigationActivity.FilterPagerAdapter?,type: String)
}