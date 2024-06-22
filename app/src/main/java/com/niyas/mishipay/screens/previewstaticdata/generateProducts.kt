package com.niyas.mishipay.screens.previewstaticdata

import com.niyas.mishipay.data.network.ProductData

object PreviewStaticData {
    fun generateStaticData(): List<ProductData> {
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
}
