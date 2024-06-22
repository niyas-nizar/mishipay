package com.niyas.mishipay.repository

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import com.niyas.mishipay.data.network.ProductData

class BarcodeRepository {

    private val availableProducts = generateProducts()
    private val productsInCart: SnapshotStateList<ProductData> = mutableStateListOf()

    private fun generateProducts(): List<ProductData> {
        return listOf(
            ProductData(
                price = "100",
                title = "Mobile",
                description = "Latest smartphone with amazing features",
                category = "Electronics",
                id = "12345"
            ),
            ProductData(
                price = "50",
                title = "Headphones",
                description = "High-quality over-ear headphones",
                category = "Electronics",
                id = "12346"
            ),
            ProductData(
                price = "30",
                title = "Mouse",
                description = "Wireless mouse with ergonomic design",
                category = "Accessories",
                id = "12347"
            ),
            ProductData(
                price = "150",
                title = "Smartwatch",
                description = "Smartwatch with health tracking features",
                category = "Wearables",
                id = "12348"
            ),
            ProductData(
                price = "200",
                title = "Tablet",
                description = "Tablet with high-resolution display",
                category = "Electronics",
                id = "12349"
            ),
            ProductData(
                price = "70",
                title = "Keyboard",
                description = "Mechanical keyboard with RGB lighting",
                category = "Accessories",
                id = "12350"
            )
        )
    }

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

    fun removeSameProductsFromCart(product: ProductData): SnapshotStateList<ProductData> {
        productsInCart.removeAll {
            it.id == product.id
        }
        return productsInCart
    }

    fun removeProductFromCart(product: ProductData): SnapshotStateList<ProductData> {
        val existingProduct = productsInCart.find { it.id == product.id }
        if (existingProduct != null && existingProduct.quantity > 0) {
            existingProduct.quantity--
            if (existingProduct.quantity == 0) {
                productsInCart.remove(product)
            }
        }
        return productsInCart
    }

    fun getTotalAmountToPay() =
        productsInCart.sumOf { it.price.toInt() * it.quantity }

}