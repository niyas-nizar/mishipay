package com.niyas.mishipay.screens

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
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
import com.niyas.mishipay.screens.composables.ReusableDoubleButtons
import com.niyas.mishipay.screens.composables.ShowProgress
import com.niyas.mishipay.screens.previewstaticdata.PreviewStaticData

@Composable
fun CartScreen(viewModel: BarcodeViewModel, navController: NavHostController) {

    var showLoader by remember {
        mutableStateOf(true)
    }

    if (showLoader) ShowProgress()
    viewModel.getProductsFromCart()

    val cartItems by viewModel.cartItems.observeAsState(emptyList())

    cartListingScreen(cartItems, showLoader = {
        showLoader = it
    }, removeWholeProduct = {
        viewModel.removeAllInstancesOfProduct(it, cartIsEmpty = { cartIsEmpty ->
            if (cartIsEmpty)
                navController.popBackStack()
        })
    }, addProduct = {
        viewModel.addProductToCart(product = it)
    }, removeProduct = {
        viewModel.removeSingleInstanceOfProduct(it) { cartIsEmpty ->
            if (cartIsEmpty)
                navController.popBackStack()
        }
    }, navController)

}

@Composable
private fun cartListingScreen(
    cartItems: List<ProductData>,
    showLoader: (Boolean) -> Unit,
    removeWholeProduct: (ProductData) -> Unit,
    addProduct: (ProductData) -> Unit,
    removeProduct: (ProductData) -> Unit,
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
                CartItem(it, removeWholeProduct = {
                    removeWholeProduct(it)
                }, addProduct = {
                    addProduct(it)
                }, removeProduct = {
                    removeProduct(it)
                })
            }
            showLoader(false)
        }, modifier = Modifier.weight(1f))
        ReusableDoubleButtons(negativeButtonAction = {
            navController.popBackStack()
        }, positiveButtonAction = {
            navController.navigate(BarcodeScannerScreens.INVOICE_SCREEN.name)
        }, negativeButtonText = "Add More Items", positiveButtonText = "Checkout")
    }
}


@Composable
fun CartItem(
    productData: ProductData,
    removeWholeProduct: () -> Unit,
    addProduct: () -> Unit,
    removeProduct: () -> Unit
) {
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
                                    removeWholeProduct()
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
                        text = "${productData.price} Rs",
                        fontSize = 20.sp,
                        style = TextStyle(fontWeight = FontWeight.SemiBold),
                        modifier = Modifier.padding(8.dp)
                    )
                    QuantitySelector(
                        productData.quantity,
                        addProduct = addProduct,
                        removeProduct = removeProduct
                    )
                }
            }
        }
    }

}


@Composable
fun QuantitySelector(quantitySelected: Int, addProduct: () -> Unit, removeProduct: () -> Unit) {
    var quantity by remember(quantitySelected) {
        mutableIntStateOf(quantitySelected)
    }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
        modifier = Modifier.padding(horizontal = 16.dp)
    ) {
        Box(
            modifier = Modifier
                .size(30.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(Color.Gray)
                .clickable {
                    if (quantity > 0) {
                        quantity--
                        removeProduct()
                    }
                },
            contentAlignment = Alignment.Center
        ) {
            Text(text = "-", fontSize = 18.sp, color = Color.White)
        }


        Text(
            text = quantity.toString(),
            fontSize = 24.sp,
            modifier = Modifier.padding(horizontal = 12.dp)
        )


        Box(
            modifier = Modifier
                .size(30.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(Color.Gray)
                .clickable {
                    quantity++
                    addProduct()
                },
            contentAlignment = Alignment.Center
        ) {
            Text(text = "+", fontSize = 18.sp, color = Color.White)
        }
    }
}

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
    CartItem(product, addProduct = {}, removeProduct = {}, removeWholeProduct = {})
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
        removeWholeProduct = {},
        addProduct = {},
        removeProduct = {},
        navController = navController
    )
}
