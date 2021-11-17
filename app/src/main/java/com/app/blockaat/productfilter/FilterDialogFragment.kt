package com.app.blockaat.productfilter

import android.app.Activity
import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.*
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import androidx.viewpager.widget.ViewPager
import com.google.android.material.tabs.TabLayout
import com.app.blockaat.R
import com.app.blockaat.helper.Global
import com.app.blockaat.navigation.NavigationActivity
import com.app.blockaat.productlisting.fragment.FilterListFragment
import com.app.blockaat.productlisting.model.ClearFilter
import com.app.blockaat.productlisting.model.FilterObject
import com.app.blockaat.productlisting.model.ProductListingFilterModel
import org.greenrobot.eventbus.EventBus


class FilterDialogFragment : DialogFragment() {
    private var parentType: String = ""
    private lateinit var arrListFilterData: java.util.ArrayList<ProductListingFilterModel?>
    private var isFrom: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.sheetDialog)
        arrListFilterData = arguments?.get("list") as ArrayList<ProductListingFilterModel?>
        parentType = arguments?.getString("parentType").toString()
        isFrom = arguments?.getString("isFrom").toString()
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.window?.requestFeature(Window.FEATURE_NO_TITLE)
        return dialog

    }

    override fun onStart() {
        super.onStart()
        val dialog = getDialog()
        if (dialog != null) {
            dialog.window?.setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            );
            dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT));
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view: View = inflater.inflate(R.layout.layout_filter_bottomsheet, container)

        return view

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val viewpager = view.findViewById(R.id.viewpager) as ViewPager
        val tabFilter = view.findViewById(R.id.tabFilter) as TabLayout
        val txtClearAll = view.findViewById(R.id.txtClearAll) as TextView
        val txtFilterByLabel = view.findViewById(R.id.txtFilterByLabel) as TextView
        val txtApply = view.findViewById(R.id.txtApply) as TextView
        val relClearAll = view.findViewById(R.id.relClearAll) as RelativeLayout
        val relApply = view.findViewById(R.id.relApply) as RelativeLayout
        val imgCloseSheet = view.findViewById(R.id.imgCloseSheet) as ImageView
        Global.setTabFont(tabFilter, context as Activity)

        for (filter in arrListFilterData) {

            if (filter?.filter_values.isNullOrEmpty()) {
                arrListFilterData.remove(filter)
                break
            }

        }

        for (filter in arrListFilterData) {
            if ((filter?.filter_type.equals(
                    "Brands",
                    true
                ) || filter?.filter_type.equals("Brand")) && isFrom.equals("brand")
            ) {
                arrListFilterData.remove(filter)
                break
            }
        }
        for (filter in arrListFilterData) {
            if ((filter?.filter_type.equals(
                    "Categories",
                    true
                ) || filter?.filter_type.equals("Category")) && isFrom.equals("categories")
            ) {
                arrListFilterData.remove(filter)
                break
            }
        }

        //set data
        val adapterFilter = NavigationActivity.FilterPagerAdapter(
            childFragmentManager,
            arrListFilterData
        )
        viewpager.adapter = adapterFilter
        if (arrListFilterData.size ?: 0 > 3) {
            tabFilter.tabMode = TabLayout.MODE_SCROLLABLE
        }
        tabFilter.setupWithViewPager(viewpager)

        adapterFilter.notifyDataSetChanged()

        //set fonts
        txtClearAll.typeface = Global.fontBtn
        txtApply.typeface = Global.fontBtn
        txtFilterByLabel.typeface = Global.fontBold

        //set click listener
        relClearAll.setOnClickListener {
            if (!arrListFilterData.isNullOrEmpty() && adapterFilter != null) {

                for (i in 0 until arrListFilterData?.size as Int) {
                    val fragment = adapterFilter?.getItem(i)
                    (fragment as FilterListFragment).clearAll()
                }
            }
            adapterFilter.notifyDataSetChanged()
        }
        relApply.setOnClickListener {
            if (!arrListFilterData.isNullOrEmpty() && adapterFilter != null) {
                EventBus.getDefault().post(ClearFilter(true))
                EventBus.getDefault()
                    .post(FilterObject(arrListFilterData, adapterFilter, "apply", parentType))
                dialog?.dismiss()
            }
        }
        imgCloseSheet.setOnClickListener {
            if (!arrListFilterData.isNullOrEmpty() && adapterFilter != null) {

                //filterInterface?.onFilterClicked(arrListFilterData,adapterFilter,"clear")
                for (i in 0 until arrListFilterData.size as Int) {
                    val fragment = adapterFilter.getItem(i)
                    (fragment as FilterListFragment).clearAll()
                }
            }
            adapterFilter.notifyDataSetChanged()
            EventBus.getDefault()
                .post(FilterObject(arrListFilterData, adapterFilter, "apply", parentType))
            dialog?.dismiss()
        }

    }
}
