package com.tecsup.productmanager.data.model

data class Product(
    val id: String = "",
    val nombre: String = "",
    val precio: Double = 0.0,
    val stock: Int = 0,
    val categoria: String = "",
    val userId: String = ""
) {
    constructor() : this("", "", 0.0, 0, "", "")

    fun toMap(): Map<String, Any> {
        return mapOf(
            "nombre" to nombre,
            "precio" to precio,
            "stock" to stock,
            "categoria" to categoria,
            "userId" to userId
        )
    }
}