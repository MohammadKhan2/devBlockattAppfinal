package com.app.blockaat.productlisting.fragment

import android.annotation.SuppressLint
import android.app.Activity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.blockaat.R
import com.app.blockaat.helper.Global
import com.app.blockaat.productlisting.adapter.FilterListAdapter
import com.app.blockaat.productlisting.interfaces.OnFilterValueClicked
import com.app.blockaat.productlisting.model.ClearFilter
import com.app.blockaat.productlisting.model.ProductListingFilterValueModel
import kotlinx.android.synthetic.main.fragment_filter_list.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"
private const val ARG_PARAM3 = "param3"

/**
 * A simple [Fragment] subclass.
 * Use the [FilterListFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class FilterListFragment : Fragment(), OnFilterValueClicked {
    // TODO: Rename and change types of parameters
    private var arrListFilterValues: ArrayList<ProductListingFilterValueModel>? = null
    private var arrListOriginalFilterValues: ArrayList<ProductListingFilterValueModel>? = null

    private var param2: String? = null
    private var adapterFilterList: FilterListAdapter? = null
    private var strFilterType: String? = ""
    private var strFilterName: String? = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            arrListFilterValues = it.getParcelableArrayList(ARG_PARAM1)
            arrListOriginalFilterValues = it.getParcelableArrayList(ARG_PARAM1)
            strFilterType = it.getString(ARG_PARAM2)
            strFilterName = it.getString(ARG_PARAM3)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_filter_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initializeFields()
        setFonts()
        setOnClickListener()
        onTextChangeListener()
    }


    @SuppressLint("StringFormatInvalid")
    private fun initializeFields() {
        val filterName =
            strFilterName?.trim()?.get(0)?.toUpperCase().toString() + strFilterName?.trim()
                ?.substring(
                    1
                )?.toLowerCase().toString()
        edtSearch.hint = resources.getString(R.string.filter_search_hint, filterName)
        if (!arrListFilterValues.isNullOrEmpty()) {
            /* adapterFilterList = FilterListAdapter(
                 activity as Activity,
                 arrListFilterValues as ArrayList<ProductListingFilterValueModel>,
                 this
             )*/
            rvFilter.layoutManager = LinearLayoutManager(activity)
            rvFilter.adapter = adapterFilterList
            adapterFilterList?.notifyDataSetChanged()

            relSearch.visibility = View.VISIBLE

        } else {
            relSearch.visibility = View.GONE
        }

    }

    private fun setFonts() {
        edtSearch.typeface = Global.fontMedium
        txtCancel.typeface = Global.fontMedium
    }

    private fun setOnClickListener() {
        ivClear.setOnClickListener {
            edtSearch.setText("")
            ivSearch.visibility = View.GONE
        }
    }

    private fun onTextChangeListener() {
        txtCancel.setOnClickListener {
            Global.hideKeyboard(activity as Activity)
            ivClear.visibility = View.GONE
            txtCancel.visibility = View.GONE
        }
        edtSearch.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(text: Editable?) {
                if (text.toString().isNotEmpty()) {
                    ivClear.visibility = View.VISIBLE
                    txtCancel.visibility = View.VISIBLE
                } else {
                    ivClear.visibility = View.GONE
                }
//                adapterFilterList?.filter?.filter(text.toString())

            }

            override fun beforeTextChanged(text: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(text: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

        })
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    fun clearEditText(clearFilter: ClearFilter) {
        edtSearch.setText("")
    }

    override fun onResume() {
        super.onResume()
        EventBus.getDefault().register(this)
    }

    override fun onPause() {
        super.onPause()
        EventBus.getDefault().unregister(this)

    }

    companion object {

        @JvmStatic
        fun newInstance(
            filter_values: ArrayList<ProductListingFilterValueModel>,
            param2: String,
            param3: String
        ) =
            FilterListFragment().apply {
                arguments = Bundle().apply {
                    putParcelableArrayList(ARG_PARAM1, filter_values)
                    putString(ARG_PARAM2, param2)
                    putString(ARG_PARAM3, param3)
                }
            }
    }

    override fun onFilterClicked(id: String) {

        for (i in 0 until arrListFilterValues?.size as Int) {
            if (id == arrListFilterValues?.get(i)?.id) {
                arrListFilterValues?.get(i)?.isSelected =
                    arrListFilterValues?.get(i)?.isSelected != true
                break
            }
        }
        adapterFilterList?.notifyDataSetChanged()
    }

    fun clearAll() {
        if (!arrListFilterValues.isNullOrEmpty() && adapterFilterList != null) {
            for (i in 0 until arrListFilterValues?.size as Int) {
                arrListFilterValues?.get(i)?.isSelected = false
            }
            adapterFilterList?.notifyDataSetChanged()
        }

    }

    fun getFilteredValues(): ArrayList<ProductListingFilterValueModel>? {
        return arrListFilterValues
    }

    fun getFilteredType(): String {
        return strFilterType.toString()
    }


}
