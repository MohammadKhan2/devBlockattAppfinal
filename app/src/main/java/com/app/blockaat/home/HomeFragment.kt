package com.app.blockaat.home


import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.app.blockaat.R
import com.app.blockaat.brands.activity.BrandsActivity
import com.app.blockaat.category.activity.CategoryActivity
import com.app.blockaat.category.activity.SubCategoryActivity
import com.app.blockaat.helper.AppController
import com.app.blockaat.helper.CustomEvents
import com.app.blockaat.helper.Global
import com.app.blockaat.home.fragment.HomeDataFragment
import com.app.blockaat.home.model.*
import com.app.blockaat.navigation.NavigationActivity
import com.app.blockaat.productdetails.ProductDetailsActivity
import com.app.blockaat.productlisting.ProductListActivity
import com.app.blockaat.productlisting.fragment.ProductListFragment
import com.app.blockaat.productlisting.model.HomeTvList
import com.app.blockaat.productlisting.model.ProductListBannerHome
import com.app.blockaat.tv.activity.TvProductActivity
import com.app.blockaat.webview.WebViewActivity
import com.google.android.gms.analytics.HitBuilders
import com.google.android.gms.analytics.Tracker
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode


class HomeFragment : Fragment() {
    private lateinit var mActivity: NavigationActivity
    private var homeDataFragment: Fragment = HomeDataFragment()
    private var activeFragment: Fragment? = null
    private var productListFragment: Fragment = ProductListFragment()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mActivity = activity as NavigationActivity
        CustomEvents.screenViewed(activity as NavigationActivity, getString(R.string.home_screen))
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_home, container, false)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        displayHomeDataFragment()

    }

    private fun displayHomeDataFragment() {
        childFragmentManager.beginTransaction()
            .add(R.id.fragment_home_frame, homeDataFragment, "homeDataFragment")
            .show(homeDataFragment).commit()
        activeFragment = homeDataFragment

    }

    //Display product list from CelebrityAdapter and CelebrityOfweekAdapter
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun displayProductList(influencer: Influencer) {
        val bundle = Bundle()
        bundle.putString("influencerID", influencer.id.toString())
        bundle.putString("header_text", influencer.title)
        bundle.putString("banner", influencer.banner)
        bundle.putString("isFrom", "celebrityHome")
        bundle.putString("parent_type", "home")
        bundle.putString("has_subcategory", "yes")
        bundle.putString("categoryID", "1") //Perviously pass influencer.categoryId // CategoryID paa Always 1

        productListFragment = ProductListFragment()
        productListFragment.arguments = bundle
        val transaction = childFragmentManager.beginTransaction()
        transaction.add(R.id.fragment_home_frame, productListFragment, "ProductList")
            .addToBackStack(getFragment().tag).show(productListFragment)
        transaction.commitAllowingStateLoss()
        activeFragment = productListFragment
        (mActivity).displayProductListInHome(influencer.title.toString())
    }

