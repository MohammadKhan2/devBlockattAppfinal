package com.app.blockaat.home.interfaces

interface HomeItemClickInterface {
    fun onClickItem(position: Int, type: String, link_type: String, link_id: String, header: String)
}