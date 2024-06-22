package com.niyas.mishipay.utils.barcodedetectionprocessor

import android.graphics.Rect
import android.util.Log
import com.google.android.gms.tasks.Task
import com.google.mlkit.vision.barcode.BarcodeScanner
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage
import com.niyas.mishipay.utils.BaseImageAnalyzer

class BarcodeDetectionProcessor(private val detectionListener: DetectionListener) :
    BaseImageAnalyzer<List<Barcode>>() {

    private var scanner: BarcodeScanner? = null

    private val options = BarcodeScannerOptions.Builder()
        .setBarcodeFormats(
            Barcode.FORMAT_QR_CODE,
            Barcode.FORMAT_AZTEC
        )
        .build()

    fun startProcessor() {
        scanner = BarcodeScanning.getClient(options)
    }

    fun stopProcessor() {
        scanner?.close()
        scanner = null
    }

    override fun detectInputImage(image: InputImage): Task<List<Barcode>>? =
        scanner?.process(image)

    override fun onFailure(e: Exception) {
        Log.e("Barcode Detection Processor", "Barcode Recognition failed. ${e.localizedMessage}")
        detectionListener.onDetectionFailure(e.localizedMessage)
    }

    override fun onSuccess(result: List<Barcode>?, rect: Rect) {
        result?.first()?.let { detectionListener.onDetectionSuccess(it) }
    }
}

interface DetectionListener {
    fun onDetectionSuccess(barcode: Barcode)
    fun onDetectionFailure(failureMessage: String? = null)
}