package com.app.blockaat.changestores.interfaces

import com.app.blockaat.changestores.model.StoreDataModel

interface UpdateStoreItemEvent {
    fun onStoreUpdateClicked(
        position: Int,
        type: String,
        get: StoreDataModel?
    )
}