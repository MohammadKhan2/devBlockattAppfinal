package com.app.blockaat.wishlist.fragment

import androidx.appcompat.app.AppCompatActivity
import android.app.ProgressDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.app.blockaat.R
import com.app.blockaat.helper.*
import com.app.blockaat.navigation.NavigationActivity
import com.app.blockaat.wishlist.adapter.WishlistAdapter
import com.app.blockaat.wishlist.modelclass.WishListDataModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import java.util.ArrayList

class WishListFragment : Fragment() {
    internal var txtNoItem: TextView? = null
    internal var txtWishListEmptyDetail: TextView? = null
    internal var txtItems: TextView? = null
    private lateinit var layoutManager: GridLayoutManager
    private var disposable: Disposable? = null
    private var progressDialog: ProgressDialog? = null
    private lateinit var adapterWishList: WishlistAdapter
    private lateinit var arrListWishlistData: ArrayList<WishListDataModel>
    private lateinit var productsDBHelper: DBHelper
    private var isFromRefresh: Boolean? = false
    private var rvWishList: RecyclerView? = null
    private var swipeRefreshLayout: SwipeRefreshLayout? = null
    private var linNoItems: LinearLayout? = null
    private var isFromFragment: Boolean? = true
    var myActivity: AppCompatActivity? = null
    private var linDivItems: LinearLayout? = null
    private var relItems: RelativeLayout? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_wish_list, container, false)

        txtNoItem = rootView.findViewById<View>(R.id.txtNoItem) as TextView
        txtWishListEmptyDetail =
            rootView.findViewById<View>(R.id.txtWishListEmptyDetail) as TextView
        txtItems = rootView.findViewById<View>(R.id.txtItems) as TextView
        relItems = rootView.findViewById(R.id.relItems)
        linDivItems = rootView.findViewById(R.id.linDivItems) as LinearLayout

        txtItems!!.typeface = Global.fontLight
        txtNoItem!!.typeface = Global.fontSemiBold
        txtWishListEmptyDetail!!.typeface = Global.fontSemiBold
      //  println("Here i am Wishlist fragment")
        myActivity = activity as NavigationActivity?

        Global.setLocale(myActivity!!)
        initializeFields(rootView)

        return rootView
    }

    private fun initializeFields(rootView: View) {
        productsDBHelper = DBHelper(myActivity!!)
        layoutManager = GridLayoutManager(myActivity!!, 2)

        rvWishList = rootView.findViewById(R.id.rvWishList)
        swipeRefreshLayout = rootView.findViewById(R.id.swipeRefreshLayout)
        linNoItems = rootView.findViewById(R.id.linNoItems)
        rvWishList!!.layoutManager = layoutManager
        rvWishList!!.isNestedScrollingEnabled = false


        swipeRefreshLayout!!.setOnRefreshListener {
            isFromRefresh = true
            swipeRefreshLayout!!.isRefreshing = true
            swipeRefreshLayout!!.postDelayed({
                swipeRefreshLayout!!.isRefreshing = false
                //Load wishList data
                if (Global.getStringFromSharedPref(
                        myActivity!!,
                        Constants.PREFS_isUSER_LOGGED_IN
                    ) == "yes"
                ) {
                    getWishList()
                } else {
                    rvWishList!!.visibility = View.GONE
                    linNoItems!!.visibility = View.VISIBLE
                    linDivItems!!.visibility = View.GONE
                }
            }, 1000)
        }

        rvWishList!!.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                val topRowVerticalPosition =
                    if (recyclerView == null || recyclerView.childCount == 0) 0 else recyclerView.getChildAt(
                        0
                    ).top
                swipeRefreshLayout!!.isEnabled = topRowVerticalPosition >= 0

            }

        })

        if (Global.getTotalWishListProductCount(myActivity!!) != -1) {
            if (Global.getTotalWishListProductCount(activity as AppCompatActivity) == 1) {
//                txtItems!!.text = resources.getString(
//                    R.string.no_of_item_in_your_saves,
//                    Global.getTotalWishListProductCount(myActivity!!).toString()
//                )
            } else {
//                txtItems!!.text = resources.getString(
//                    R.string.no_of_item_s_in_your_saves,
//                    Global.getTotalWishListProductCount(myActivity!!).toString()
//                )
            }
            linDivItems!!.visibility = View.VISIBLE
            relItems!!.visibility = View.VISIBLE
        } else {
            linDivItems!!.visibility = View.GONE
            relItems!!.visibility = View.GONE
//            txtItems!!.text = resources.getString(R.string.no_of_item_s_in_your_saves, "0")
        }

        txtWishListEmptyDetail!!.setOnClickListener {
            //(activity as MainActivity).loadHomeFragment()
        }
    }

    private fun getWishList() {
        if (NetworkUtil.getConnectivityStatus(myActivity!!) != 0) {
            if (!isFromRefresh!!) {
                showProgressDialog(myActivity!!)
            }
            disposable = Global.apiService.getWishList(
                com.app.blockaat.apimanager.WebServices.WishListWs + Global.getStringFromSharedPref(
                    myActivity!!,
                    Constants.PREFS_LANGUAGE
                ) + "&user_id=" + Global.getStringFromSharedPref(
                    myActivity!!,
                    Constants.PREFS_USER_ID
                )
                        + "&store=" + Global.getStringFromSharedPref(
                    myActivity!!,
                    Constants.PREFS_STORE_CODE
                )
            )
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { result ->
                   //     println("RESPONSE - WISH LIST Ws :   " + result.data)
                        productsDBHelper.deleteTable("table_wishlist")
                        if (result != null) {
                            if (!isFromRefresh!!) {
                                hideProgressDialog()
                            }
                            if (result.status == 200) {

                                if (result.data != null && result.data.isNotEmpty()) {
                                    rvWishList!!.visibility = View.VISIBLE
                                    linNoItems!!.visibility = View.GONE
                                    arrListWishlistData = result.data
//                                    adapterWishList = WishlistAdapter(myActivity!!, arrListWishlistData, onWishlistUpdateClicked, productsDBHelper, isFromFragment!!)
                                    rvWishList!!.adapter = adapterWishList

                                    for (x in 0 until arrListWishlistData.size) {
                                        productsDBHelper.addProductToWishlist(
                                            ProductsDataModel(
                                                arrListWishlistData[x].id.toString()
                                            )
                                        )
                                    }
                                    if (isAdded && Global.getTotalWishListProductCount(myActivity!!) != -1) {
                                        if (Global.getTotalWishListProductCount(activity as AppCompatActivity) == 1) {
//                                            txtItems!!.text = resources.getString(
//                                                R.string.no_of_item_in_your_saves,
//                                                Global.getTotalWishListProductCount(myActivity!!).toString()
//                                            )
                                        } else {
//                                            txtItems!!.text = resources.getString(
//                                                R.string.no_of_item_s_in_your_saves,
//                                                Global.getTotalWishListProductCount(myActivity!!).toString()
//                                            )
                                        }
                                        linDivItems!!.visibility = View.VISIBLE
                                        relItems!!.visibility = View.VISIBLE
                                    } else {
//                                        txtItems!!.text = resources.getString(
//                                            R.string.no_of_item_in_your_saves,
//                                            "0"
//                                        )
                                        linDivItems!!.visibility = View.GONE
                                        relItems!!.visibility = View.GONE
                                    }

/*
                                    println(
                                        "Here wishlist count is " + Global.getTotalWishListProductCount(
                                            myActivity!!
                                        ).toString()
                                    )
*/
                                } else {
                                    // wish list is empty
                                    rvWishList!!.visibility = View.GONE
                                    linNoItems!!.visibility = View.VISIBLE
                                }

                            } else if (result.status == 404) {
                                //wish list is empty
                                //No products in the wishlist.
                                rvWishList!!.visibility = View.GONE
                                linNoItems!!.visibility = View.VISIBLE

                            } else {
                                rvWishList!!.visibility = View.GONE
                                linNoItems!!.visibility = View.VISIBLE
                                Global.showSnackbar(myActivity!!, result.message)
                            }
                        } else {
                            if (!isFromRefresh!!) {
                                hideProgressDialog()
                            }
                            Global.showSnackbar(myActivity!!, resources.getString(R.string.error))
                        }

//                                (activity as HomeActivity).updateWishlistBadge()
                    },
                    { error ->
                      //  println("ERROR - WISH LIST Ws :   " + error.localizedMessage)
                        if (!isFromRefresh!!) {
                            hideProgressDialog()
                        }
                        if (isAdded)
                            Global.showSnackbar(myActivity!!, resources.getString(R.string.error))
                        productsDBHelper.deleteTable("table_wishlist")
                    }
                )
        }
        isFromRefresh = false
    }

    private val onWishlistUpdateClicked =
        com.app.blockaat.wishlist.interfaces.UpdateWishlistItemEvent { data: ArrayList<WishListDataModel>, type: String ->
            if (adapterWishList != null) {
                arrListWishlistData.clear()
                arrListWishlistData.addAll(data)
                adapterWishList.notifyDataSetChanged()
                if (Global.getTotalWishListProductCount(myActivity!!) != -1) {
                    if (Global.getTotalWishListProductCount(activity as AppCompatActivity) == 1) {
//                        txtItems!!.text = resources.getString(
//                            R.string.no_of_item_in_your_saves,
//                            Global.getTotalWishListProductCount(myActivity!!).toString()
//                        )
                    } else {
//                        txtItems!!.text = resources.getString(
//                            R.string.no_of_item_s_in_your_saves,
//                            Global.getTotalWishListProductCount(myActivity!!).toString()
//                        )
                    }
                    linDivItems!!.visibility = View.VISIBLE
                    relItems!!.visibility = View.VISIBLE
                } else {
//                    txtItems!!.text = resources.getString(R.string.no_of_item_s_in_your_saves, "0")
                    linDivItems!!.visibility = View.GONE
                    relItems!!.visibility = View.GONE
                }


                if (arrListWishlistData != null && arrListWishlistData.size > 0) {
                    rvWishList!!.visibility = View.VISIBLE
                    linNoItems!!.visibility = View.GONE
                } else {
                    // wish list is empty
                    rvWishList!!.visibility = View.GONE
                    linNoItems!!.visibility = View.VISIBLE
                }
            } else {
                // wish list is empty
                rvWishList!!.visibility = View.GONE
                linNoItems!!.visibility = View.VISIBLE
            }
        }


    private fun showProgressDialog(activity: AppCompatActivity) {
        progressDialog = ProgressDialog(activity)
        progressDialog!!.setMessage(resources.getString(R.string.loading))
        progressDialog!!.show()
    }

    private fun hideProgressDialog() {
        if (progressDialog != null && progressDialog!!.isShowing) {
            progressDialog!!.dismiss()
        }
    }

    override fun onResume() {
        super.onResume()
        getWishList()

    }


    companion object {

        fun newInstance(): WishListFragment {
            return WishListFragment()
        }
    }
}