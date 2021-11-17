package com.app.blockaat.orders

import android.content.Context
import android.content.Intent
import android.graphics.PorterDuff
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.blockaat.R
import com.app.blockaat.helper.BaseActivity
import com.app.blockaat.helper.Global
import com.app.blockaat.navigation.NavigationActivity
import com.app.blockaat.orders.adapter.OrderedItemsAdapter
import com.app.blockaat.orders.model.CheckoutItemCartModel
import com.app.blockaat.orders.model.CheckoutItemItemModel

import kotlinx.android.synthetic.main.activity_ordered_items_listing.*

class OrderedItemsListingActivity : BaseActivity() {
    var orderItemListing: CheckoutItemCartModel? = null
    private lateinit var adapterOrderedItemsListing: OrderedItemsAdapter
    private lateinit var arrListOrderedItems: ArrayList<CheckoutItemItemModel>
    private lateinit var layoutManager: LinearLayoutManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ordered_items_listing)

        Global.setLocale(this@OrderedItemsListingActivity )
        initializeToolbar()
        initializeFields()
        setFonts()
        onClickListeners()
    }

    private fun onClickListeners() {
        txtToolbarHeaderDone.setOnClickListener {
            val intent = Intent(this@OrderedItemsListingActivity, NavigationActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
            finish()
        }
    }

    private fun initializeToolbar() {
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowTitleEnabled(false)
        var upArrow = ContextCompat.getDrawable(this@OrderedItemsListingActivity, R.drawable.ic_back_arrow)
        if(!Global.isEnglishLanguage(this@OrderedItemsListingActivity))
        {
            upArrow = ContextCompat.getDrawable(this@OrderedItemsListingActivity, R.drawable.ic_back_arrow_ar)
        }
        upArrow?.setColorFilter(resources.getColor(R.color.black), PorterDuff.Mode.SRC_ATOP)
        upArrow?.setVisible(true, true)
        supportActionBar!!.setHomeAsUpIndicator(upArrow)
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true)
    }

    private fun setFonts() {

        txtToolbarHeader.typeface = Global.fontNavBar
        txtToolbarHeaderDone.typeface = Global.fontBold


    }

    private fun initializeFields() {
        layoutManager = LinearLayoutManager(this@OrderedItemsListingActivity)
        rvOrderedItemListing!!.layoutManager = layoutManager
        rvOrderedItemListing!!.isNestedScrollingEnabled = false

        if (intent.hasExtra("orderedItems")) {
            orderItemListing = intent.getSerializableExtra("orderedItems") as CheckoutItemCartModel
            arrListOrderedItems = orderItemListing!!.items!!
        } else {
            rvOrderedItemListing.visibility = View.GONE
            linNoItems.visibility = View.VISIBLE
        }

        txtToolbarHeader.text = resources.getString(R.string.items_ordered, orderItemListing!!.items!!.size.toString())

        adapterOrderedItemsListing = OrderedItemsAdapter(this@OrderedItemsListingActivity, arrListOrderedItems)
        rvOrderedItemListing!!.adapter = adapterOrderedItemsListing
    }

    //below is inbuilt function of android to manage activity toolbar arrow
    override fun onOptionsItemSelected(item: MenuItem): Boolean = when (item.itemId) {
        android.R.id.home -> {
            val view = this.currentFocus
            if (view != null) {
                val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(view.windowToken, 0)
            }
            this.finish()
            true
        }
        else -> super.onOptionsItemSelected(item)
    }
}
