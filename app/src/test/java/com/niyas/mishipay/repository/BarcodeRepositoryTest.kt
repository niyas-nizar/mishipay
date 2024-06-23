package com.niyas.mishipay.repository

import com.google.common.truth.Truth.assertThat
import com.niyas.mishipay.data.network.ProductData
import org.junit.Before
import org.junit.Test


class BarcodeRepositoryTest {

    private lateinit var repository: BarcodeRepository
    private val testProduct = ProductData(
        price = 79999,
        title = "Apple iPhone 15",
        description = "Apple iPhone 15 with 128GB storage, A15 Bionic chip, 6.1-inch Super Retina XDR display, and dual-camera system.",
        category = "Electronics",
        id = "10001"
    )


    @Before
    fun setUp() {
        repository = BarcodeRepository()
    }

    @Test
    fun findProduct() {
        repository.upsertProductToCart(testProduct.copy())

        val productsInCart = repository.getProductsFromCart()

        assertThat(productsInCart.first().id).isEqualTo(testProduct.copy().id)
    }

    @Test
    fun upsertProductToCart() {
        repository.upsertProductToCart(testProduct.copy())

        val productsInCart = repository.getProductsFromCart()

        assertThat(productsInCart.size).isEqualTo(1)
        assertThat(productsInCart.first().quantity).isEqualTo(1)

        repository.upsertProductToCart(testProduct.copy())

        assertThat(productsInCart.size).isEqualTo(1)
        assertThat(productsInCart.first().quantity).isEqualTo(2)
    }

    @Test
    fun getProductsFromCart() {
        repository.upsertProductToCart(testProduct.copy())

        val productsInCart = repository.getProductsFromCart()

        assertThat(productsInCart.size).isEqualTo(1)
    }

    @Test
    fun removeSameProductsFromCart() {
        repository.upsertProductToCart(testProduct.copy())
        repository.upsertProductToCart(testProduct.copy())

        repository.removeAllInstancesOfProduct(testProduct.copy())

        val productsInCart = repository.getProductsFromCart()

        assertThat(productsInCart.size).isEqualTo(0)
    }

    @Test
    fun removeSingleInstanceOfProduct() {
        repository.upsertProductToCart(testProduct.copy())
        repository.upsertProductToCart(testProduct.copy())

        repository.removeSingleInstanceOfProduct(testProduct.copy())

        val productsInCart = repository.getProductsFromCart()
        assertThat(productsInCart.size).isEqualTo(1)
        assertThat(productsInCart[0].id).isEqualTo(testProduct.copy().id)

        repository.removeSingleInstanceOfProduct(testProduct.copy())
        assertThat(repository.getProductsFromCart().size).isEqualTo(0)

    }

    @Test
    fun getTotalAmountToPay() {
        val testProduct2 = ProductData(
            price = 5999,
            title = "Sony WH-1000XM4",
            description = "Sony WH-1000XM4 Wireless Noise Cancelling Over-Ear Headphones with mic for phone-call, 30 hours battery life, and Alexa voice control.",
            category = "Electronics",
            id = "10002"
        )

        repository.upsertProductToCart(testProduct.copy())
        repository.upsertProductToCart(testProduct2)

        val totalAmount = repository.getTotalAmountToPay()
        assertThat(totalAmount).isEqualTo(totalAmount)
    }
}