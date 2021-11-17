package com.app.blockaat.brands.fragment

import android.app.Activity
import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout

import com.app.blockaat.R
import com.app.blockaat.brands.adapter.BrandsAdapter
import com.app.blockaat.brands.interfaces.OnBrandClickListener
import com.app.blockaat.brands.model.BrandsDataModel
import com.app.blockaat.helper.*
import com.app.blockaat.navigation.NavigationActivity
import com.havrylyuk.alphabetrecyclerview.StickyHeadersBuilder
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_brand_data.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.util.*
import kotlin.Comparator
import kotlin.collections.ArrayList

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [BrandDataFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class BrandDataFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var mActivity: NavigationActivity
    private var disposable: Disposable? = null
    private var mCompositeDisposable: CompositeDisposable? = null
    private var arrListBrands: ArrayList<BrandsDataModel> = arrayListOf()
    private var adapterBrands: BrandsAdapter? = null
    private var isFromRefresh: Boolean? = false
    private var dialog: CustomProgressBar? = null
    private var strCategoryId: String? = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
        mActivity = activity as NavigationActivity

        Global.setLocale(mActivity)
        mCompositeDisposable = CompositeDisposable()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        strCategoryId = arguments?.getString("categoryID")

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_brand_data, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val layoutManager =
            LinearLayoutManager(mActivity, LinearLayoutManager.VERTICAL, false)
        rvAllBrands.layoutManager = layoutManager
        adapterBrands = BrandsAdapter(arrListBrands, mActivity, onBrandClickListener)

        val topStickyHeadersItemDecoration = StickyHeadersBuilder()
            .setAdapter(adapterBrands)
            .setRecyclerView(rvAllBrands)
            .setSticky(false)
            .build()
        rvAllBrands.addItemDecoration(topStickyHeadersItemDecoration)
        getAllBrands()

        swipe_refresh_layout.setOnRefreshListener(SwipeRefreshLayout.OnRefreshListener {
            isFromRefresh = true
            swipe_refresh_layout.setRefreshing(true)
            swipe_refresh_layout.postDelayed(Runnable {
                swipe_refresh_layout.setRefreshing(false)

                if (NetworkUtil.getConnectivityStatus(mActivity) != 0) {
                    //set adapater based on user login and logout
                    getAllBrands()

                }
            }, 1000)
        })


    }

    private fun getAllBrands() {
        if (NetworkUtil.getConnectivityStatus(mActivity) != 0) {

            if (!isFromRefresh!!) {
                showProgressDialog(mActivity)
            }

            disposable = Global.apiService.getBrandData(
                com.app.blockaat.apimanager.WebServices.BrandsWs
                        + Global.getStringFromSharedPref(
                    mActivity,
                    Constants.PREFS_LANGUAGE
                ) + "&category_id=" + Global.getPreferenceCategory(mActivity)
            )
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { result ->

                        if (!isFromRefresh!!) {
                            hideProgressDialog()
                        }
                        if (result != null) {
                            if (result.status == 200) {
                                arrListBrands.clear()
                                adapterBrands?.notifyDataSetChanged()
                              ///  println("Brand data: ")
                                if (result.data != null && result.data.isNotEmpty()) {
                                    result.data.let { arrListBrands.addAll(it) }
                                    setData()
                                }
                            }

                        } else {
                            //no data
                        }


                    },
                    { error ->
                        //println("ERROR - Brands Ws :   " + error.localizedMessage)
                        hideProgressDialog()
                    })

        }
    }

    private fun setData() {
        //brand list
        arrListBrands.sortWith(Comparator { p0, p1 ->
            (p0?.name ?: "").compareTo(
                p1?.name ?: "",
                ignoreCase = true
            )
        })

        adapterBrands?.setData(arrListBrands as List<BrandsDataModel?>?)

        linBrandIndex.removeAllViews()
        for (element in adapterBrands!!.headersLetters as TreeSet<Char>) {
            val view = View.inflate(mActivity, R.layout.item_brand_index, null)
            val txtCountryIndex = view.findViewById<TextView>(R.id.txtBrandsIndex)
            txtCountryIndex.text = element.toString()
            txtCountryIndex.typeface = Global.fontRegular
            txtCountryIndex.setOnClickListener {
                for (i in arrListBrands.indices) {
                    if (arrListBrands[i]?.name?.get(0) ?: "" == txtCountryIndex.text.toString()[0]) {
                        rvAllBrands!!.layoutManager?.scrollToPosition(i)
                        break
                    }
                }
            }
            linBrandIndex.addView(view)
        }
        rvAllBrands.visibility = View.VISIBLE
        scrollIndex.visibility = View.VISIBLE
        swipe_refresh_layout.visibility = View.VISIBLE

    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    fun changeCategory(changeCategory: ChangeCategory) {
//        getAllBrands()
    }

    override fun onResume() {
        super.onResume()
        EventBus.getDefault().register(this)
    }

    override fun onPause() {
        super.onPause()
        EventBus.getDefault().unregister(this)

    }

    fun hideSoftKeyboard() {
        if (mActivity.currentFocus != null) {
            val inputMethodManager =
                mActivity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.hideSoftInputFromWindow(mActivity.currentFocus!!.windowToken, 0)
        }
    }

    private val onBrandClickListener = object : OnBrandClickListener {

        override fun onBrandClick(type: String, position: Int, dateModel: Any?) {
            EventBus.getDefault().post(arrListBrands[position])

        }

    }

    private fun showProgressDialog(mActivity: Activity) {
        dialog = CustomProgressBar(mActivity)
        dialog?.showDialog()

    }

    private fun hideProgressDialog() {
        dialog?.hideDialog()

    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment BrandDataFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            BrandDataFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}
