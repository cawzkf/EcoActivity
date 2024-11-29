package com.ecoactivity.app.ui

/**
 * Classe que representa um aparelho com seus consumos e custos associados.
 */
data class Aparelho(
    val id: String = "",
    val tipo: String = "",
    val consumoAtual: Double = 0.00, // Consumo atual em kWh
    val consumoInicial: Double = 0.00,
    val custo: Double = 0.00,         // Custo total do consumo (R$)
    val dataCadastro: Long = 0L,    // Data do registro em timestamp
    val quantidade: Int = 1,         // Quantidade de aparelhos (ajustado para padrão 1)
    val statusAp: Boolean = false    // Status do aparelho
) {
    /**
     * Calcula o custo total com base na tarifa fornecida (R$/kWh).
     */
    private fun calcularCusto(tarifa: Double): Double {
        return consumoAtual * quantidade * tarifa * 30
    }

    /**
     * Atualiza o custo baseado no consumo atual e na tarifa.
     */
    fun atualizarCusto(tarifa: Double): Aparelho {
        val novoCusto = calcularCusto(tarifa)
        return this.copy(custo = novoCusto)
    }
}
