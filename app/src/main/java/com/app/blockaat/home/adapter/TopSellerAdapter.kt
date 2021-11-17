package com.app.blockaat.home.adapter

import android.annotation.SuppressLint
import android.app.Activity
import androidx.appcompat.app.AppCompatActivity
import android.content.Context
import android.content.Intent
import android.graphics.Paint
import android.graphics.drawable.Drawable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.app.blockaat.R
import com.app.blockaat.cart.model.AddCartRequestModel
import com.app.blockaat.helper.Constants
import com.app.blockaat.helper.DBHelper
import com.app.blockaat.helper.Global
import com.app.blockaat.home.model.BestSellers
import com.app.blockaat.login.LoginActivity
import com.app.blockaat.productdetails.ProductDetailsActivity
import kotlinx.android.synthetic.main.item_home_product.view.*
import java.util.ArrayList

class TopSellerAdapter(
    private val dataList: ArrayList<BestSellers?>,
    private val context: AppCompatActivity?
) :
    RecyclerView.Adapter<TopSellerAdapter.ViewHolder>() {

    private var width: Int = 0
    private var height: Int = 0
    private var productsDBHelper: DBHelper

    init {
        productsDBHelper = DBHelper(context!!)
        val metrics = context.resources!!.displayMetrics
        val screenWidth =
            (metrics.widthPixels - (2 * context?.resources.getDimension(R.dimen.ten_dp).toInt())) / 2
        width = screenWidth
        height = screenWidth
    }

    override fun getItemViewType(position: Int): Int {

        return position
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {


        val a = dataList[position]
        /* val params = RelativeLayout.LayoutParams(width, height)
         holder.itemView.ivProduct.layoutParams = params

         val paramsMainHolder =
             RelativeLayout.LayoutParams(width, ViewGroup.LayoutParams.WRAP_CONTENT)
         if (position == 0) {
             paramsMainHolder.marginStart =
                 context?.resources?.getDimensionPixelSize(R.dimen.ten_dp) as Int
         }
         paramsMainHolder.marginEnd = context?.resources?.getDimensionPixelSize(R.dimen.ten_dp) as Int
         paramsMainHolder.topMargin = context.resources?.getDimensionPixelSize(R.dimen.ten_dp) as Int


         holder.itemView.linMainHolder.layoutParams = paramsMainHolder*/

        val lpMain = LinearLayout.LayoutParams(
            Global.getDimenVallue(context as Activity, 153.00).toInt(),
            LinearLayout.LayoutParams.MATCH_PARENT
        )

        lpMain.marginEnd = context.resources.getDimension(R.dimen._5sdp).toInt()
        holder.itemView.linMainHolder.layoutParams = lpMain

        val lpImage = LinearLayout.LayoutParams(
            Global.getDimenVallue(context as Activity, 153.00).toInt(),
            Global.getDimenVallue(context as Activity, 153.00).toInt()
        )
        holder.itemView.ivProduct.layoutParams = lpImage

        if (!Global.isEnglishLanguage(context)) {
            holder.itemView.ivDiscount.rotation=180f
        }
        holder.itemView.txtName.typeface = Global.fontRegular
        holder.itemView.txtBrandName.typeface = Global.fontBold
        holder.itemView.txtFinalPrice.typeface = Global.fontPriceMedium
        holder.itemView.txtRegularPrice.typeface = Global.fontPriceRegular
        holder.itemView.txtDiscount.typeface = Global.fontRegular
        holder.itemView.txtAddCart.typeface = Global.fontBtn
        holder.itemView.txtName.text = dataList[position]!!.name
        holder.itemView.txtBrandName.text = dataList[position]!!.brand_name
        holder.itemView.txtFinalPrice.text =
            Global.setPriceWithCurrency(context!!, dataList[position]?.final_price.toString())
        if (a?.type.toString().toLowerCase() == "simple") {
            if (a?.is_saleable == 1) {
                holder.itemView.txtAddCart.text = context.resources.getString(R.string.add_to_cart)
            } else {
                holder.itemView.txtAddCart.text = context.resources.getString(R.string.notify_me)
            }
        } else {
            holder.itemView.txtAddCart.text = context.resources.getString(R.string.view_more)
        }

        /*  if (dataList[position]!!.is_saleable == 1) {
              if (dataList[position]?.type == "Simple") {
                  holder.itemView.txtAddCart.text =
                      context!!.resources.getString(R.string.add_to_cart)
              } else {
                  holder.itemView.txtAddCart.text = context.resources.getString(R.string.view_more)
              }
          } else {
              holder.itemView.txtAddCart.text = context.resources.getString(R.string.sold_out)
          }*/

//        holder.itemView.togWishlist.isChecked = dataList[position]?.item_in_wishlist == 1
//        holder.itemView.togWishlist.isChecked =
//            productsDBHelper.isProductPresentInWishlist(dataList[position]?.id!!.toString())

        if (dataList[position]?.final_price != dataList[position]!!.regular_price) {
            holder.itemView.txtRegularPrice.visibility = View.VISIBLE
            holder.itemView.relDiscount.visibility = View.VISIBLE
            holder.itemView.txtDiscount.text = Global.getDiscountedPrice(
                dataList[position]?.final_price.toString(),
                dataList[position]?.regular_price.toString()
            ).toString() + "%"

            holder.itemView.txtRegularPrice.text =
                Global.setPriceWithCurrency(context, dataList[position]?.regular_price.toString())
            holder.itemView.txtRegularPrice.paintFlags =
                holder.itemView.txtRegularPrice.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
        } else {
            holder.itemView.relDiscount.visibility = View.GONE
            holder.itemView.txtRegularPrice.visibility = View.GONE
        }

        if (context != null) {

            Glide.with(context)
                .load(dataList[position]?.image)
                .override(width, height)
                .listener(object : RequestListener<Drawable> {
                    override fun onResourceReady(
                        resource: Drawable?,
                        model: Any?,
                        target: Target<Drawable>?,
                        dataSource: DataSource?,
                        isFirstResource: Boolean
                    ): Boolean {

                        return false
                    }

                    override fun onLoadFailed(
                        e: GlideException?,
                        model: Any?,
                        target: Target<Drawable>?,
                        isFirstResource: Boolean
                    ): Boolean {
                        return false
                    }
                })
                .transition(DrawableTransitionOptions.withCrossFade(800))
                .into(holder.itemView.ivProduct)

        }

        holder.itemView.ivWishList.setOnClickListener {

            if (Global.getStringFromSharedPref(
                    context,
                    Constants.PREFS_isUSER_LOGGED_IN
                ) == "yes"
            ) {
                if (dataList[position]!!.item_in_wishlist == 1) {
                    Global.deleteFromWishlist(
                        context,
                        dataList[position]?.id.toString(),
                        productsDBHelper
                    )
                    dataList[position]!!.item_in_wishlist = 0
                } else {
                    Global.addToWishlist(
                        context,
                        dataList[position]?.id.toString(),
                        productsDBHelper
                    )
                    dataList[position]!!.item_in_wishlist = 1
                }

            } else {
                val i = Intent(context, LoginActivity::class.java)
                i.putExtra("isFromProducts", "yes")
                context.startActivityForResult(i, 1)
            }
        }

        holder.itemView.txtAddCart.setOnClickListener {
            if (a?.type.toString().toLowerCase() == "simple") {
                if (a?.is_saleable == 1) {
                    val model = AddCartRequestModel(
                        dataList[position]?.id.toString(),
                        dataList[position]?.id.toString(),
                        dataList[position]?.name,
                        dataList[position]?.SKU,
                        dataList[position]?.regular_price,
                        dataList[position]?.final_price,
                        dataList[position]?.is_saleable,
                        dataList[position]?.brand_name,
                        dataList[position]?.image,
                        dataList[position]?.type,
                        dataList[position]?.brand_id,
                        dataList[position]?.influencer_id,
                        dataList[position]?.video_id
                    )

                    if (Global.getStringFromSharedPref(
                            context,
                            Constants.PREFS_isUSER_LOGGED_IN
                        ) == "yes"
                    ) {
                        Global.addToCartOnline(context, model)
                    } else {
                        Global.addToCartOffline(context, model)
                    }

                } else {
                    Global.notifyMe(context, a?.id.toString())
                }

            } else {
                holder.itemView.linMainHolder.performClick()
            }
        }

        holder.itemView.linMainHolder.setOnClickListener {
            val intent = Intent(context, ProductDetailsActivity::class.java)
            intent.putExtra("strProductID", dataList[position]?.id.toString())
            intent.putExtra("strProductName", dataList[position]?.name.toString())
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int = dataList.count()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_home_product, parent, false)

        return ViewHolder(view, parent.context)
    }

    class ViewHolder(view: View, val activity: Context) : RecyclerView.ViewHolder(view)
}