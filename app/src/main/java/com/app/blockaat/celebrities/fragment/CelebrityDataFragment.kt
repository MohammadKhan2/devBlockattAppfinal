package com.app.blockaat.celebrities.fragment

import android.annotation.SuppressLint
import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.app.blockaat.R
import com.app.blockaat.celebrities.adapter.InfluencerListAdapter
import com.app.blockaat.celebrities.interfaces.OnInfluencerClickListener
import com.app.blockaat.celebrities.model.InfluenceResponseModel
import com.app.blockaat.celebrities.model.InfluencerList
import com.app.blockaat.faq.model.FaqDataModel
import com.app.blockaat.helper.*
import com.app.blockaat.navigation.NavigationActivity
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_celebrity_data.*
import kotlinx.android.synthetic.main.layout_product_detail.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.util.*
import kotlin.collections.ArrayList


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [CelebrityDataFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class CelebrityDataFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private var dialog: CustomProgressBar? = null
    private var isFromRefresh = false
    private lateinit var mActivity: NavigationActivity

    private var strCategoryId: String? = "0"
    private var header = ""
    private var arrListCelebrities: ArrayList<InfluencerList?>? = null

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
        return inflater.inflate(R.layout.fragment_celebrity_data, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
    }

    private fun init() {
        rcyCelebrity.visibility = GONE
        arrListCelebrities = ArrayList()
        isFromRefresh = false
        header = resources.getString(R.string.bottom_menu_stars)
        /*   header = resources.getString(R.string.menu_celebrities)*/
        swipe_refresh_layout.setOnRefreshListener(SwipeRefreshLayout.OnRefreshListener {
            isFromRefresh = true
            swipe_refresh_layout.isRefreshing = true
            swipe_refresh_layout.postDelayed(Runnable {
                swipe_refresh_layout.isRefreshing = false

                if (NetworkUtil.getConnectivityStatus(mActivity) != 0) {
                    getCelebrity()
                }
            }, 1000)
        })

        if (rcyCelebrity.itemDecorationCount == 0) {
            rcyCelebrity.addItemDecoration(
                GridSpacingItemDecoration(
                    2,
                    2,
                    false,
                    Global.isEnglishLanguage(mActivity)
                )
            )
            rcyCelebrity.addItemDecoration(GridDividerItemDecoration(1))
        }

        getCelebrity()

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun changeCategory(changeCategory: ChangeCategory) {
        getCelebrity()
    }

    override fun onResume() {
        super.onResume()
        EventBus.getDefault().register(this)
    }

    override fun onPause() {
        super.onPause()
        EventBus.getDefault().unregister(this)

    }

    fun getHeader(): String {
        return header
    }

    @SuppressLint("CheckResult")
    private fun getCelebrity() {
        if (NetworkUtil.getConnectivityStatus(mActivity) != 0) {
            //loading
            if (!isFromRefresh)
                showProgressDialog(mActivity)

            Global.apiService.getInfluencer(
                com.app.blockaat.apimanager.WebServices.AllInfluencersWs + Global.getLanguage(
                    mActivity
                ) + "&category_id=" + Global.getPreferenceCategory(mActivity)
            )
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::handleResponse, this::handleError)
        }
    }

    ///handling success response
    private fun handleResponse(model: InfluenceResponseModel?) {

        if (!isFromRefresh)
            dismissProgressDialog()

        println("Success")

        if (model != null) {

            if (model.status == 200) {
                rcyCelebrity?.visibility = View.VISIBLE
                val layoutManager = GridLayoutManager(mActivity, 2)
                arrListCelebrities?.clear()
                arrListCelebrities?.addAll(model.data)
                arrListCelebrities?.sortWith(Comparator { c1, c2 ->
                    c1?.title?.compareTo(c2?.title.toString(), true) ?: 0
                })
                val adapter = InfluencerListAdapter(
                    arrListCelebrities as java.util.ArrayList<InfluencerList?>,
                    mActivity,
                    onInfluencerClickListener
                )
                rcyCelebrity?.layoutManager = layoutManager
                rcyCelebrity?.adapter = adapter

            } else {
                rcyCelebrity?.visibility = View.GONE
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

    private val onInfluencerClickListener = object : OnInfluencerClickListener {

        override fun onInfluencerClicked(position: Int) {
            EventBus.getDefault().post(arrListCelebrities?.get(position))

        }

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

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment CelebrityDataFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            CelebrityDataFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        if (!hidden) {
            // init()
        }
    }
}
