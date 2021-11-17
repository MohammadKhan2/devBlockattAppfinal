package com.app.blockaat.expadablerecylerview.model
import com.app.blockaat.category.model.Subcategory

class MainCatModel(val id : Int,val name: String, val icon: String,var image : String, val has_subcategory : String,val subcategories: ArrayList<Subcategory>) :
    com.app.blockaat.expadablerecylerview.model.Parent<Subcategory> {


    override fun isInitiallyExpanded(): Boolean {
        return false
    }

    override fun getChildList(): MutableList<Subcategory> {
        return subcategories
    }


}
