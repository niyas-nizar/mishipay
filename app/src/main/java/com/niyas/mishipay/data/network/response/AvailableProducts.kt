package com.niyas.mishipay.data.network.response

import com.niyas.mishipay.data.network.ProductData

object AvailableProducts {

    fun generateProducts(): List<ProductData> {
        return listOf(
            ProductData(
                price = "79999",
                title = "Apple iPhone 15",
                description = "Apple iPhone 15 with 128GB storage, A15 Bionic chip, 6.1-inch Super Retina XDR display, and dual-camera system.",
                category = "Electronics",
                id = "10001"
            ),
            ProductData(
                price = "5999",
                title = "Sony WH-1000XM4",
                description = "Sony WH-1000XM4 Wireless Noise Cancelling Over-Ear Headphones with mic for phone-call, 30 hours battery life, and Alexa voice control.",
                category = "Electronics",
                id = "10002"
            ),
            ProductData(
                price = "1499",
                title = "Logitech M235",
                description = "Logitech M235 Wireless Mouse with 12-month battery life, compact design, and advanced optical tracking.",
                category = "Accessories",
                id = "10003"
            ),
            ProductData(
                price = "24999",
                title = "Samsung Galaxy Watch 5",
                description = "Samsung Galaxy Watch 5 with 1.4-inch AMOLED display, GPS, heart rate monitoring, and 50m water resistance.",
                category = "Wearables",
                id = "10004"
            ),
            ProductData(
                price = "54999",
                title = "Apple iPad Air",
                description = "Apple iPad Air with 10.9-inch Liquid Retina display, A14 Bionic chip, 64GB storage, and 12MP back camera.",
                category = "Electronics",
                id = "10005"
            ),
            ProductData(
                price = "4999",
                title = "Corsair K95 RGB Platinum",
                description = "Corsair K95 RGB Platinum Mechanical Gaming Keyboard with Cherry MX Speed switches, RGB backlighting, and 6 programmable macro keys.",
                category = "Accessories",
                id = "10006"
            ),
            ProductData(
                price = "7999",
                title = "Amazon Echo Show 8",
                description = "Amazon Echo Show 8 with 8-inch HD screen, Alexa, and stereo sound for smart home control and entertainment.",
                category = "Electronics",
                id = "10007"
            ),
            ProductData(
                price = "15999",
                title = "Bose SoundLink Revolve",
                description = "Bose SoundLink Revolve Portable Bluetooth 360 Speaker with deep, loud, immersive sound, and up to 12 hours of play time.",
                category = "Electronics",
                id = "10008"
            ),
            ProductData(
                price = "3499",
                title = "SanDisk Extreme Pro 1TB SSD",
                description = "SanDisk Extreme Pro 1TB SSD with up to 2000MB/s read and write speeds, and rugged design for durability.",
                category = "Accessories",
                id = "10009"
            ),
            ProductData(
                price = "29999",
                title = "Dell UltraSharp 27 Monitor",
                description = "Dell UltraSharp 27 4K USB-C Monitor with 27-inch 4K UHD resolution, 99% sRGB coverage, and USB-C connectivity.",
                category = "Electronics",
                id = "10010"
            )
        )
    }

}