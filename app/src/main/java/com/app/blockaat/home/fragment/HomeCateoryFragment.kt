package com.app.blockaat.home.fragment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import androidx.viewpager.widget.ViewPager
import com.github.demono.AutoScrollViewPager
import com.app.blockaat.R
import com.app.blockaat.brands.activity.BrandsActivity
import com.app.blockaat.celebrities.activity.CelebrityActivity
import com.app.blockaat.helper.*
import com.app.blockaat.home.adapter.*
import com.app.blockaat.home.interfaces.GroupItemClickListener
import com.app.blockaat.home.interfaces.HomeItemClickInterface
import com.app.blockaat.home.model.*
import com.app.blockaat.navigation.NavigationActivity
import com.bumptech.glide.Glide
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_home_cateory.*
import org.greenrobot.eventbus.EventBus


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [HomeCateoryFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class HomeCateoryFragment : Fragment(), HomeItemClickInterface {
    private lateinit var productsDBHelper: DBHelper
    private val WISHLIST_RESULT: Int = 101

    // TODO: Rename and change types of parameters
    private var param1: Int? = 0
    private var param2: String? = null
    private lateinit var mActivity: NavigationActivity
    private var dialog: CustomProgressBar? = null
    private var autoPager: AutoScrollViewPager? = null

    private var rcyBrands: RecyclerView? = null
    private var viewPagerIndicator: com.app.blockaat.instadotview.InstaDotView? = null
    private var swipeLayout: SwipeRefreshLayout? = null
    private var isFromRefresh = false
    private var lnrBrands: LinearLayout? = null
    private var lnrMain: LinearLayout? = null
    private var influencerAdapter: CelebrityAdapter? = null
    private var adaptArrival: ArrivalAdapter? = null
    private var adaptFeaturd: TopSellerAdapter? = null
    private var adapterPicks: OurPicksAdapter? = null
    private var arrListCelebrityOfWeek: ArrayList<Influencer?>? = null
    private var adapterCelebrityOfWeek: CelebrityOfWeekAdapter? = null
    private var arrListCelebrity: ArrayList<Influencer?>? = null
    private var arrListCelebrity2: ArrayList<Influencer?>? = null
    private var adapterCelebrity1: CelebrityAdapter? = null
    private var adapterCelebrity2: CelebrityAdapter? = null
    private var arrListBrands: ArrayList<Brands?>? = null
    private var adapterBrands: BrandsAdapter? = null
    private var arrListBestSellers: ArrayList<BestSellers?>? = null
    private var adapterTopSeller: TopSellerAdapter? = null
    private var arrListGroup: ArrayList<CollectionGroup> = arrayListOf()
    private var adapterGroupAdapter: GroupAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getInt(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
        mActivity = activity as NavigationActivity
        productsDBHelper = DBHelper(mActivity)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_home_cateory, container, false)
        return view

    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init(view)
        setFont(view)
        getHomePageData()
    }

    private fun init(rootView: View) {
        dialog = CustomProgressBar(mActivity)

        autoPager = rootView.findViewById(R.id.autoPager)
        rcyBrands = rootView.findViewById(R.id.rcyBrands)
        viewPagerIndicator = rootView.findViewById(R.id.viewPagerIndicator)
        swipeLayout = rootView.findViewById(R.id.swipeHome)
        lnrBrands = rootView.findViewById(R.id.lnrBrands)
        lnrMain = rootView.findViewById(R.id.lnrMain)


        //Celebrity of week
        arrListCelebrityOfWeek = ArrayList()
        adapterCelebrityOfWeek = CelebrityOfWeekAdapter(
            arrListCelebrityOfWeek as ArrayList<Influencer?>,
            mActivity,
            param1.toString()
        )
        rcyCelebrityOfWeek.layoutManager = LinearLayoutManager(
            mActivity,
            LinearLayoutManager.HORIZONTAL,
            false
        )
        rcyCelebrityOfWeek.adapter = adapterCelebrityOfWeek

        //Celebrity
        arrListCelebrity = ArrayList()
        arrListCelebrity2 = ArrayList()
        adapterCelebrity1 = CelebrityAdapter(
            arrListCelebrity as ArrayList<Influencer?>,
            mActivity,
            param1.toString()
        )
        rcyCelebrity1.layoutManager = GridLayoutManager(
            mActivity,
            2,
            GridLayoutManager.HORIZONTAL,
            false
        )
        rcyCelebrity1.adapter = adapterCelebrity1
        rcyCelebrity1.isNestedScrollingEnabled = false
        rcyCelebrity2.isNestedScrollingEnabled = false
        rcyCelebrity2.layoutManager = GridLayoutManager(
            mActivity,
            1,
            GridLayoutManager.HORIZONTAL,
            false
        )
        adapterCelebrity2 = CelebrityAdapter(
            arrListCelebrity2 as ArrayList<Influencer?>,
            mActivity,
            param1.toString()
        )
        rcyCelebrity2.adapter = adapterCelebrity2


        //Brand
        arrListBrands = ArrayList()
        adapterBrands =
            BrandsAdapter(arrListBrands as java.util.ArrayList<Brands?>, mActivity, this)
        rcyBrands?.layoutManager = GridLayoutManager(
            mActivity,
            2,
            LinearLayoutManager.HORIZONTAL,
            false
        )
        rcyBrands?.adapter = adapterBrands

        //Best Sellers
        arrListBestSellers = ArrayList()
        adapterTopSeller = TopSellerAdapter(
            arrListBestSellers as java.util.ArrayList<BestSellers?>,
            mActivity
        )
        rcyTopSeller.layoutManager = LinearLayoutManager(
            mActivity,
            LinearLayoutManager.HORIZONTAL,
            false
        )
        rcyTopSeller.adapter = adapterTopSeller

        //Collection group
        arrListGroup = ArrayList()
        rcyCollection.layoutManager = LinearLayoutManager(mActivity)
        adapterGroupAdapter =
            GroupAdapter(
                arrListGroup as ArrayList<CollectionGroup>,
                mActivity,
                this,
                groupItemClickListener,
                param1.toString()
            )
        rcyCollection.adapter = adapterGroupAdapter

        rootView.findViewById<TextView>(R.id.txtTopSellerView).setOnClickListener {
            val homeLinkModel = HomeLinkModel(
                link_id = param1.toString() ?: "",
                link_type = "N",
                type_name = resources.getString(R.string.featured_products) ?: "",
                url = "",
                categoryId = param1.toString()
            )
            EventBus.getDefault().post(homeLinkModel)

//            val intent = Intent(activity, ProductListActivity::class.java)
//            intent.putExtra("isFeatured", "1")
//            intent.putExtra("header_text", resources.getString(R.string.featured_products))
//            intent.putExtra("categoryID", param1.toString())
//            startActivity(intent)

        }


        rootView.findViewById<TextView>(R.id.txtCelebrityView).setOnClickListener {
            //(mActivity).showViewAll(2, param1 as Int)
            val intent = Intent(mActivity, CelebrityActivity::class.java)
            intent.putExtra("categoryID", param1.toString())
            startActivity(intent)
        }

        rootView.findViewById<TextView>(R.id.txtBrandsView).setOnClickListener {
            println("VIew All brand")
            startActivity(Intent(mActivity, BrandsActivity::class.java))
        }


        swipeLayout?.setOnRefreshListener {
            isFromRefresh = true
            swipeLayout?.isRefreshing = true
            swipeLayout?.postDelayed({
                swipeLayout?.isRefreshing = false

                if (NetworkUtil.getConnectivityStatus(mActivity) != 0) {
                    getHomePageData()

                }
            }, 1000)
        }

    }

    private fun setFont(rootView: View) {

        txtCelebrityOfWeek.typeface = Global.fontSemiBold
        txtCelebrity.typeface = Global.fontSemiBold
        txtCelebrityView.typeface = Global.fontSemiBold
        txtTopSeller.typeface = Global.fontSemiBold
        txtTopSellerView.typeface = Global.fontSemiBold
        txtBrands.typeface = Global.fontSemiBold
        txtBrandsView.typeface = Global.fontSemiBold
    }

    ////getting home page data
    private fun getHomePageData() {
        if (NetworkUtil.getConnectivityStatus(mActivity) != 0) {
            //loading
            if (!isFromRefresh)
                showProgressDialog(mActivity)

            Global.apiService.getHomeData(
                com.app.blockaat.apimanager.WebServices.HomeWs + Global.getLanguage(mActivity) + "&store=" + Global.getStoreCode(
                    mActivity
                )
                        + "&user_id=" + Global.getUserId(mActivity) + "&category_id=" + param1
            )
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::handleResponse, this::handleError)
        }
    }
    ////


    ///handling success response for home
    private fun handleResponse(model: HomeResponseModel?) {

        if (!isFromRefresh)
            dismissProgressDialog()

        println("Success : " + model?.status)

        if (model != null && model.status == 200) {

            lnrMain?.visibility = View.VISIBLE
            if (!model.data?.support_email.isNullOrEmpty()) {
                Global.strSupportEmail = model.data?.support_email.toString()
            }
            if (!model.data?.support_phone.isNullOrEmpty()) {
                Global.strSupportPhone = model.data?.support_phone.toString()
            }

            val params = RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                Global.getDimenVallue(mActivity!!, 170.0).toInt()
            )
            val arrListTopBanner = ArrayList<Banner>()
            val arrListBottomBanner = ArrayList<Banner>()

            for (banner in model.data?.banners!!) {
                if (banner.position == "B") {
                    arrListBottomBanner.add(banner)
                } else {
                    arrListTopBanner.add(banner)
                }
            }
            if (arrListBottomBanner.isNullOrEmpty()) {
                relPagerBottom.visibility = View.GONE
            } else {
                relPagerBottom.visibility = View.VISIBLE
            }
            if (arrListTopBanner.isNullOrEmpty()) {
                relPager.visibility = View.GONE
            } else {
                relPager.visibility = View.VISIBLE
            }

            autoPagerBottom?.layoutParams = params
            autoPagerBottom?.adapter =
                BannerAdapter(mActivity!!, arrListBottomBanner, this)

            autoPager?.layoutParams = params
            val bannerAdapter = BannerAdapter(
                mActivity,
                arrListTopBanner,
                this
            )
            autoPager?.adapter = bannerAdapter

            autoPager?.startAutoScroll()
            autoPager?.startAutoScroll(6000)
            autoPager?.isCycle = true
            autoPager?.isStopWhenTouch = false


            viewPagerIndicator?.noOfPages = arrListTopBanner.size
            viewPagerIndicator?.visibleDotCounts = 7
            viewPagerIndicator?.onPageChange(0)

            if (!Global.isEnglishLanguage(mActivity)) {
                arrListTopBanner.reverse()
                bannerAdapter?.notifyDataSetChanged()
                autoPager?.currentItem = bannerAdapter.count - 1
                autoPager?.direction = AutoScrollViewPager.DIRECTION_LEFT
                viewPagerIndicator?.onPageChange(arrListTopBanner?.size - 1)

            }
            autoPager?.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {

                override fun onPageScrollStateChanged(state: Int) {
                }

                override fun onPageScrolled(
                    position: Int,
                    positionOffset: Float,
                    positionOffsetPixels: Int
                ) {
                }

                override fun onPageSelected(position: Int) {

                    if (position > viewPagerIndicator?.noOfPages ?: 1 - 1) {
                        viewPagerIndicator?.onPageChange(viewPagerIndicator?.noOfPages ?: 1 - 1)
                    } else {
                        viewPagerIndicator?.onPageChange(position)
                    }
                }
            })

            //Celebrity of weeek
            if (model.data.influencers_of_the_week?.size!! > 0) {
                arrListCelebrityOfWeek?.clear()
                lnrCelebrityOfWeek?.visibility = View.VISIBLE
                arrListCelebrityOfWeek?.addAll(model.data.influencers_of_the_week)
                adapterCelebrityOfWeek?.notifyDataSetChanged()
            } else {
                lnrCelebrityOfWeek?.visibility = View.GONE
            }

            //BestSellers
            /*     if (model.data.best_seller.size > 0) {
                     lnrTopSeller?.visibility = View.VISIBLE
                     arrListBestSellers?.clear()
                     arrListBestSellers?.addAll(model.data.best_seller)
                     adapterTopSeller?.notifyDataSetChanged()
                 } else {
                     lnrTopSeller?.visibility = View.GONE
                 }*/

            ///Influencers
            if (model.data.influencers?.size!! > 0) {
                lnrCelebrity?.visibility = View.VISIBLE
                arrListCelebrity?.clear()
                arrListCelebrity2?.clear()
                if (model.data.influencers.size > 6) {
                    rcyCelebrity2.visibility = VISIBLE
                    for (i in 0 until model.data.influencers.size) {
                        if (i % 2 == 0) {
                            arrListCelebrity?.add(model.data.influencers[i])
                        } else {
                            arrListCelebrity2?.add(model.data.influencers[i])
                        }
                    }
                    rcyCelebrity1.layoutManager = GridLayoutManager(
                        mActivity,
                        1,
                        GridLayoutManager.HORIZONTAL,
                        false
                    )
                } else if (model.data.influencers.size < 6) {
                    rcyCelebrity2.visibility = GONE
                    rcyCelebrity1.layoutManager = GridLayoutManager(
                        mActivity,
                        2,
                        GridLayoutManager.HORIZONTAL,
                        false
                    )
                    arrListCelebrity2?.clear()
                    arrListCelebrity?.addAll(model.data.influencers)
                }

                adapterCelebrity1?.notifyDataSetChanged()
                adapterCelebrity2?.notifyDataSetChanged()
            } else {
                lnrCelebrity?.visibility = View.GONE
            }

            if (model.data.collectionGroups?.size!! > 0) {
                arrListGroup?.clear()
                arrListGroup?.addAll(model.data.collectionGroups as Collection<CollectionGroup>)
                adapterGroupAdapter?.notifyDataSetChanged()
            }

            //Brands
            /* if (model.data.brands.size > 0) {
                 lnrBrands?.visibility = View.VISIBLE
                 arrListBrands?.clear()
                 arrListBrands?.addAll(model.data.brands)
                 adapterBrands?.notifyDataSetChanged()
                  } else {
                 lnrBrands?.visibility = View.GONE
             }*/

        } else {
            lnrMain?.visibility = View.GONE
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


    private fun showProgressDialog(activity: AppCompatActivity) {
        dialog?.showDialog()
    }

    private fun dismissProgressDialog() {
        dialog?.hideDialog()

    }

    override fun onResume() {
        super.onResume()
        if (influencerAdapter != null) {
            influencerAdapter?.notifyDataSetChanged()
        }

        if (adaptArrival != null) {
            adaptArrival?.notifyDataSetChanged()
        }

        if (adaptFeaturd != null) {
            adaptFeaturd?.notifyDataSetChanged()
        }

        if (adapterPicks != null) {
            adapterPicks?.notifyDataSetChanged()
        }
        updateCollection()
    }

    private val groupItemClickListener = object : GroupItemClickListener {

        override fun onGroupClicked(position: Int) {
            if (!arrListGroup.isNullOrEmpty()) {
                arrListGroup.get(position).category_id = param1.toString()
                EventBus.getDefault().post(arrListGroup.get(position))

            }
        }

    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        if (!hidden) {
            updateCollection()
        }
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment HomeCateoryFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(id: Int, param2: String) =
            HomeCateoryFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_PARAM1, id)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    override fun onClickItem(
        position: Int,
        type: String,
        link_type: String,
        link_id: String,
        header: String
    ) {
        if (type == "openItem") {
            val homeLinkModel = HomeLinkModel(
                link_id = link_id ?: "",
                link_type = link_type ?: "",
                type_name = header ?: "",
                url = "",
                categoryId = param1.toString()
            )

            EventBus.getDefault().post(homeLinkModel)
        } else if (type == "homeWishlist") {
            println("update collectioon")
            updateCollection()
        }

    }


    private fun updateCollection() {
        for (i in 0 until arrListGroup?.size) {
            if (arrListGroup[i].group_type.toString().toLowerCase() == "c") {
                for (j in 0 until arrListGroup[i].collection_list?.size!!) {
                    arrListGroup[i].collection_list?.get(j)?.item_in_wishlist = 0
                    if (productsDBHelper?.isProductPresentInWishlist(
                            arrListGroup[i].collection_list?.get(
                                j
                            )?.id.toString()
                        )
                    ) {
                        println("product id match:" + arrListGroup[i].collection_list?.get(j)?.id)
                        arrListGroup[i].collection_list?.get(j)?.item_in_wishlist = 1
                    }
                }
            }
        }
        adapterGroupAdapter?.notifyDataSetChanged()
    }

}
