package com.app.blockaat.productdetails.adapter

/*import kotlinx.android.synthetic.main.item_related_products.view.**/
import android.app.Activity
import android.content.Context
import android.graphics.drawable.Drawable
import android.opengl.Visibility
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.app.blockaat.R
import com.app.blockaat.helper.DBHelper
import com.app.blockaat.helper.Global
import com.app.blockaat.helper.Global.getDeviceWidth
import com.app.blockaat.helper.Global.setPriceWithCurrency
import com.app.blockaat.productdetails.interfaces.RelatedItemInterface
import com.app.blockaat.productdetails.model.ProductDetailRelatedModel
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import kotlinx.android.synthetic.main.item_product_list.view.*
import java.util.*

class RelatedProductsAdapter(
    private val dataList: ArrayList<ProductDetailRelatedModel>?,
    private val context: Context?,
    private val productsDBHelper: DBHelper,
    private val relatedItemInterface: RelatedItemInterface
) : RecyclerView.Adapter<RelatedProductsAdapter.ViewHolder>() {

    private var width = 0.0
    private var height = 0.0
    private var screenWidth = 0.0
    private var deviceMultiplier = 0.0
    /*init {
        val metrics = context?.resources!!.displayMetrics
        val screenWidth =  (metrics.widthPixels) - 3 * context?.resources.getDimension(R.dimen.ten_dp).toInt()
        width = screenWidth / 3
        height = screenWidth / 3
    }*/

    override fun getItemViewType(position: Int): Int {

        return position
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val a = dataList?.get(position)
        /* val lpMain = LinearLayout.LayoutParams(
             Global.getDimenValue(context as Activity, 153.00).toInt(),
             LinearLayout.LayoutParams.MATCH_PARENT
         )

         lpMain.marginEnd = context.resources.getDimension(R.dimen._5sdp).toInt()
         holder.itemView.linHead.layoutParams = lpMain

         val lpImage = LinearLayout.LayoutParams(
             Global.getDimenVallue(context as Activity, 153.00).toInt(),
             Global.getDimenVallue(context as Activity, 153.00).toInt()
         )
         holder.itemView.ivProduct.layoutParams = lpImage*/




        if (position > -1) {
            //set data
            holder.itemView.txtName.text = a?.name
            holder.itemView.txtBrandName.text = a?.brand_name

            /* if (!Global.isEnglishLanguage(context)) {
            holder.itemView.ivDiscount.rotation = 180f
        }*/
            /*  if (context != null) {
           Global.loadImagesUsingGlide(context, a?.image, holder.itemView.ivProduct)
        }*/
            deviceMultiplier =
                getDeviceWidth(context as Activity) / java.lang.Double.valueOf(320.0)

            screenWidth =
                getDeviceWidth(context as Activity) - 30 * deviceMultiplier
            width = screenWidth / 2
            height = screenWidth / 2
            if (context != null) {
                Glide.with(context)
                    .load(a!!.image)
                    .override(width.toInt(), height.toInt())
                    .listener(object : RequestListener<Drawable?> {
                        override fun onLoadFailed(
                            e: GlideException?,
                            model: Any,
                            target: Target<Drawable?>,
                            isFirstResource: Boolean
                        ): Boolean {
                            return false
                        }

                        override fun onResourceReady(
                            resource: Drawable?,
                            model: Any,
                            target: Target<Drawable?>,
                            dataSource: DataSource,
                            isFirstResource: Boolean
                        ): Boolean {
                            return false
                        }
                    })
                    .transition(DrawableTransitionOptions.withCrossFade(800))
                    .into(holder.itemView.ivProduct)
            }

            //discount and prices
            holder.itemView.txtFinalPrice.text = setPriceWithCurrency(
                context as Activity,
                a?.final_price ?: ""
            )
            if (a?.is_saleable == 0) {
                holder.itemView.txtSoldOut.visibility = View.VISIBLE
            } else {
                holder.itemView.txtSoldOut.visibility = View.GONE
            }
/*
        holder.itemView.txtRegularPrice.paintFlags =
            holder.itemView.txtRegularPrice.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
        if (a?.item_in_wishlist == 1) {
            holder.itemView.ivWishList.setImageResource(R.drawable.ic_wishlist_selected)
        } else {
            holder.itemView.ivWishList.setImageResource(R.drawable.ic_wishlist_unselected)
        }
        if (a?.final_price != a?.regular_price) {
            holder.itemView.txtRegularPrice.text = setOnlyPrice(
                context,
                a?.regular_price ?: ""
            )
            holder.itemView.txtRegularPrice.visibility = View.VISIBLE
            holder.itemView.relDiscount.visibility = View.VISIBLE
            holder.itemView.txtDiscount.text = Global.getDiscountedPrice(
                a?.final_price ?: "",
                a?.regular_price ?: ""
            ).toString() + "%"
        } else {
            holder.itemView.txtRegularPrice.visibility = View.GONE
            holder.itemView.relDiscount.visibility = View.GONE
        }

        //set button text
        if (a?.product_type.toString().toLowerCase() == "simple") {
            if (a?.is_saleable == 1) {
                holder.itemView.txtAddCart.text = context.resources.getString(R.string.add_to_cart)
            } else {
                holder.itemView.txtAddCart.text = context.resources.getString(R.string.notify_me)
            }
        } else {
            holder.itemView.txtAddCart.text = context.resources.getString(R.string.view_more)
        }
*/
            //set fonts

            holder.itemView.txtBrandName.typeface = Global.fontNavBar
            holder.itemView.txtName.typeface = Global.fontRegular
            /*  holder.itemView.txtAddCart.typeface = Global.fontSemiBold
           holder.itemView.txtFinalPrice.typeface = Global.fontPriceMedium*/
            holder.itemView.txtFinalPrice.typeface = Global.fontNavBar

            //set click listener
            holder.itemView.linHead.setOnClickListener {
                relatedItemInterface.onItemClick(position, "openProduct")
            }
            holder.itemView.ivWishlist.setOnClickListener {
                relatedItemInterface.onItemClick(position, "addOrRemoveWishlist")
            }

            //change wishList icon color
            if (a?.item_in_wishlist == 1) {
                holder.itemView.ivWishlist.setImageResource(R.drawable.ic_wishlist_selected)
                holder.itemView.ivWishlist.setColorFilter(R.color.gray)
            } else {
                holder.itemView.ivWishlist.setImageResource(R.drawable.ic_wishlist_unselected)
                //holder.itemView.ivWishlist.setColorFilter(R.color.tab_select)
            }

/*        holder.itemView.txtAddCart.setOnClickListener {
            if (a?.product_type.toString().toLowerCase() == "simple") {
                if (a?.is_saleable == 1) {
                    relatedItemInterface.onItemClick(position, "addToCart")
                } else {
                    relatedItemInterface.onItemClick(position, "notifyMe")
                }
            } else {
                relatedItemInterface.onItemClick(position, "openProduct")
            }
        }*/
        }

    }

    override fun getItemCount(): Int = dataList?.count() ?: 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        /*val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_related_products, parent, false)*/
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_product_list, parent, false)
        return ViewHolder(view, parent.context)
    }

    class ViewHolder(view: View, val activity: Context) : RecyclerView.ViewHolder(view)
}