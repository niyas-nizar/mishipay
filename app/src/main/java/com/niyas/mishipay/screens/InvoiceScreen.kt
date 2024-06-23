package com.niyas.mishipay.screens

import android.content.res.Configuration
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.niyas.mishipay.data.network.ProductData
import com.niyas.mishipay.screens.composables.ReusableDoubleButtons
import com.niyas.mishipay.screens.previewstaticdata.PreviewStaticData
import com.niyas.mishipay.utils.CurrencyFormatter

@Composable
fun InvoiceScreen(viewModel: BarcodeViewModel, navController: NavHostController) {
    viewModel.getProductsFromCart()

    val cartItems by viewModel.cartItems.observeAsState(emptyList())

    val totalToPayAmount by viewModel.getTotalAmountToPay().collectAsState(initial = 0)
    InvoiceDetailsScreen(navController, cartItems, totalToPayAmount)
}

@Composable
private fun InvoiceDetailsScreen(
    navController: NavHostController,
    cartItems: List<ProductData>,
    totalToPayAmount: Int
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Text(
            text = "Order Summary",
            fontSize = 20.sp,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            textAlign = TextAlign.Center
        )
        HorizontalDivider()
        AddressDetails()
        HorizontalDivider(modifier = Modifier.padding(top = 8.dp))
        PriceDetails(cartItems, totalToPayAmount = totalToPayAmount) {
            navController.popBackStack()
        }
    }
}

@Composable
fun AddressDetails() {
    Column {
        Text(
            text = "Deliver to",
            fontSize = 14.sp,
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, top = 16.dp),
            textAlign = TextAlign.Start
        )

        Text(
            text = "Mohamed Niyas N",
            fontSize = 18.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, top = 4.dp),
            textAlign = TextAlign.Start
        )

        Text(
            text = "4/46 Kandathil Parambu Mattancherry, \nKochi 2",
            fontSize = 12.sp,
            fontWeight = FontWeight.Light,
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, top = 4.dp),
            textAlign = TextAlign.Start
        )

    }
}


@Composable
fun PriceDetails(cartItems: List<ProductData>, totalToPayAmount: Int, editCartItems: () -> Unit) {
    Column {
        Text(
            text = "Price Details",
            fontSize = 14.sp,
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, top = 16.dp),
            textAlign = TextAlign.Start
        )

        LazyColumn(
            content = {
                item {
                    ProductPriceListingHeader()
                }
                items(cartItems) {
                    IndividualProductPriceListing(it)
                }
            }, modifier = Modifier
                .padding(top = 8.dp)
                .weight(1f)
        )
        val context = LocalContext.current
        val amountToPay = CurrencyFormatter.formatToINR(totalToPayAmount)
        val totalPayableMessage = "Total to pay - $amountToPay"
        ReusableDoubleButtons(
            negativeButtonAction = { editCartItems() },
            positiveButtonAction = {
                Toast.makeText(context, totalPayableMessage, Toast.LENGTH_SHORT).show()
            },
            negativeButtonText = "Edit Cart Items",
            positiveButtonText = totalPayableMessage
        )
    }
}

@Composable
fun IndividualProductPriceListing(productData: ProductData) {
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 2.dp), horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = productData.title,
                fontSize = 12.sp,
                fontWeight = FontWeight.Light,
                modifier = Modifier
                    .padding(start = 16.dp)
                    .weight(0.55f),
                textAlign = TextAlign.Start
            )

            Text(
                text = CurrencyFormatter.formatToINR(productData.price),
                fontSize = 12.sp,
                fontWeight = FontWeight.Light,
                modifier = Modifier
                    .padding(start = 8.dp)
                    .weight(0.2f),
                textAlign = TextAlign.Center
            )

            Text(
                text = "x${productData.quantity}",
                fontSize = 12.sp,
                fontWeight = FontWeight.Light,
                modifier = Modifier
                    .padding(start = 8.dp)
                    .weight(0.1f),
                textAlign = TextAlign.Center
            )

            val amount = CurrencyFormatter.formatToINR(productData.quantity * productData.price)

            Text(
                text = amount,
                fontSize = 12.sp,
                fontWeight = FontWeight.Light,
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .weight(0.3f),
                textAlign = TextAlign.Center
            )

        }
    }

}

@Composable
fun ProductPriceListingHeader() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp)
    ) {
        Text(
            text = "Item",
            fontSize = 12.sp,
            fontWeight = FontWeight.Light,
            modifier = Modifier
                .padding(start = 16.dp)
                .weight(0.55f),
            textAlign = TextAlign.Start
        )
        Text(
            text = "Price",
            fontSize = 12.sp,
            fontWeight = FontWeight.Light,
            modifier = Modifier
                .padding(start = 8.dp)
                .weight(0.2f),
            textAlign = TextAlign.Center
        )

        Text(
            text = "Qty",
            fontSize = 12.sp,
            fontWeight = FontWeight.Light,
            modifier = Modifier
                .padding(start = 8.dp)
                .weight(0.1f),
            textAlign = TextAlign.Center
        )
        Text(
            text = "Total",
            fontSize = 12.sp,
            fontWeight = FontWeight.Light,
            modifier = Modifier
                .padding(horizontal = 8.dp)
                .weight(0.3f),
            textAlign = TextAlign.Center
        )

    }

}

@Preview(showSystemUi = true, showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_NO)
@Composable
fun InvoiceScreenPreview() {
    val navController = rememberNavController()
    val cartItems = PreviewStaticData.generateStaticData()
    InvoiceDetailsScreen(navController, cartItems, totalToPayAmount = 150)
}

@Preview(showSystemUi = true, showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_NO)
@Composable
fun IndividualProductPriceListingPreview() {
    val productList = PreviewStaticData.generateStaticData()
    IndividualProductPriceListing(productList.first())
}