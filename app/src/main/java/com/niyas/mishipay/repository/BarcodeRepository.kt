package com.niyas.mishipay.repository

import com.niyas.mishipay.data.network.BarcodeData

class BarcodeRepository {

    val availableProducts = generateProducts()

    private fun generateProducts(): List<BarcodeData> {
        return listOf(
            BarcodeData(
                imageUrl = "https://example.com/image1.jpg",
                price = "100",
                title = "Mobile",
                description = "Latest smartphone with amazing features",
                category = "Electronics",
                id = "12345"
            ),
            BarcodeData(
                imageUrl = "https://example.com/image2.jpg",
                price = "50",
                title = "Headphones",
                description = "High-quality over-ear headphones",
                category = "Electronics",
                id = "12346"
            ),
            BarcodeData(
                imageUrl = "https://example.com/image3.jpg",
                price = "30",
                title = "Mouse",
                description = "Wireless mouse with ergonomic design",
                category = "Accessories",
                id = "12347"
            ),
            BarcodeData(
                imageUrl = "https://example.com/image4.jpg",
                price = "150",
                title = "Smartwatch",
                description = "Smartwatch with health tracking features",
                category = "Wearables",
                id = "12348"
            ),
            BarcodeData(
                imageUrl = "https://example.com/image5.jpg",
                price = "200",
                title = "Tablet",
                description = "Tablet with high-resolution display",
                category = "Electronics",
                id = "12349"
            ),
            BarcodeData(
                imageUrl = "https://example.com/image6.jpg",
                price = "70",
                title = "Keyboard",
                description = "Mechanical keyboard with RGB lighting",
                category = "Accessories",
                id = "12350"
            )
        )
    }

    fun findProduct(id: String) = availableProducts.firstOrNull { it.id == id }
}