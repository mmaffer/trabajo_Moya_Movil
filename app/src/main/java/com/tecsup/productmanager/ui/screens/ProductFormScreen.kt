package com.tecsup.productmanager.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.tecsup.productmanager.data.model.Product

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductFormScreen(
    product: Product?,
    userId: String,
    onSave: (Product) -> Unit,
    onNavigateBack: () -> Unit,
    isLoading: Boolean
) {
    var nombre by remember { mutableStateOf(product?.nombre ?: "") }
    var precio by remember { mutableStateOf(product?.precio?.toString() ?: "") }
    var stock by remember { mutableStateOf(product?.stock?.toString() ?: "") }
    var categoria by remember { mutableStateOf(product?.categoria ?: "") }

    var nombreError by remember { mutableStateOf(false) }
    var precioError by remember { mutableStateOf(false) }
    var stockError by remember { mutableStateOf(false) }
    var categoriaError by remember { mutableStateOf(false) }

    val categorias = listOf("Electrónica", "Ropa", "Alimentos", "Hogar", "Deportes", "Otros")
    var expanded by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (product == null) "Nuevo Producto" else "Editar Producto") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Volver")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedTextField(
                value = nombre,
                onValueChange = {
                    nombre = it
                    nombreError = false
                },
                label = { Text("Nombre *") },
                isError = nombreError,
                supportingText = {
                    if (nombreError) Text("El nombre es obligatorio")
                },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            OutlinedTextField(
                value = precio,
                onValueChange = {
                    if (it.isEmpty() || it.matches(Regex("^\\d*\\.?\\d*$"))) {
                        precio = it
                        precioError = false
                    }
                },
                label = { Text("Precio (S/)") },
                isError = precioError,
                supportingText = {
                    if (precioError) Text("Ingrese un precio válido")
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )


            OutlinedTextField(
                value = stock,
                onValueChange = {
                    if (it.isEmpty() || it.matches(Regex("^\\d+$"))) {
                        stock = it
                        stockError = false
                    }
                },
                label = { Text("Stock") },
                isError = stockError,
                supportingText = {
                    if (stockError) Text("Ingrese un stock válido")
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = it }
            ) {
                OutlinedTextField(
                    value = categoria,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Categoría") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                    isError = categoriaError,
                    supportingText = {
                        if (categoriaError) Text("Seleccione una categoría")
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor(),
                    colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors()
                )

                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    categorias.forEach { cat ->
                        DropdownMenuItem(
                            text = { Text(cat) },
                            onClick = {
                                categoria = cat
                                categoriaError = false
                                expanded = false
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = {
                    nombreError = nombre.isBlank()
                    precioError = precio.isBlank() || precio.toDoubleOrNull() == null
                    stockError = stock.isBlank() || stock.toIntOrNull() == null
                    categoriaError = categoria.isBlank()

                    if (!nombreError && !precioError && !stockError && !categoriaError) {
                        val newProduct = Product(
                            id = product?.id ?: "",
                            nombre = nombre,
                            precio = precio.toDouble(),
                            stock = stock.toInt(),
                            categoria = categoria,
                            userId = userId
                        )
                        onSave(newProduct)
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = !isLoading
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Text("Guardar")
                }
            }
        }
    }
}