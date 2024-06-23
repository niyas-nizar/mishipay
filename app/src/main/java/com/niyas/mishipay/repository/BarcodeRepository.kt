package com.niyas.mishipay.repository

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import com.niyas.mishipay.data.network.ProductData
import com.niyas.mishipay.data.network.response.AvailableProducts

class BarcodeRepository {

    private val availableProducts = AvailableProducts.generateProducts()
    private val productsInCart: SnapshotStateList<ProductData> = mutableStateListOf()

    fun findProduct(id: String) = availableProducts.firstOrNull { it.id == id }

    fun upsertProductToCart(product: ProductData) {
        val existingProduct = productsInCart.find { it.id == product.id }
        if (existingProduct != null) {
            existingProduct.quantity++
        } else {
            productsInCart.add(product.copy())
        }
    }

    fun getProductsFromCart() = productsInCart

    /**
     *[removeAllInstancesOfProduct] is called when we need to remove the whole product
     *  from the cart
     **/
    fun removeAllInstancesOfProduct(product: ProductData): SnapshotStateList<ProductData> {
        productsInCart.removeAll {
            it.id == product.id
        }
        return productsInCart
    }

    /**
     *[removeSingleInstanceOfProduct] is called when we need to remove only one instance
     *  of the product from the cart
     **/
    fun removeSingleInstanceOfProduct(product: ProductData): SnapshotStateList<ProductData> {
        val existingProduct = productsInCart.find { it.id == product.id }
        if (existingProduct != null && existingProduct.quantity > 0) {
            existingProduct.quantity--
            if (existingProduct.quantity == 0) {
                productsInCart.remove(product.copy(quantity = 0))
            }
        }
        return productsInCart
    }

    fun getTotalAmountToPay() =
        productsInCart.sumOf { it.price * it.quantity }

}