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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.navigation.NavHostController
import com.google.mlkit.vision.barcode.common.Barcode
import com.niyas.mishipay.R
import com.niyas.mishipay.data.BarcodeDetectionProcessorStatus
import com.niyas.mishipay.data.BarcodeDetectionProcessorStatus.START
import com.niyas.mishipay.data.BarcodeDetectionProcessorStatus.STOP
import com.niyas.mishipay.navigation.BarcodeScannerScreens
import com.niyas.mishipay.screens.composables.ShowProgress
import com.niyas.mishipay.utils.barcodedetectionprocessor.BarcodeDetectionProcessor
import com.niyas.mishipay.utils.barcodedetectionprocessor.DetectionListener
import java.util.concurrent.Executors

@Composable
fun ScannerScreen(viewModel: BarcodeViewModel, navController: NavHostController) {

    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    var barcodeProcessor: BarcodeDetectionProcessor? = null
    var cameraProvider: ProcessCameraProvider? = null

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
                        viewModel.updateBarcodeDetectionProcessorStatus(STOP)
                        viewModel.addProductToCart(product)
                        showNoProductFoundView = false
                        cameraProvider?.unbindAll()
                        navController.navigate(BarcodeScannerScreens.CART_SCREEN.name)
                    }
                }
            else {
                showNoProductFoundView = true
            }

        }, processorInstance = {
            barcodeProcessor = it
        }, setBarcodeProcessorStatus = {
            viewModel.updateBarcodeDetectionProcessorStatus(it)
        }, cameraProviderInstance = {
            cameraProvider = it
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
            text = stringResource(R.string.no_matching_product_found),
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
            text = stringResource(R.string.camera_permission_is_required_to_scan_barcodes),
            modifier = Modifier.padding(horizontal = 16.dp)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Button(
            onClick = requestPermission,
            shape = RoundedCornerShape(5.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color.Black),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        ) {
            Text(text = stringResource(R.string.request_camera_permission), color = Color.White)
        }
    }
}

@Composable
fun ScannerCameraPreview(
    getProductDetails: (String?) -> Unit,
    processorInstance: (BarcodeDetectionProcessor) -> Unit,
    setBarcodeProcessorStatus: (BarcodeDetectionProcessorStatus) -> Unit,
    cameraProviderInstance: (ProcessCameraProvider) -> Unit
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    var lastDetectedBarcodeData by remember { mutableStateOf<String?>(null) }
    var lastDetectionTime by remember { mutableStateOf(0L) }

    var showLoader by remember {
        mutableStateOf(true)
    }

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
            cameraProviderInstance(cameraProvider)
            val preview = androidx.camera.core.Preview.Builder().build().also { preview ->
                preview.setSurfaceProvider(previewView.surfaceProvider)
            }
            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
            val executor = Executors.newSingleThreadExecutor()
            val barcodeDetectionProcessor: BarcodeDetectionProcessor?

            barcodeDetectionProcessor = BarcodeDetectionProcessor(object : DetectionListener {
                override fun onDetectionSuccess(barcode: Barcode) {
                    val currentTime = System.currentTimeMillis()
                    if (barcode.rawValue != lastDetectedBarcodeData || (currentTime - lastDetectionTime) > 2000) {
                        lastDetectedBarcodeData = barcode.rawValue
                        lastDetectionTime = currentTime

                        getProductDetails(barcode.rawValue)
                    }
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
                showLoader = false
            } catch (e: Exception) {
                showLoader = false
                Toast.makeText(context, e.localizedMessage, Toast.LENGTH_LONG).show()
                setBarcodeProcessorStatus(STOP)
            }
        }, ContextCompat.getMainExecutor(context))
        previewView
    })

    if (showLoader)
        ShowProgress()

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

    }, cameraProviderInstance = {

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