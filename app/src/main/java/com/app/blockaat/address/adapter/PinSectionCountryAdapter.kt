package com.app.blockaat.address.adapter

import androidx.appcompat.app.AppCompatActivity
import android.content.Intent
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.havrylyuk.alphabetrecyclerview.BaseAlphabeticalAdapter
import com.app.blockaat.R
import com.app.blockaat.helper.Global
import java.util.*


class PinSectionCountryAdapter(val activity: AppCompatActivity) :
    BaseAlphabeticalAdapter<com.app.blockaat.address.model.CountryListDataModel>(activity) {
    public override fun sortList() {
        if (null != entityList) {
            entityList.sortWith(Comparator { c1, c2 ->
                c1.name!!.compareTo(c2.name!!)
            })
        }
    }

    public override fun initHeadersLetters() {
        val characters = TreeSet<Char>()
        if (null != entityList) {
            for (country in entityList) {
                if (!TextUtils.isEmpty(country.name)) {
                    characters.add(country.name!![0])
                }
            }
            headersLetters = characters
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.lv_item_state_list_child, parent, false)
        return ItemViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        //set data
        val viewHolder =
            holder as com.app.blockaat.address.adapter.PinSectionCountryAdapter.ItemViewHolder
        viewHolder.txtTitle.text = entityList[position].name
        if (position == entityList.size - 1) {
            viewHolder.linDivLine.visibility = View.GONE
        } else {
            viewHolder.linDivLine.visibility = View.VISIBLE
        }

        //set fonts
        viewHolder.txtTitle.typeface = Global.fontRegular

        //set click listeners
        viewHolder.relMainHolder.setOnClickListener {
            val intent = Intent()
            intent.putExtra("phoneCode", entityList[position]!!.phonecode.toString())
            intent.putExtra("country_name", entityList[position]!!.name.toString())
            intent.putExtra("country_id", entityList[position]!!.id.toString())
            intent.putExtra("gov_has", entityList[position]!!.has_states.toString())
            activity.setResult(103, intent)
            activity.finish()
        }
    }

    override fun onCreateHeaderViewHolder(parent: ViewGroup): RecyclerView.ViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.lv_item_state_list_header, parent, false)
        return com.app.blockaat.address.adapter.PinSectionCountryAdapter.HeaderViewHolder(
            itemView
        )
    }

    override fun onBindHeaderViewHolder(viewHolder: RecyclerView.ViewHolder, position: Int) {
        val headerViewHolder =
            viewHolder as com.app.blockaat.address.adapter.PinSectionCountryAdapter.HeaderViewHolder
        headerViewHolder.txtStateHeader.text = entityList[position].name!![0].toString()

        //set fonts
        viewHolder.txtStateHeader.typeface = Global.fontSemiBold
    }

    override fun getHeaderId(position: Int): Long {
        return entityList[position].name!![0].toLong()
    }


    inner class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val txtTitle: TextView = itemView.findViewById<View>(R.id.txtTitle) as TextView

        val relMainHolder: RelativeLayout = itemView.findViewById(R.id.relMainHolder)
        val linDivLine: LinearLayout = itemView.findViewById(R.id.linDivLine)
    }

    class HeaderViewHolder(headerView: View) : RecyclerView.ViewHolder(headerView) {
        val txtStateHeader: TextView =
            headerView.findViewById<View>(R.id.txtStateHeader) as TextView
    }
}
