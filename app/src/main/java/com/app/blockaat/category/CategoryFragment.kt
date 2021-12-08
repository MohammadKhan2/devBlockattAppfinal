package com.app.blockaat.category

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.app.blockaat.R
import com.app.blockaat.category.fragment.CategoryDataFragment
import com.app.blockaat.category.model.Subcategory
import com.app.blockaat.helper.AppController
import com.app.blockaat.helper.CustomEvents
import com.app.blockaat.helper.Global
import com.app.blockaat.navigation.NavigationActivity
import com.app.blockaat.productlisting.fragment.ProductListFragment
import com.app.blockaat.productlisting.model.CelebrityTvList
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class CategoryFragment : Fragment() {

    private var categoryDataFragment: Fragment = CategoryDataFragment()
    private lateinit var mActivity: NavigationActivity

    private var activeFragment: Fragment = Fragment()
    private var productListFragment: Fragment = ProductListFragment()
    private var tagId: String = "ProductListData"
    private var catTagId: String = "SubCategoryDataFragment"
    private var tagNumber = 0
    private var catTagNumber = 0

    private var header = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mActivity = activity as NavigationActivity
        if (Global.isUserLoggedIn(mActivity)){
            val userId = Global.getUserId(mActivity)
            CustomEvents.screenViewed(mActivity, userId,getString(R.string.blocks_screen))
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_category, container, false)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        displayCategoryDataFragment()
    }


    private fun displayCategoryDataFragment() {
        childFragmentManager.beginTransaction()
            .add(R.id.fragment_category_frame, categoryDataFragment, "categoryDataFragment")
            .show(categoryDataFragment).commit()
        activeFragment = categoryDataFragment

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun displayProductList(subCategory: Subcategory) {
        if (subCategory.has_subcategory == "Yes") {
            val bundle = Bundle()
            bundle.putString("categoryID", subCategory.id.toString())
            bundle.putString("header_text", subCategory.name)
            bundle.putString("parent_type", catTagId + catTagNumber)
            bundle.putString("isFrom", "subCategory")
            bundle.putString("image", subCategory.image)
            bundle.putString("has_subcategory", subCategory.has_subcategory)
            bundle.putParcelableArrayList("subcategories", subCategory.subcategories)
           // Previously used SubCategoryDataFragment
            productListFragment = ProductListFragment()
            productListFragment.arguments = bundle
            val transaction = childFragmentManager.beginTransaction()
            transaction.add(R.id.fragment_category_frame, productListFragment, catTagId + catTagNumber)
                .addToBackStack(getFragment().tag).show(productListFragment)
            transaction.commitAllowingStateLoss()
            activeFragment = productListFragment
            (mActivity).displaySubCategoryInCategory(subCategory.name.toString())
//            (mActivity).updateTabPosition()
            catTagNumber++

        } else {
            val bundle = Bundle()
            bundle.putString("categoryID", subCategory.id.toString())
            bundle.putString("header_text", subCategory.name)
            bundle.putString("parent_type", tagId + tagNumber)
            bundle.putString("isFrom", "categories")
            bundle.putString("has_subcategory", subCategory.has_subcategory)
            bundle.putString("banner", subCategory.image)
            bundle.putString("image", subCategory.image)

            productListFragment = ProductListFragment()
            productListFragment.arguments = bundle
            val transaction = childFragmentManager.beginTransaction()
            transaction.add(R.id.fragment_category_frame, productListFragment, tagId + tagNumber)
                .addToBackStack(getFragment().tag).show(productListFragment)
            transaction.commitAllowingStateLoss()
            activeFragment = productListFragment
            (mActivity).displayProductListInCategory(subCategory.name.toString())
//            (mActivity).updateTabPosition()
            tagNumber++
        }


    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun displayTvProductList(tvModel: CelebrityTvList) {
        val bundle = Bundle()
        bundle.putString("header_text", tvModel?.tvs?.name)
        bundle.putString("parent_type", "celebrity")
        bundle.putString("isFrom", "celebrityTab")
        bundle.putString("has_subcategory", "yes")
        bundle.putString("tv_id", tvModel?.tvs?.id.toString())
        bundle.putSerializable("tvData", tvModel.tvs)
        bundle.putString("categoryID", Global.getPreferenceCategory(mActivity))

        productListFragment = ProductListFragment()
        productListFragment.arguments = bundle
        val transaction = childFragmentManager.beginTransaction()
        transaction.add(R.id.fragment_category_frame, productListFragment, tagId + tagNumber)
            .addToBackStack(getFragment().tag).show(productListFragment)
        transaction.commitAllowingStateLoss()
        activeFragment = productListFragment
        (mActivity).displayProductListInCategory(tvModel?.tvs?.name.toString())
        tagNumber++
    }

    override fun onResume() {
        super.onResume()
        EventBus.getDefault().register(this)
    }

    fun getFragment(): Fragment {
        return childFragmentManager.fragments[childFragmentManager.fragments.size - 1]
    }


    override fun onPause() {
        super.onPause()
        EventBus.getDefault().unregister(this)
    }

    fun popBack(): Boolean {
        return if (childFragmentManager.backStackEntryCount >= 1) {
            childFragmentManager.popBackStackImmediate()
            true
        } else {
            false
        }
    }

    /*  override fun onHiddenChanged(hidden: Boolean) {
          super.onHiddenChanged(hidden)
          if (!hidden) {
              displayCategoryDataFragment()
          }
      }*/

}