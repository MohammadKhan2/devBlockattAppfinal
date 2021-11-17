package com.app.blockaat.search.adapter

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.app.blockaat.R
import com.app.blockaat.helper.DBHelper
import com.app.blockaat.helper.Global
import com.app.blockaat.productdetails.ProductDetailsActivity
import com.app.blockaat.productlisting.ProductListActivity
import com.app.blockaat.search.SearchActivity
import com.app.blockaat.search.model.SearchDataModel

import kotlinx.android.synthetic.main.lv_item_search.view.*

/**
 * Created by pinal-leza on 2/27/2018.
 */

class SearchAdapter(private val searchData: List<SearchDataModel>, private val searchDBHelper: DBHelper, private val isHeader: Boolean, private val fontRegular: Typeface, private val fontLight: Typeface, private val fontMedium: Typeface, private val fontSemiBold: Typeface) : RecyclerView.Adapter<SearchAdapter.ViewHolder>() {

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(searchData, position, searchDBHelper, isHeader, fontRegular, fontLight, fontMedium, fontSemiBold)
    }


    override fun getItemCount(): Int = searchData.count()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.lv_item_search, parent, false)
        return ViewHolder(view, parent.context)
    }

    class ViewHolder(view: View, val activity: Context) : RecyclerView.ViewHolder(view) {
        fun bind(searchData: List<SearchDataModel>, position: Int, searchDBHelper: DBHelper, headerRequired: Boolean, fontRegular: Typeface, fontLight: Typeface, fontMedium: Typeface, fontSemiBold: Typeface) {

            if (headerRequired && position == 0) {
                itemView.relClearRecentSearch.visibility = View.VISIBLE
            } else {
                itemView.relClearRecentSearch.visibility = View.GONE
            }

            //set data
            itemView.txtTitle.text = searchData[position].name
            itemView.txtProductCount.text = searchData[position].product_total

            if (!Global.isEnglishLanguage(activity as Activity)) {
                itemView.img_next.rotation = 90f
            }
            
            //set fonts
            itemView.txtTitle.typeface = fontRegular
            itemView.txtProductCount.typeface = fontLight
            itemView.txtClearRecent.typeface = fontSemiBold
            itemView.txtClear.typeface = fontSemiBold

            //click listener
            itemView.txtClear.setOnClickListener {
                //this click will go in HomeActivity and from there we will hide list view
                itemView.relClearRecentSearch.visibility = View.GONE
                (activity as SearchActivity).clearRecentSearches()

            }



            itemView.relMainHolder.setOnClickListener {
               println("Search data : "+searchData[position])
                if (searchData[position].type.equals("P")) {
                    val i = Intent(activity, ProductDetailsActivity::class.java)
                    i.putExtra("strProductID", searchData[position].id)
                    i.putExtra("strProductName", searchData[position].name)
                    activity.startActivity(i)
                } else if (searchData[position].type.equals("BR")) {

                    val i = Intent(activity, ProductListActivity::class.java)
                    i.putExtra("brandID", searchData[position].id)
                    i.putExtra("header_text",searchData[position].name)
                    i.putExtra("categoryID", Global.getPreferenceCategory(activity as SearchActivity))
                    i.putExtra("isFrom","brand")
                    i.putExtra("has_subcategory","yes")
                    activity.startActivity(i)
                } else if (searchData[position].type.equals("B")) {

                    val i = Intent(activity, ProductListActivity::class.java)
                    i.putExtra("brandID", searchData[position].id)
                    i.putExtra("header_text",searchData[position].name)
                    i.putExtra("categoryID", Global.getPreferenceCategory(activity as SearchActivity))
                    i.putExtra("isFrom","brand")
                    i.putExtra("has_subcategory","yes")
                    activity.startActivity(i)
                } else if (searchData[position].type.equals("C")) {

                    val i = Intent(activity, ProductListActivity::class.java)
                    i.putExtra("categoryID", searchData[position].id)
                    i.putExtra("isFrom", "categories")
                    i.putExtra("has_subcategory", "yes")
                    i.putExtra("header_text",searchData[position].name)
                    activity.startActivity(i)
                }

                else {

                    val i = Intent(activity, ProductListActivity::class.java)
                    i.putExtra("categoryID", searchData[position].id)
                    i.putExtra("isFrom", "categories")
                    i.putExtra("has_subcategory", "yes")
                    i.putExtra("product_name", searchData[position].name)
                    activity.startActivity(i)

                }



                if (searchDBHelper.isPresentInRecentSearch(searchData[position].id!!)) {
                    searchDBHelper.deleteFromRecent(searchData[position].id!!)
                    searchDBHelper.addToRecentSearch(searchData[position])

                } else {
                    if (searchDBHelper.getAllRecentSearch().size == 10) {
                        searchDBHelper.deleteFirstRow()
                        searchDBHelper.addToRecentSearch(searchData[position])
                    } else {
                        searchDBHelper.addToRecentSearch(searchData[position])
                    }

                }
            }


        }
    }
}
