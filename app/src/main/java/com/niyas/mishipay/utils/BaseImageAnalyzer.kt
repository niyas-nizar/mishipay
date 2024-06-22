package com.niyas.mishipay.utils

import android.graphics.Rect
import androidx.annotation.OptIn
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.google.android.gms.tasks.Task
import com.google.mlkit.vision.common.InputImage

abstract class BaseImageAnalyzer<T> : ImageAnalysis.Analyzer {

    @OptIn(ExperimentalGetImage::class)
    override fun analyze(imageProxy: ImageProxy) {

        val barcodeImage = imageProxy.image

        barcodeImage?.let {
            detectInputImage(InputImage.fromMediaImage(it, imageProxy.imageInfo.rotationDegrees))
                ?.addOnSuccessListener { result ->
                    onSuccess(result, it.cropRect)
                }
                ?.addOnFailureListener { exception ->
                    onFailure(exception)
                }
                ?.addOnCompleteListener {
                    imageProxy.close()
                }
        }
    }

    protected abstract fun detectInputImage(image: InputImage): Task<T>?

    protected abstract fun onSuccess(result: T?, rect: Rect)

    protected abstract fun onFailure(e: Exception)
}