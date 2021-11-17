package com.app.blockaat.productlisting.model

import com.app.blockaat.navigation.NavigationActivity

data class FilterObject (
    val arrListFilterData: ArrayList<ProductListingFilterModel?>?,
    val adapterFilter: NavigationActivity.FilterPagerAdapter?,
    val type: String,
    val parentType: String
)