package com.app.blockaat.productlisting.interfaces

import com.app.blockaat.productlisting.model.ProductListingProductModel

interface OnProductListListener {
   fun onProductClicked(product: ProductListingProductModel)
   fun onProductClicked(product: ProductListingProductModel, type: String)
}