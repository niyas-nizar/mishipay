package com.niyas.mishipay.screens

import android.content.res.Configuration
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.niyas.mishipay.data.network.ProductData
import com.niyas.mishipay.navigation.BarcodeScannerScreens
import com.niyas.mishipay.screens.previewstaticdata.PreviewStaticData

@Composable
fun CartScreen(viewModel: BarcodeViewModel, navController: NavHostController) {

    var showLoader by remember {
        mutableStateOf(true)
    }

    if (showLoader) ShowProgress()

    val cartItems by viewModel.getProductsFromCart().collectAsState(initial = emptyList())

    cartListingScreen(cartItems, showLoader = {
        showLoader = it
    }, removeItem = {
        viewModel.removeProductFromCart(it)
    }, navController)

}

@Composable
private fun cartListingScreen(
    cartItems: List<ProductData>,
    showLoader: (Boolean) -> Unit,
    removeItem: (ProductData) -> Unit,
    navController: NavHostController
) {
    Column {
        Text(
            text = "Items Added to Cart",
            fontSize = 24.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp)
        )
        LazyColumn(content = {
            items(items = cartItems) {
                CartItem(it) {
                    removeItem(it)
                }
            }
            showLoader(false)
        }, modifier = Modifier.weight(1f))
        Button(
            onClick = {
                navController.popBackStack()
            }, modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 4.dp)
        ) {
            Text(text = "Add More Items")
        }
        Button(
            onClick = {
                navController.navigate(BarcodeScannerScreens.CHECKOUT_SCREEN.name)
            }, modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 4.dp)
        ) {
            Text(text = "Checkout")
        }
    }
}


@Composable
fun CartItem(productData: ProductData, removeItem: () -> Unit) {
    Card(
        modifier = Modifier.padding(8.dp), shape = RoundedCornerShape(16.dp)
    ) {
        Column {
            Row(
                modifier = Modifier.padding(16.dp)
            ) {
                var showIconLoader by remember {
                    mutableStateOf(false)
                }
                Box {
                    if (showIconLoader) CircularProgressIndicator(
                        modifier = Modifier.size(75.dp),
                    )
                    Icon(rememberAsyncImagePainter(model = ImageRequest.Builder(LocalContext.current)
                        .data(productData.imageUrl).crossfade(true).build(), onLoading = {
                        showIconLoader = true
                    }, onSuccess = {
                        showIconLoader = false
                    }),
                        contentDescription = null,
                        modifier = Modifier
                            .size(75.dp)
                            .clip(RoundedCornerShape(16.dp)),
                        tint = Color.Unspecified
                    )
                }

                Column {
                    Row {
                        Text(
                            maxLines = 1,
                            text = productData.title,
                            fontSize = 18.sp,
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f)
                                .padding(horizontal = 8.dp)
                        )

                        Icon(
                            imageVector = Icons.Default.Clear,
                            modifier = Modifier
                                .size(18.dp)
                                .clickable {
                                    // FIXME: Remove item from listing
                                    removeItem()
                                },
                            contentDescription = productData.description
                        )
                    }


                    Text(
                        maxLines = 3,
                        text = productData.description,
                        fontSize = 15.sp,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp)
                    )

                    Text(
                        text = "${productData.price} RS",
                        fontSize = 20.sp,
                        style = TextStyle(fontWeight = FontWeight.SemiBold),
                        modifier = Modifier.padding(8.dp)
                    )

                }


            }

        }
    }

}


/*@Preview(
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_NO,
    showSystemUi = true
)
@Composable
fun CartScreenPreview() {
    CartScreen()
}*/

@Preview(
    showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_NO, showSystemUi = true
)
@Composable
fun CartItemPreview() {
    val product = ProductData(
        imageUrl = "https://example.com/image1.jpg",
        price = "100",
        title = "Mobile",
        description = "Latest smartphone with amazing features",
        category = "Electronics",
        id = "12345"
    )
    CartItem(product) {

    }
}

@Preview(
    showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_NO, showSystemUi = true
)
@Composable
fun CartButtonsPreview() {
    Column {
        Button(
            onClick = { },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 4.dp)
        ) {
            Text(text = "Add More Items")
        }
        Button(
            onClick = { },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 4.dp)
        ) {
            Text(text = "Checkout")
        }
    }
}


@Preview(
    showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_NO, showSystemUi = true
)
@Composable
fun CartScreenPreview() {
    val navController = rememberNavController()
    val cartItems = PreviewStaticData.generateStaticData()
    cartListingScreen(
        cartItems = cartItems,
        showLoader = {},
        removeItem = {},
        navController = navController)
}
