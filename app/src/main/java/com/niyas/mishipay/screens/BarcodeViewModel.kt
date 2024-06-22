package com.niyas.mishipay.screens

import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.niyas.mishipay.data.BarcodeDetectionProcessorStatus
import com.niyas.mishipay.data.network.ProductData
import com.niyas.mishipay.repository.BarcodeRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class BarcodeViewModel(
    private val repository: BarcodeRepository = BarcodeRepository()
) : ViewModel() {

    private var _cartItems = MutableLiveData<SnapshotStateList<ProductData>>()
    val cartItems: LiveData<SnapshotStateList<ProductData>> = _cartItems

    fun getProductById(id: String, productDetails: (ProductData?) -> Unit) =
        viewModelScope.launch(Dispatchers.IO) {
            val product = repository.findProduct(id)
            withContext(Dispatchers.Main) {
                productDetails(product)
            }
        }

    private var _barcodeDetectionProcessorStatus: MutableLiveData<BarcodeDetectionProcessorStatus> =
        MutableLiveData(null)
    val barcodeDetectionProcessorStatus: LiveData<BarcodeDetectionProcessorStatus> =
        _barcodeDetectionProcessorStatus

    fun updateBarcodeDetectionProcessorStatus(status: BarcodeDetectionProcessorStatus) {
        _barcodeDetectionProcessorStatus.value = status
    }

    fun addProductToCart(product: ProductData) =
        viewModelScope.launch(Dispatchers.IO) {
            repository.upsertProductToCart(product)
        }

    fun getProductsFromCart() {
        viewModelScope.launch {
            val products = repository.getProductsFromCart()
            _cartItems.value = products
        }
    }

    fun removeSameProductsFromCart(productData: ProductData, cartIsEmpty: (Boolean) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            val updatedProducts = repository.removeSameProductsFromCart(productData)
            withContext(Dispatchers.Main) {
                _cartItems.value = updatedProducts
                cartIsEmpty(updatedProducts.isEmpty())
            }
        }
    }

    fun removeProductFromCart(productData: ProductData, cartIsEmpty: (Boolean) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            val updatedProducts = repository.removeProductFromCart(productData)
            withContext(Dispatchers.Main) {
                _cartItems.value = updatedProducts
                cartIsEmpty(updatedProducts.isEmpty())
            }
        }
    }

}