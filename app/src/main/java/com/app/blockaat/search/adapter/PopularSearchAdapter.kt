package com.app.blockaat.search.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.app.blockaat.R
import com.app.blockaat.helper.Global
import com.app.blockaat.productdetails.ProductDetailsActivity
import com.app.blockaat.productlisting.ProductListActivity
import com.app.blockaat.search.SearchActivity

import com.app.blockaat.search.model.PopularSearchModel
import kotlinx.android.synthetic.main.lv_item_search.view.*

/**
 * Created by pinal-leza on 2/27/2018.
 */

class PopularSearchAdapter(private val searchData: ArrayList<PopularSearchModel>) : RecyclerView.Adapter<PopularSearchAdapter.ViewHolder>() {

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(searchData, position)
    }


    override fun getItemCount(): Int = searchData.count()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.lv_item_search, parent, false)
        return ViewHolder(view, parent.context)
    }

    class ViewHolder(view: View, val activity: Context) : RecyclerView.ViewHolder(view) {
        fun bind(searchData: List<PopularSearchModel>, position: Int) {


                itemView.relClearRecentSearch.visibility = View.GONE


            //set data
            itemView.txtTitle.text = searchData[position].name
            itemView.txtProductCount.text = searchData[position].product_total

            //set fonts
            itemView.txtTitle.typeface = Global.fontRegular
            itemView.txtProductCount.typeface = Global.fontLight
            itemView.txtClearRecent.typeface = Global.fontSemiBold
            itemView.txtClear.typeface = Global.fontSemiBold

            //click listener
            itemView.txtClear.setOnClickListener {
                //this click will go in HomeActivity and from there we will hide list view
                (activity as SearchActivity).clearRecentSearches()

            }


            itemView.relMainHolder.setOnClickListener {

                if (searchData[position].type.equals("P")){
                    val i = Intent(activity, ProductDetailsActivity::class.java)
                    i.putExtra("strProductID", searchData[position].type_id)
                    activity.startActivity(i)
                }else if (searchData[position].type.equals("BR")){
                    val i = Intent(activity, ProductListActivity::class.java)
                    i.putExtra("brandID", searchData[position].type_id)
                    i.putExtra("header_text",searchData[position].name)
                    i.putExtra("isFrom","brand")
                    i.putExtra("has_subcategory","yes")
                    i.putExtra("categoryID", Global.getPreferenceCategory(activity as SearchActivity))
                    activity.startActivity(i)
                }else if (searchData[position].type.equals("C")){
                    val i = Intent(activity, ProductListActivity::class.java)
                    i.putExtra("categoryID", searchData[position].type_id)
                    i.putExtra("header_text",searchData[position].name)
                    i.putExtra("isFrom", "categories")
                    i.putExtra("has_subcategory", "yes")
                    activity.startActivity(i)
                }else{

                    val i = Intent(activity, ProductListActivity::class.java)
                    i.putExtra("categoryID", searchData[position].type_id)
                    i.putExtra("header_text",searchData[position].name)
                    i.putExtra("isFrom", "categories")
                    i.putExtra("has_subcategory", "yes")
                    activity.startActivity(i)

                }

            }
        }
    }
}