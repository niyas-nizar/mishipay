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
import androidx.compose.ui.res.stringResource
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
import com.niyas.mishipay.R
import com.niyas.mishipay.data.network.ProductData
import com.niyas.mishipay.navigation.BarcodeScannerScreens
import com.niyas.mishipay.screens.composables.ReusableDoubleButtons
import com.niyas.mishipay.screens.composables.ShowProgress
import com.niyas.mishipay.screens.previewstaticdata.PreviewStaticData
import com.niyas.mishipay.utils.CurrencyFormatter

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
            text = stringResource(R.string.items_added_to_cart),
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
        }, negativeButtonText = stringResource(R.string.add_more_items),
            positiveButtonText = stringResource(R.string.checkout)
        )
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
        Row(
            modifier = Modifier.padding(16.dp)
        ) {
            var showIconLoader by remember {
                mutableStateOf(false)
            }
            Box(modifier = Modifier.align(Alignment.Top)) {
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
                        fontSize = 16.sp,
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
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Light,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp)
                )

                Text(
                    text = CurrencyFormatter.formatToINR(productData.price),
                    fontSize = 18.sp,
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


@Composable
fun QuantitySelector(quantitySelected: Int, addProduct: () -> Unit, removeProduct: () -> Unit) {
    var quantity by remember(quantitySelected) {
        mutableIntStateOf(quantitySelected)
    }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
        modifier = Modifier.padding(horizontal = 8.dp)
    ) {
        Box(
            modifier = Modifier
                .size(24.dp)
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
            Text(text = stringResource(R.string.minus), fontSize = 18.sp, color = Color.White)
        }


        Text(
            text = quantity.toString(),
            fontSize = 16.sp,
            modifier = Modifier.padding(horizontal = 8.dp)
        )


        Box(
            modifier = Modifier
                .size(24.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(Color.Gray)
                .clickable {
                    quantity++
                    addProduct()
                },
            contentAlignment = Alignment.Center
        ) {
            Text(text = stringResource(R.string.plus), fontSize = 18.sp, color = Color.White)
        }
    }
}

@Preview(
    showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_NO, showSystemUi = true
)
@Composable
fun CartItemPreview() {
    val product = PreviewStaticData.generateStaticData().first()
    CartItem(product, addProduct = {}, removeProduct = {}, removeWholeProduct = {})
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
