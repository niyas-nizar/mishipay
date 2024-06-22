package com.niyas.mishipay.data.network

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlin.random.Random

@Parcelize
data class ProductData(
    val imageUrl: String = "https://picsum.photos/200?random=${Random.nextInt(10)}",
    val price: String,
    val title: String,
    val description: String,
    val category: String,
    val id: String,
    var quantity: Int = 1
) :Parcelable