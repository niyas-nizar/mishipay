package com.niyas.mishipay.data.network

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class BarcodeData(
    val imageUrl: String,
    val price: String,
    val title: String,
    val description: String,
    val category: String,
    val id: String
) :Parcelable