package com.app.blockaat.celebrities

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.app.blockaat.R
import com.app.blockaat.celebrities.fragment.CelebrityDataFragment
import com.app.blockaat.celebrities.model.InfluencerList
import com.app.blockaat.helper.AppController
import com.app.blockaat.helper.CustomEvents
import com.app.blockaat.helper.Global
import com.app.blockaat.navigation.NavigationActivity
import com.app.blockaat.productdetails.ProductDetailsActivity
import com.app.blockaat.productlisting.ProductListActivity
import com.app.blockaat.productlisting.fragment.ProductListFragment
import com.app.blockaat.productlisting.model.CelebrityTvList
import com.app.blockaat.productlisting.model.ProductListBannerCelebrity
import com.app.blockaat.tv.activity.TvProductActivity
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class CelebrityFragment : Fragment() {
    private var isCelebrityDataFragmentLoaded: Boolean = false
    private lateinit var mActivity: NavigationActivity

    private var strCategoryId: String? = "0"
    private var celebrityDataFragment: Fragment = CelebrityDataFragment()
    private var activeFragment: Fragment = Fragment()
    private var productListFragment: Fragment = ProductListFragment()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mActivity = activity as NavigationActivity
        if (Global.isUserLoggedIn(mActivity)){
            val userId = Global.getUserId(mActivity)
            CustomEvents.screenViewed(mActivity,userId, getString(R.string.talent_screen))
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        strCategoryId = arguments?.getString("categoryID")

        val view = inflater.inflate(R.layout.fragment_musician, container, false)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        displayCelebrityDataFragment()
    }

    private fun displayCelebrityDataFragment() {
        if (activeFragment !is CelebrityDataFragment) {
            childFragmentManager.beginTransaction()
                .add(R.id.fragment_celebrity_frame, celebrityDataFragment, "celebrityDataFragment")
                .show(celebrityDataFragment).commit()
            isCelebrityDataFragmentLoaded = true
            activeFragment = celebrityDataFragment
        }

    }

    fun getFragment(): Fragment {
        return childFragmentManager.fragments[childFragmentManager.fragments.size - 1]
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun displayProductList(productList: ProductListBannerCelebrity) {
        if (productList.type?.toLowerCase() == "c") {
            val bundle = Bundle()
            bundle.putString("header_text", productList.header)
            bundle.putString("isFrom", "categories")
            bundle.putString("parent_type", "home")
            bundle.putString("has_subcategory", "yes")
            bundle.putString("categoryID", productList.id)

            productListFragment = ProductListFragment()
            productListFragment.arguments = bundle
            val transaction = childFragmentManager.beginTransaction()
            transaction.add(R.id.fragment_celebrity_frame, productListFragment, "ProductList")
                .addToBackStack(getFragment().tag).show(productListFragment)
            transaction.commitAllowingStateLoss()
            activeFragment = productListFragment
            (mActivity).displayProductListInHome(productList.header.toString())
        } else if (productList.type?.toLowerCase().toString().toLowerCase() == "p") {
            val intent = Intent(mActivity, ProductDetailsActivity::class.java)
            intent.putExtra("strProductID", productList.id.toString())
            intent.putExtra("strProductName", productList.header)
            startActivity(intent)
        } else if (productList.type?.toLowerCase() == "t" || productList.type.toString()
                .toLowerCase() == "v"
        ) {
            val intent = Intent(mActivity, TvProductActivity::class.java)
            intent.putExtra("tvID", productList.id.toString())
            intent.putExtra("header_text", productList.header)
            startActivity(intent)
        }

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun displayProductListInCelebrity(influencerList: InfluencerList) {
        val bundle = Bundle()
        bundle.putString("influencerID", influencerList.id.toString())
        bundle.putString("header_text", influencerList.title)
        bundle.putString("parent_type", "celebrity")
        bundle.putString("has_subcategory", "yes")
        bundle.putString("banner", influencerList.banner)
        bundle.putString("isFrom", "celebrity")
        bundle.putString("categoryID", Global.getPreferenceCategory(mActivity))

        productListFragment = ProductListFragment()
        productListFragment.arguments = bundle
        val transaction = childFragmentManager.beginTransaction()
        transaction.add(R.id.fragment_celebrity_frame, productListFragment, "ProductList")
            .hide(childFragmentManager.findFragmentByTag("celebrityDataFragment") as Fragment)
            .addToBackStack("ProductList").show(productListFragment)
        transaction.commitAllowingStateLoss()
        activeFragment = productListFragment
        (mActivity).displayProductListInCelebrity(influencerList.title)
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    fun displayProductListInCelebrity(tvModel: CelebrityTvList) {
        /*val bundle = Bundle()
        bundle.putString("header_text", celebrityList.tvs?.name)
        bundle.putString("parent_type", "celebrity")
        bundle.putString("isFrom", "celebrityTab")
        bundle.putString("has_subcategory", "yes")
        bundle.putString("tv_id", celebrityList.tvs?.id.toString())
        bundle.putSerializable("tvData", celebrityList.tvs)
        bundle.putString("categoryID", Global.getPreferenceCategory(mActivity))

        productListFragment = ProductListFragment()
        productListFragment.arguments = bundle
        val transaction = childFragmentManager.beginTransaction()
        transaction.add(R.id.fragment_celebrity_frame, productListFragment, "ProductList1")
            .addToBackStack("ProductList").show(productListFragment)
        transaction.commitAllowingStateLoss()
        activeFragment = productListFragment
        (mActivity).displayProductListInCelebrity(celebrityList.tvs?.name.toString())*/

        val intent = Intent(mActivity, ProductListActivity::class.java)
        intent.putExtra(
            "header_text",
            tvModel?.tvs?.name
        )
        intent.putExtra(
            "categoryID",
            Global.getPreferenceCategory(mActivity)
        )
        intent.putExtra("parent_type", "celebrity")
        intent.putExtra("isFrom", "celebrityTab")
        intent.putExtra("has_subcategory", "yes")
        intent.putExtra("tv_id", tvModel?.tvs?.id)
        intent.putExtra("tvData", tvModel?.tvs)
        startActivity(intent)
    }


    override fun onResume() {
        super.onResume()
        EventBus.getDefault().register(this)
    }

    override fun onPause() {
        super.onPause()
        EventBus.getDefault().unregister(this)
    }

    /*  fun popBack() {
          if (childFragmentManager.backStackEntryCount >= 1) {
              childFragmentManager.popBackStackImmediate()
          }
          activeFragment = celebrityDataFragment
      }*/

    fun popBack(): Boolean {
        return if (childFragmentManager.backStackEntryCount >= 1) {
            childFragmentManager.popBackStackImmediate()
            true
        } else {
            false
        }
    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        if (!hidden) {
            //  displayCelebrityDataFragment()
        }
    }

}