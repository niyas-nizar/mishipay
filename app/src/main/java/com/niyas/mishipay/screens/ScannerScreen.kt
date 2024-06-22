package com.niyas.mishipay.screens

import android.Manifest
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.os.Handler
import android.os.Looper
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.google.mlkit.vision.barcode.common.Barcode
import com.niyas.mishipay.data.BarcodeDetectionProcessorStatus
import com.niyas.mishipay.data.BarcodeDetectionProcessorStatus.START
import com.niyas.mishipay.data.BarcodeDetectionProcessorStatus.STOP
import com.niyas.mishipay.utils.barcodedetectionprocessor.BarcodeDetectionProcessor
import com.niyas.mishipay.utils.barcodedetectionprocessor.DetectionListener
import java.util.concurrent.Executors

@Composable
fun ScannerScreen(viewModel: BarcodeViewModel) {

    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    var barcodeProcessor: BarcodeDetectionProcessor? = null

    var hasCameraPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = {
            hasCameraPermission = it
        })

    var showNoProductFoundView by remember {
        mutableStateOf(false)
    }

    viewModel.barcodeDetectionProcessorStatus.observe(lifecycleOwner) { status ->
        when (status) {
            START -> barcodeProcessor?.startProcessor()
            else -> barcodeProcessor?.stopProcessor()
        }
    }

    if (hasCameraPermission) {
        ScannerCameraPreview(getProductDetails = {
            if (it != null)
                viewModel.getProductById(id = it) { product ->
                    if (product == null) {
                        showNoProductFoundView = true
                    } else {
                        // FIXME: navigate to Product Listing page
                        Toast.makeText(context, product.toString(), Toast.LENGTH_SHORT)
                            .show()
                        viewModel.updateBarcodeDetectionProcessorStatus(STOP)
                        showNoProductFoundView = false
                    }
                }
            else {
                showNoProductFoundView = true
            }

        }, processorInstance = {
            barcodeProcessor = it
        }, setBarcodeProcessorStatus = {
            viewModel.updateBarcodeDetectionProcessorStatus(it)
        })
    } else {
        CameraPermissionDeniedMessage {
            cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    if (showNoProductFoundView) {
        NoProductFound()
        Handler(Looper.getMainLooper()).postDelayed({
            showNoProductFoundView = false
        }, 2000)
    }
}

@Composable
private fun NoProductFound() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Transparent),
        verticalArrangement = Arrangement.Bottom,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            modifier = Modifier
                .fillMaxWidth()
                .padding(36.dp),
            textAlign = TextAlign.Center,
            text = "No Matching Product found",
            color = Color.White,
            fontSize = 20.sp,
        )
    }
}

@Composable
fun CameraPermissionDeniedMessage(requestPermission: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Camera permission is required to scan barcodes",
            modifier = Modifier.padding(horizontal = 36.dp)
        )
        Spacer(modifier = Modifier.height(36.dp))
        Button(onClick = requestPermission) {
            Text(text = "Request Camera Permission")
        }
    }
}

@Composable
fun ScannerCameraPreview(
    getProductDetails: (String?) -> Unit,
    processorInstance: (BarcodeDetectionProcessor) -> Unit,
    setBarcodeProcessorStatus: (BarcodeDetectionProcessorStatus) -> Unit
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    AndroidView(factory = {
        val previewView = PreviewView(it).apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
        }

        val cameraProviderFuture = ProcessCameraProvider.getInstance(context)

        cameraProviderFuture.addListener({

            val cameraProvider = cameraProviderFuture.get()
            val preview = androidx.camera.core.Preview.Builder().build().also { preview ->
                preview.setSurfaceProvider(previewView.surfaceProvider)
            }
            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
            val executor = Executors.newSingleThreadExecutor()
            val barcodeDetectionProcessor: BarcodeDetectionProcessor?

            barcodeDetectionProcessor = BarcodeDetectionProcessor(object : DetectionListener {
                override fun onDetectionSuccess(barcode: Barcode) {
                    getProductDetails(barcode.rawValue)
                }

                override fun onDetectionFailure(failureMessage: String?) {
                    Toast.makeText(context, failureMessage, Toast.LENGTH_SHORT).show()
                    setBarcodeProcessorStatus(STOP)
                }
            })
            processorInstance(barcodeDetectionProcessor)
            setBarcodeProcessorStatus(START)
            val barcodeAnalysis = ImageAnalysis.Builder().build().apply {
                setAnalyzer(executor, barcodeDetectionProcessor)
            }

            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(
                    lifecycleOwner,
                    cameraSelector,
                    preview,
                    barcodeAnalysis
                )
            } catch (e: Exception) {
                Toast.makeText(context, e.localizedMessage, Toast.LENGTH_LONG).show()
                setBarcodeProcessorStatus(STOP)
            }


        }, ContextCompat.getMainExecutor(context))
        previewView
    })
}


@Preview(
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_NO,
    showSystemUi = true
)
@Composable
fun CameraPermissionDeniedMessagePreview() {
    CameraPermissionDeniedMessage {

    }
}

@Preview(
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_NO,
    showSystemUi = true
)
@Composable
fun ScannerViewPreview() {
    ScannerCameraPreview(getProductDetails = {

    }, processorInstance = {

    }, setBarcodeProcessorStatus = {

    })
}

@Preview(
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_NO,
    showSystemUi = true
)
@Composable
fun NoProductFoundPreview() {
    NoProductFound()
}