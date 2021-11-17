package com.app.blockaat.cart.adapter

import android.content.Context
import android.content.Intent
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.app.blockaat.R
import com.app.blockaat.helper.AlertDialogInterface
import com.app.blockaat.helper.DBHelper
import com.app.blockaat.helper.Global
import com.app.blockaat.helper.ProductsDataModel
import com.app.blockaat.login.LoginActivity
import kotlinx.android.synthetic.main.lv_item_cart.view.*


class OfflineCartDataAdapter(
    private val arrListCartList: ArrayList<ProductsDataModel>,
    private var onCartUpdateClicked: com.app.blockaat.cart.interfaces.UpdateCartItemEvent,
    private var productsDBHelper: DBHelper
) : RecyclerView.Adapter<OfflineCartDataAdapter.ViewHolder>() {

    override fun onBindViewHolder(holder: ViewHolder, position: Int) =
        holder.bind(arrListCartList, position, onCartUpdateClicked, productsDBHelper)

    override fun getItemCount(): Int = arrListCartList.count()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.lv_item_cart, parent, false)
        return ViewHolder(view, parent.context)
    }

    class ViewHolder(view: View, val activity: Context) : RecyclerView.ViewHolder(view) {
        fun bind(
            arrListCartList: ArrayList<ProductsDataModel>,
            position: Int,
            onCartUpdateClicked: com.app.blockaat.cart.interfaces.UpdateCartItemEvent,
            productsDBHelper: DBHelper
        ) {
            val a = arrListCartList[position]


            //set data
            itemView.txtProductName.text = a.name?.trim().toString()

            itemView.txtFinalPrice.text =
                Global.setPriceWithCurrency(activity as AppCompatActivity, a.final_price.toString())
            Glide.with(activity)
                .load(a.image)
                .override(1000, 1000)
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
                .into(itemView.ivProduct)
            itemView.txtQty.text = a.quantity.toString()

            if (a.quantity?.toInt() == 1) {
                itemView.ivMinus.setColorFilter(activity.resources.getColor(R.color.icon_color))
            } else {
                itemView.ivMinus.setColorFilter(activity.resources.getColor(R.color.primary_color))
            }


            if (a?.sizeValue!!.isNotEmpty()) {
                itemView.linAttributeOne.visibility = View.VISIBLE
                itemView.txtAttr1.text = activity.resources.getString(R.string.size_colon)
                itemView.txtAttr1Value.text = a.sizeValue
            } else {
                itemView.linAttributeOne.visibility = View.GONE
            }
            if (a.colorValue!!.isNotEmpty()) {
                itemView.linAttributeTwo.visibility = View.VISIBLE
                itemView.txtAttr2.text = activity.resources.getString(R.string.color_colon)
                itemView.txtAttr2Value.text = a.colorValue
            } else {
                itemView.linAttributeTwo.visibility = View.GONE
            }


            //set fonts
            itemView.txtProductName.typeface = Global.fontRegular
            itemView.txtFinalPrice.typeface = Global.fontMedium
            itemView.txtAttr1.typeface = Global.fontRegular
            itemView.txtAttr2.typeface = Global.fontRegular
            itemView.txtAttr1Value.typeface = Global.fontMedium
            itemView.txtRemove.typeface = Global.fontMedium
            itemView.txtAttr2Value.typeface = Global.fontMedium
            itemView.txtMoveToWishList.typeface = Global.fontMedium
            itemView.txtQty.typeface = Global.fontMedium

            //set click listeners
            itemView.cvMainHolder.setOnClickListener {

            }



            itemView.linRemove.setOnClickListener {
                Global.showAlert(
                    activity,
                    "",
                    activity.resources.getString(R.string.delete_item_desc),
                    activity.resources.getString(R.string.yes),
                    activity.resources.getString(R.string.no),
                    false,
                    R.drawable.ic_alert,
                    object : AlertDialogInterface {
                        override fun onYesClick() {
                            productsDBHelper.deleteProductFromCart(
                                a.product_id.toString(),
                                a.entity_id.toString()
                            )
                            onCartUpdateClicked.onCartUpdateClicked(null, "offline")
                        }

                        override fun onNoClick() {
                        }

                    })

            }

            itemView.ivPlus.setOnClickListener {
                //if ((a?.remaining_quantity ?: 0).toString() > (itemView.txtQty.text.toString())) {
                if ((itemView.txtQty.text.toString()) > "0") {
                    val x = itemView.txtQty.text.toString().toInt() + 1
                    itemView.txtQty.text = x.toString()
                    productsDBHelper.updateProductsInCart(x.toString(), a.product_id, a.entity_id)
                    onCartUpdateClicked.onCartUpdateClicked(null, "offline")
                } else {
                    //no more qty available
                    //println("Remaining qty is reached")
                }

            }

            itemView.ivMinus.setOnClickListener {
                if (itemView.txtQty.text.toString().toInt() > 1) {
                    val x = itemView.txtQty.text.toString().toInt() - 1
                    itemView.txtQty.text = x.toString()
                    productsDBHelper.updateProductsInCart(x.toString(), a.product_id, a.entity_id)
                    onCartUpdateClicked.onCartUpdateClicked(null, "offline")
                }
            }

            itemView.linWishlist.setOnClickListener {

                Global.showAlert(
                    activity,
                    "",
                    activity.resources.getString(R.string.move_to_wishlist_desc),
                    activity.resources.getString(R.string.move_to_wishlist),
                    activity.resources.getString(R.string.no_thanks),
                    false,
                    R.drawable.ic_alert,
                    object : AlertDialogInterface {
                        override fun onYesClick() {
                            val i = Intent(activity, LoginActivity::class.java)
                            activity.startActivityForResult(i, 1)
                        }

                        override fun onNoClick() {
                        }

                    })

            }

        }
    }
}
