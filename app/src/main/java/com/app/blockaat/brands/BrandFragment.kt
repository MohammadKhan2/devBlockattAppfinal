package com.app.blockaat.brands

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.app.blockaat.R
import com.app.blockaat.brands.model.BrandsDataModel
import com.app.blockaat.brands.fragment.BrandDataFragment
import com.app.blockaat.helper.*
import com.app.blockaat.navigation.NavigationActivity
import com.app.blockaat.productlisting.fragment.ProductListFragment
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class BrandFragment : Fragment() {
    private lateinit var mActivity: NavigationActivity

    private var strCategoryId: String? = "0"
    private var brandDataFragment: Fragment = BrandDataFragment()
    private var activeFragment: Fragment? = null
    private var productListFragment: Fragment = ProductListFragment()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mActivity = activity as NavigationActivity
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        strCategoryId = arguments?.getString("categoryID")

        val rootView = inflater.inflate(R.layout.fragment_brand, container, false)
        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        displayBrandDataFragment()

    }

    private fun displayBrandDataFragment() {
        val bundle = Bundle()
        bundle.putString("categoryID", strCategoryId ?: "")
        brandDataFragment.arguments = bundle
        childFragmentManager.beginTransaction()
            .add(R.id.fragment_brand_frame, brandDataFragment, "BrandDataFragment")
            .show(brandDataFragment).commit()
        activeFragment = brandDataFragment
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun displayProductList(brand: BrandsDataModel) {
        val bundle = Bundle()
        bundle.putString("brandID", brand.id.toString())
        bundle.putString("header_text", brand.name)
        bundle.putString("isFromBrands", "yes")
        bundle.putString("has_subcategory", "yes")
        bundle.putString("parent_type", "brand")
        bundle.putString("isFrom", "brand")
        bundle.putString("banner", brand.banner)
        bundle.putString("categoryID", Global.getPreferenceCategory(mActivity))

        productListFragment = ProductListFragment()
        productListFragment.arguments = bundle
        val transaction = childFragmentManager.beginTransaction()
        transaction.add(R.id.fragment_brand_frame, productListFragment, "ProductList")
            .hide(childFragmentManager.findFragmentByTag("BrandDataFragment") as Fragment)
            .addToBackStack("ProductList").show(productListFragment)
        transaction.commitAllowingStateLoss()
        activeFragment = productListFragment
        (mActivity).displayProductListInBrands(brand.name.toString())
    }

    fun getFragment(): Fragment {
        return childFragmentManager.fragments[childFragmentManager.fragments.size - 1]
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