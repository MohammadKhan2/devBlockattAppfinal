package com.app.blockaat.home.adapter

import android.app.Activity
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.app.blockaat.R
import com.app.blockaat.helper.Global
import com.app.blockaat.home.interfaces.GroupItemClickListener
import com.app.blockaat.home.interfaces.HomeItemClickInterface
import com.app.blockaat.home.model.CollectionGroup
import com.app.blockaat.home.model.CollectionList
import kotlinx.android.synthetic.main.item_home_collection.view.*

class GroupAdapter(
    private val arrListGroup: ArrayList<CollectionGroup>,
    private val activity: Activity,
    private val homeItemClickInterface: HomeItemClickInterface,
    private val groupItemClickInterface: GroupItemClickListener,
    private val categoryType: String
) : RecyclerView.Adapter<GroupAdapter.GroupViewHolder>() {

    private var deviceMultiplier = 0.0

    init {
        deviceMultiplier = Global.getDeviceWidth(activity) / 320.toDouble()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GroupViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_home_collection, parent, false)

        return GroupViewHolder(view)
    }

    override fun getItemCount(): Int {

        return arrListGroup.size
    }

    override fun onBindViewHolder(holder: GroupViewHolder, position: Int) {

        val group = arrListGroup[position]

        if (group.collection_list?.isEmpty() == true){
            holder.itemView.txtHeader.visibility= View.GONE
        }else{
            holder.itemView.txtHeader.text = group.title
            holder.itemView.txtHeader.visibility= View.VISIBLE

        }


        holder.itemView.txtHeader.typeface = Global.fontSemiBold
        holder.itemView.txtView.typeface = Global.fontSemiBold

        holder.itemView.rvGroupList.layoutManager =
            LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
        val groupListAdapter =
            GroupListAdapter(
                group.collection_list as ArrayList<CollectionList>,
                group,
                activity,
                homeItemClickInterface,
                categoryType
            )
        holder.itemView.rvGroupList.adapter = groupListAdapter

        groupListAdapter.notifyDataSetChanged()
        when (group.group_type) {
            "G" -> {
                holder.itemView.txtView.visibility = View.GONE
                if (group.hide_title == 1) {
                    holder.itemView.relHeader.visibility = View.GONE
                } else {
                    holder.itemView.relHeader.visibility = View.VISIBLE
                }

            }
            "C" -> {
                holder.itemView.txtView.visibility = View.VISIBLE
                holder.itemView.relHeader.visibility = View.VISIBLE
            }
            "EB" -> {
                holder.itemView.txtView.visibility = View.VISIBLE
                holder.itemView.relHeader.visibility = View.VISIBLE
                holder.itemView.txtHeader.text =
                    activity.resources.getString(R.string.exclusive_brand)
            }
            "FB" -> {
                holder.itemView.txtView.visibility = View.VISIBLE
                holder.itemView.relHeader.visibility = View.VISIBLE
                holder.itemView.txtHeader.text =
                    activity.resources.getString(R.string.featured_brands)
            }
            "FC" -> {
                holder.itemView.txtView.visibility = View.VISIBLE
                holder.itemView.relHeader.visibility = View.VISIBLE
                holder.itemView.txtHeader.text =
                    activity.resources.getString(R.string.featured_categories)
            }
        }

        holder.itemView.txtView.setOnClickListener {
            groupItemClickInterface.onGroupClicked(position)

        }
    }

    class GroupViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    }

}