// WOrking
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun displayProductList(productList: ProductListBannerHome) {
        if (productList.type?.toLowerCase() == "c") {
            val bundle = Bundle()
            bundle.putString("header_text", productList.header)
            bundle.putString("isFrom", "categories")
            bundle.putString("parent_type", "home")
            bundle.putString("has_subcategory", "no")
            bundle.putString("categoryID", "1")//productList.id

            productListFragment = ProductListFragment()
            productListFragment.arguments = bundle
            val transaction = childFragmentManager.beginTransaction()
            transaction.add(R.id.fragment_home_frame, productListFragment, "ProductList")
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
    fun displayProductList(tvModel: HomeTvList) {
        /* val bundle = Bundle()
         bundle.putString("header_text", hometvList.tvs?.name)
         bundle.putString("isFrom", "home")
         bundle.putString("parent_type", "home")
         bundle.putString("has_subcategory", "yes")
         bundle.putString("tv_id", hometvList.tvs?.id.toString())
         bundle.putSerializable("tvData", hometvList.tvs)
         bundle.putString("categoryID", Global.getPreferenceCategory(mActivity))

         productListFragment = ProductListFragment()
         productListFragment.arguments = bundle
         val transaction = childFragmentManager.beginTransaction()
         transaction.add(R.id.fragment_home_frame, productListFragment, "ProductList1")
             .addToBackStack(getFragment().tag).show(productListFragment)
         transaction.commitAllowingStateLoss()
         activeFragment = productListFragment
         mActivity.displayProductListInHome(hometvList.tvs?.name.toString())*/
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


    fun getFragment(): Fragment {
        return childFragmentManager.fragments[childFragmentManager.fragments.size - 1]
    }


    //Display product list from GroupAdapter
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun displayProductListGroupAdapter(collection: CollectionGroup) {

        if (collection.group_type == "EB") {
            /*(activity as NavigationActivity).showViewAll(
                1,
                collection.group_type_id?.toInt() as Int
            )*/
            val i = Intent(mActivity, BrandsActivity::class.java)
            i.putExtra("categoryId", collection.category_id)
            startActivity(i)

        }
        if (collection.group_type == "FB") {
            /* (activity as NavigationActivity).showViewAll(
                 1,
                 collection.group_type_id?.toInt() as Int
             )*/
            val i = Intent(mActivity, BrandsActivity::class.java)
            i.putExtra("categoryId", collection.category_id)
            startActivity(i)

        }
        if (collection.group_type == "FC") {
            /*  (activity as NavigationActivity).showViewAll(
                  3,
                  collection.group_type_id?.toInt() as Int
              )*/
            val i = Intent(mActivity, CategoryActivity::class.java)
          //  i.putExtra("categoryId", collection.category_id)
            // Category ID Always Pass 1
            i.putExtra("categoryId", "1")
            i.putExtra("is_featured", "1")
            startActivity(i)
        }
        if (collection.group_type == "C") {
            val bundle = Bundle()
            bundle.putString("categoryID", collection.group_type_id)
            bundle.putString("header_text", collection.title)
            bundle.putString("parent_type", "home")
            bundle.putString("has_subcategory", "no")
            bundle.putString("isFrom", "categories")
            productListFragment = ProductListFragment()
            productListFragment.arguments = bundle
            val transaction = childFragmentManager.beginTransaction()
            transaction.add(R.id.fragment_home_frame, productListFragment, "ProductList")
                .hide(childFragmentManager.findFragmentByTag("homeDataFragment") as Fragment)
                .addToBackStack(getFragment().tag).show(productListFragment)
            transaction.commitAllowingStateLoss()
            activeFragment = productListFragment
            (mActivity).displayProductListInHome(collection.title.toString())
        }

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun displayProductListFromBanner(homeLinkModel: HomeLinkModel) {
        println("Home link: " + homeLinkModel)
        if (homeLinkModel.link_type.isNotEmpty() && homeLinkModel.link_id.isNotEmpty()) {
            if (homeLinkModel?.link_type.toString().toLowerCase() == "p") {
                println("link type:" + homeLinkModel.link_type + " link id:" + homeLinkModel.link_id)
                val intent = Intent(mActivity, ProductDetailsActivity::class.java)
                intent.putExtra("strProductID", homeLinkModel?.link_id.toString())
                intent.putExtra("strProductName", homeLinkModel?.type_name.toString())
                startActivity(intent)
            } else if (homeLinkModel?.link_type.toString()
                    .toLowerCase() == "t" || homeLinkModel?.link_type.toString()
                    .toLowerCase() == "v"
            ) {
                println("link type:" + homeLinkModel.link_type + " link id:" + homeLinkModel.link_id)
                val intent = Intent(mActivity, TvProductActivity::class.java)
                intent.putExtra("tvID", homeLinkModel?.link_id.toString())
                intent.putExtra("header_text", homeLinkModel?.type_name)
                startActivity(intent)
            } else {
                println("link type:" + homeLinkModel.link_type + " link id:" + homeLinkModel.link_id)
                val bundle = Bundle()
                when (homeLinkModel.link_type.toLowerCase()) {
                    "cl" -> {
                        bundle.putString("influencerID", homeLinkModel.link_id.toString())
                        bundle.putString("parent_type", "celebrity")
                        bundle.putString("categoryID", homeLinkModel.categoryId)
                    }
                    "br" -> {
                        bundle.putString("brandID", homeLinkModel.link_id.toString())
                        bundle.putString("isFromBrands", "yes")
                        bundle.putString("isFrom", "brand")
                        bundle.putString("categoryID", homeLinkModel.categoryId)
                    }
                    "n" -> {
                        bundle.putString("categoryID", homeLinkModel.link_id.toString())
                        bundle.putString("isFeatured", "1")
                        bundle.putString("isFrom", "categories")
                    }
                    "c" -> {
                        bundle.putString("categoryID", homeLinkModel.link_id.toString())
                        bundle.putString("isFrom", "categories")
                    }
                }

                bundle.putString("parent_type", "home")
                bundle.putString("has_subcategory", "no")
                productListFragment = ProductListFragment()
                productListFragment.arguments = bundle
                val transaction = childFragmentManager.beginTransaction()
                transaction.add(R.id.fragment_home_frame, productListFragment, "ProductList")
                    .hide(childFragmentManager.findFragmentByTag("homeDataFragment") as Fragment)
                    .addToBackStack(getFragment().tag).show(productListFragment)
                transaction.commitAllowingStateLoss()
                activeFragment = productListFragment
                (mActivity).displayProductListInHome(homeLinkModel.type_name)
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun displayProductListFromGroupListAdapter(collectionList: GroupListData) {
        println("Collection list clicked: " + collectionList.groupType)
        if (collectionList.groupType == "E") {
            val intent = Intent(mActivity, WebViewActivity::class.java)
            intent.putExtra("strUrl", collectionList.collectionList.link)
            intent.putExtra("text_header", collectionList.collectionList.title)
            startActivity(intent)
        }

        if (collectionList.groupType == "C") {

            val bundle = Bundle()
            bundle.putString("categoryID", collectionList.collectionList.type_id.toString())
            bundle.putString("header_text", collectionList.collectionList.title)
            bundle.putString("isFrom", "categories")
            bundle.putString("parent_type", "home")
            bundle.putString("has_subcategory", "no")

            productListFragment = ProductListFragment()
            productListFragment.arguments = bundle
            val transaction = childFragmentManager.beginTransaction()
            transaction.add(R.id.fragment_home_frame, productListFragment, "ProductList")
                .hide(childFragmentManager.findFragmentByTag("homeDataFragment") as Fragment)
                .addToBackStack(getFragment().tag).show(productListFragment)
            transaction.commitAllowingStateLoss()
            activeFragment = productListFragment
            (mActivity).displayProductListInHome(collectionList.collectionList.title.toString())

        }

        if (collectionList.groupType == "FC") {
            println("FC collection :: " + collectionList.collectionList)
            /*  val bundle = Bundle()
              bundle.putString("categoryID", collectionList.collectionList.id.toString())
              bundle.putString("header_text", collectionList.collectionList.name)
              bundle.putString("isFrom", "categories")
              bundle.putString("parent_type", "home")
              bundle.putString("has_subcategory", "yes")

              productListFragment = ProductListFragment()
              productListFragment.arguments = bundle
              val transaction = childFragmentManager.beginTransaction()
              transaction.add(R.id.fragment_home_frame, productListFragment, "ProductList")
                  .hide(childFragmentManager.findFragmentByTag("homeDataFragment") as Fragment)
                  .addToBackStack(getFragment().tag).show(productListFragment)
              transaction.commitAllowingStateLoss()
              activeFragment = productListFragment
              (activity as NavigationActivity).displayProductListInHome(collectionList.collectionList.name.toString())*/

            /*Previously pass SubCategoryActivity    Changes done depaend on requirnment*/
            val intent = Intent(mActivity, ProductListActivity::class.java)
            intent.putExtra("categoryID", collectionList.collectionList?.id.toString())
            intent.putExtra("header_text", collectionList.collectionList?.name)
            intent.putExtra("has_subcategory", "no")
            intent.putExtra("parent_type", "SubCategory")
            intent.putExtra("isFrom", "subCategory")
            startActivity(intent)

        }

        if (collectionList.groupType == "EB") {
            println("")
            val bundle = Bundle()
            bundle.putString("brandID", collectionList.collectionList.id.toString())
            bundle.putString("header_text", collectionList.collectionList.name)
            bundle.putString("banner", collectionList.collectionList.banner)
            bundle.putString("isFrom", "brand")
            bundle.putString("categoryID", collectionList.collectionList.parentCAtType)
            bundle.putString("parent_type", "home")
            bundle.putString("has_subcategory", "yes")

            productListFragment = ProductListFragment()
            productListFragment.arguments = bundle
            val transaction = childFragmentManager.beginTransaction()
            transaction.add(R.id.fragment_home_frame, productListFragment, "ProductList")
                .hide(childFragmentManager.findFragmentByTag("homeDataFragment") as Fragment)
                .addToBackStack(getFragment().tag).show(productListFragment)
            transaction.commitAllowingStateLoss()
            activeFragment = productListFragment
            (mActivity).displayProductListInHome(collectionList.collectionList.name.toString())
        }

        if (collectionList.groupType == "FB") {

            val bundle = Bundle()
            bundle.putString("brandID", collectionList.collectionList.id.toString())
            bundle.putString("header_text", collectionList.collectionList.name)
            bundle.putString("banner", collectionList.collectionList.banner)
            bundle.putString("parent_type", "home")
            bundle.putString("has_subcategory", "yes")
            bundle.putString("isFrom", "brand")
            bundle.putString("categoryID", collectionList.collectionList.parentCAtType)
            productListFragment = ProductListFragment()
            productListFragment.arguments = bundle
            val transaction = childFragmentManager.beginTransaction()
            transaction.add(R.id.fragment_home_frame, productListFragment, "ProductList")
                .hide(childFragmentManager.findFragmentByTag("homeDataFragment") as Fragment)
                .addToBackStack(getFragment().tag).show(productListFragment)
            transaction.commitAllowingStateLoss()
            activeFragment = productListFragment
            (mActivity).displayProductListInHome(collectionList.collectionList.name.toString())

        }

        if (collectionList.groupType == "G") {

            println("Collection type:: " + collectionList.collectionList.type)
            if (collectionList.collectionList.type == "E") {
                val intent = Intent(mActivity, WebViewActivity::class.java)
                intent.putExtra("strUrl", collectionList.collectionList.link)
                intent.putExtra("text_header", collectionList.collectionList.title)
                startActivity(intent)

            }

            if (collectionList.collectionList.type == "B" || collectionList.collectionList.type == "BR") {
                if (!collectionList.collectionList.type_id.isNullOrEmpty()) {
                    val bundle = Bundle()
                    bundle.putString(
                        "brandID",
                        collectionList.collectionList.type_id.toString()
                    )
                    bundle.putString("header_text", collectionList.collectionList.title)
                    bundle.putString("parent_type", "home")
                    bundle.putString("isFrom", "brand")
                    bundle.putString("has_subcategory", "yes")
                    bundle.putString("categoryID", collectionList.collectionList.parentCAtType)

                    productListFragment = ProductListFragment()
                    productListFragment.arguments = bundle
                    val transaction = childFragmentManager.beginTransaction()
                    transaction.add(
                        R.id.fragment_home_frame,
                        productListFragment,
                        "ProductList"
                    )
                        .addToBackStack(getFragment().tag).show(productListFragment)
                    transaction.commitAllowingStateLoss()
                    activeFragment = productListFragment
                    mActivity.displayProductListInHome(collectionList.collectionList.title.toString())
                }

            }

            if (collectionList.collectionList.type == "P") {
                if (!collectionList.collectionList.type_id.isNullOrEmpty()) {
                    println("Parent cat id: " + collectionList.collectionList.parentCAtType)
                    val intent = Intent(mActivity, ProductDetailsActivity::class.java)
                    intent.putExtra(
                        "strProductID",
                        collectionList.collectionList.type_id.toString()
                    )
                    intent.putExtra(
                        "categoryID",
                        collectionList.collectionList.parentCAtType.toString()
                    )
                    intent.putExtra("strProductName", collectionList.collectionList.title)
                    startActivity(intent)
                }

            }

            if (collectionList.collectionList.type == "C") {
                val bundle = Bundle()
                bundle.putString("categoryID", collectionList.collectionList.type_id.toString())
                bundle.putString("header_text", collectionList.collectionList.title)
                bundle.putString("parent_type", "home")
                bundle.putString("has_subcategory", "no")
                bundle.putString("isFrom", "categories")
                productListFragment = ProductListFragment()
                productListFragment.arguments = bundle
                val transaction = childFragmentManager.beginTransaction()
                transaction.add(R.id.fragment_home_frame, productListFragment, "ProductList")
                    .addToBackStack(getFragment().tag).show(productListFragment)
                transaction.commitAllowingStateLoss()
                activeFragment = productListFragment
                (mActivity).displayProductListInHome(collectionList.collectionList.title.toString())
            }

            /* if (collectionList.collectionList.type == "FC") {
                 val bundle = Bundle()
                 bundle.putString("categoryID", collectionList.collectionList.type_id.toString())
                 bundle.putString("header_text", collectionList.collectionList.title)
                 bundle.putString("parent_type", "home")
                 bundle.putString("isFrom", "categories")
                 bundle.putString("has_subcategory", "no")
                 productListFragment = ProductListFragment()
                 productListFragment.arguments = bundle
                 val transaction = childFragmentManager.beginTransaction()
                 transaction.add(R.id.fragment_home_frame, productListFragment, "ProductList")
                     .addToBackStack(getFragment().tag).show(productListFragment)
                 transaction.commitAllowingStateLoss()
                 activeFragment = productListFragment
                 (activity as NavigationActivity).displayProductListInHome(collectionList.collectionList.title.toString())
             }*/
        }

    }


    fun popBack(): Boolean {
        return if (childFragmentManager.backStackEntryCount >= 1) {
            childFragmentManager.popBackStackImmediate()
            true
        } else {
            false
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


}