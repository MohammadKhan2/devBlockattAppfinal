package com.app.blockaat.category.fragment

import android.annotation.SuppressLint
import android.app.Activity
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout

import com.app.blockaat.R
import com.app.blockaat.category.adapter.CategoryAdapter
import com.app.blockaat.category.interfaces.OnCategorySelectListener
import com.app.blockaat.category.model.CategoryResponseDataModel
import com.app.blockaat.category.model.Subcategory
import com.app.blockaat.helper.ChangeCategory
import com.app.blockaat.helper.CustomProgressBar
import com.app.blockaat.helper.Global
import com.app.blockaat.helper.NetworkUtil
import com.app.blockaat.navigation.NavigationActivity
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_category_data.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [CategoryDataFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class CategoryDataFragment : Fragment(), OnCategorySelectListener{
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private var dialog: CustomProgressBar? = null
    private lateinit var mActivity: NavigationActivity
    private var isFromRefresh = false
    private var arrListCategory: ArrayList<Subcategory>? = null
    private var adapterCategory: CategoryAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
        mActivity = activity as NavigationActivity

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_category_data, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()

    }

    private fun init() {
        rcyCategory.visibility = GONE
        isFromRefresh = false
        arrListCategory = ArrayList()
        adapterCategory =
            CategoryAdapter(arrListCategory as ArrayList<Subcategory>, mActivity, this)
        rcyCategory.layoutManager = LinearLayoutManager(mActivity)
        rcyCategory.adapter = adapterCategory

        swipe_category.setOnRefreshListener(SwipeRefreshLayout.OnRefreshListener {
            isFromRefresh = true
            swipe_category.isRefreshing = true
            swipe_category.postDelayed(Runnable {
                swipe_category.isRefreshing = false

                if (NetworkUtil.getConnectivityStatus(mActivity) != 0) {
                    getCategories()
                }
            }, 1000)
        })

        getCategories()

    }

    ////getting data
    @SuppressLint("CheckResult")
    private fun getCategories() {
        if (NetworkUtil.getConnectivityStatus(mActivity) != 0) {
            //loading
            if (!isFromRefresh)
                showProgressDialog(mActivity)

            Global.apiService.getCategories(
                com.app.blockaat.apimanager.WebServices.CategoryWs + Global.getSelectedLanguage(
                    mActivity
                ) + "&category_id=" + 1
            )
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::handleResponse, this::handleError)
        }
    }
    ////

    ///handling success response
    private fun handleResponse(model: CategoryResponseDataModel?) {

        if (!isFromRefresh)
            dismissProgressDialog()

      //  println("Success")

        if (model != null) {
            if (model.status == 200) {
                rcyCategory?.visibility = View.VISIBLE
                arrListCategory?.clear()
                model.data?.let { arrListCategory?.addAll((it)) }
                adapterCategory?.notifyDataSetChanged()
            } else {
                rcyCategory?.visibility = View.GONE
            }

        }
    }
    ////

    ///handling error
    private fun handleError(error: Throwable) {

        if (!isFromRefresh)
            dismissProgressDialog()

        println("Error : " + error.localizedMessage)
    }
    ////

    override fun onCategorySelected(position: Int) {
        EventBus.getDefault().post(arrListCategory?.get(position))

    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    fun changeCategory(changeCategory: ChangeCategory) {
        getCategories()
    }

    private fun showProgressDialog(activity: Activity) {
        dialog = CustomProgressBar(activity)
        dialog?.showDialog()
    }

    private fun dismissProgressDialog() {
        if (dialog != null) {
            dialog?.hideDialog()
        }
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
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment CategoryDataFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            CategoryDataFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        if (!hidden) {
            //init()
        }
    }


}
