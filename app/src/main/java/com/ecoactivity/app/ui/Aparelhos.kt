package com.ecoactivity.app.ui

data class Aparelho(
    val id: String = "",
    val tipo: String = "",
    val consumoAtual: Double = 0.0, // Alterado para Double
    val consumoInicial: Double = 0.0, // Alterado para Double
    val custo: Double = 0.0,         // Alterado para Double
    val dataCadastro: Long = 0L,
    val quantidade: Int = 0          // Alterado para Int
) {
    // Método para calcular o custo total com base na tarifa fornecida (R$/kWh)
    fun calcularCusto(taxa: Double): Double {
        return consumoAtual * quantidade * taxa // Consumo atualizado * Quantidade * Tarifa
    }
}


