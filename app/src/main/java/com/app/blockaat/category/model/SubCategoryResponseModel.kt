package com.app.blockaat.category.model

import android.os.Parcelable
import com.app.blockaat.productlisting.model.ProductListingDataModel
import com.app.blockaat.productlisting.model.ProductListingFilterModel
import com.app.blockaat.productlisting.model.ProductListingProductModel
import com.app.blockaat.productlisting.model.Tvs
import kotlinx.android.parcel.Parcelize
import java.io.Serializable

data class SubCategoryResponseModel(
      val `data`: SubCategoryData? = null,
    //val `data`: ArrayList<SubcategoryNew>? = null,
    val message: String? = null,
    val status: Int? = 0,
    val success: Boolean? = false
)

data class SubCategoryData(
    val name: String? = null,
    val category_products: ProductListingDataModel? = null,
    val category_tvs: ArrayList<Tvs?>? = null,
    val subcategories: ArrayList<Subcategory?>? = null

)

data class SubCategoryProducts(
    val filter: List<ProductListingFilterModel?>? = null,
    val max_product_price: String? = null,
    val products: List<ProductListingProductModel?>? = null,
    val total_pages: Int? = 0,
    val name: String? = null,
    val total_products: String? = null
)

@Parcelize
data class SubcategoryNew(
    val has_subcategory: String? = null,
    val icon: String? = null,
    val id: Int? = null,
    val image: String? = null,
    val meta_description: String? = null,
    val name: String? = null,
    val subcategories: ArrayList<Subcategory>? = null,
    val filter: List<ProductListingFilterModel?>? = null,
    val max_product_price: String? = null,
    val products: List<ProductListingProductModel?>? = null,
    val total_pages: Int? = 0,
    val total_products: String? = null
): Serializable, Parcelable





