package com.niyas.mishipay.screens

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.niyas.mishipay.data.BarcodeDetectionProcessorStatus
import com.niyas.mishipay.data.network.BarcodeData
import com.niyas.mishipay.repository.BarcodeRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class BarcodeViewModel(
    private val repository: BarcodeRepository = BarcodeRepository()
) : ViewModel() {

    fun getProductById(id: String, productDetails: (BarcodeData?) -> Unit) =
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

}