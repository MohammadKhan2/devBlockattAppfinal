package com.app.blockaat.helper

class ProductsDataModel {
    var product_id: String? = ""
    var entity_id: String? = ""
    var name: String? = ""
    var brand: String? = ""
    var image: String? = ""
    var description: String? = ""
    var quantity: String? = ""
    var final_price: String? = ""
    var regular_price: String? = ""
    var sku: String? = ""
    var remaining_quantity: String? = ""
    var is_featured: String? = ""
    var is_saleable: String? = ""
    var type: String? = ""
    var sizeValue: String? = ""
    var colorValue: String? = ""
    var order_item_id: String? = ""

    constructor(product_id: String?, entity_id: String?, name: String?, brand: String?, image: String?, description: String?,
                quantity: String?, final_price: String?, regular_price: String?, sku: String?, remaining_quantity: String?,
                is_featured: String?, is_saleable: String?, type: String?, sizeValue: String?, colorValue: String?,order_item_id: String?="") {
        this.product_id = product_id
        this.entity_id = entity_id
        this.name = name
        this.brand = brand
        this.image = image
        this.description = description
        this.quantity = quantity
        this.final_price = final_price
        this.regular_price = regular_price
        this.sku = sku
        this.remaining_quantity = remaining_quantity
        this.is_featured = is_featured
        this.is_saleable = is_saleable
        this.type = type
        this.sizeValue = sizeValue
        this.colorValue = colorValue
        this.order_item_id = order_item_id
    }

    constructor(product_id: String?) {
        this.product_id = product_id
    }
}