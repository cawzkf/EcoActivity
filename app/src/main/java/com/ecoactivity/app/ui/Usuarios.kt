package com.ecoactivity.app.ui

data class Usuarios(
    val id: String = "",
    val email: String = "",
    val dateCreated: Long = 0L,
    val aparelhos: Map<String, Any> = emptyMap() // Inicializa com um mapa vazio
)
