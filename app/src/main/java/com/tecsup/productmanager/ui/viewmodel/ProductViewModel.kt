package com.tecsup.productmanager.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tecsup.productmanager.data.model.Product
import com.tecsup.productmanager.data.repository.ProductRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class ProductState(
    val products: List<Product> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val successMessage: String? = null
)

class ProductViewModel : ViewModel() {
    private val repository = ProductRepository()

    private val _productState = MutableStateFlow(ProductState())
    val productState: StateFlow<ProductState> = _productState

    fun loadProducts(userId: String) {
        viewModelScope.launch {
            repository.getProductsRealTime(userId).collect { products ->
                _productState.value = ProductState(products = products)
            }
        }
    }

    fun createProduct(product: Product) {
        viewModelScope.launch {
            _productState.value = _productState.value.copy(isLoading = true)

            val result = repository.createProduct(product)

            if (result.isSuccess) {
                _productState.value = _productState.value.copy(
                    isLoading = false,
                    successMessage = "Producto creado exitosamente"
                )
            } else {
                _productState.value = _productState.value.copy(
                    isLoading = false,
                    error = result.exceptionOrNull()?.message ?: "Error al crear producto"
                )
            }
        }
    }

    fun updateProduct(productId: String, product: Product) {
        viewModelScope.launch {
            _productState.value = _productState.value.copy(isLoading = true)

            val result = repository.updateProduct(productId, product)

            if (result.isSuccess) {
                _productState.value = _productState.value.copy(
                    isLoading = false,
                    successMessage = "Producto actualizado exitosamente"
                )
            } else {
                _productState.value = _productState.value.copy(
                    isLoading = false,
                    error = result.exceptionOrNull()?.message ?: "Error al actualizar producto"
                )
            }
        }
    }

    fun deleteProduct(productId: String) {
        viewModelScope.launch {
            val result = repository.deleteProduct(productId)

            if (result.isSuccess) {
                _productState.value = _productState.value.copy(
                    successMessage = "Producto eliminado exitosamente"
                )
            } else {
                _productState.value = _productState.value.copy(
                    error = result.exceptionOrNull()?.message ?: "Error al eliminar producto"
                )
            }
        }
    }

    fun clearMessages() {
        _productState.value = _productState.value.copy(
            error = null,
            successMessage = null
        )
    }
}