package com.app.blockaat.navigation.model

import com.app.blockaat.changestores.model.StoreDataModel

data class Navigationmodel(
    val parent_id: Int? = 0,
    val name: String? = null,
    val thumbnail :Int? = 0,
    val image:String? = null,
    val arrayList: ArrayList<StoreDataModel>?=null,
    val isArrowVisible: Boolean
)