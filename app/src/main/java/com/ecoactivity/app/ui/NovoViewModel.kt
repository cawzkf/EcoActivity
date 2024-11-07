package com.ecoactivity.app.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class NovoViewModel : ViewModel() {
    private val _devices = MutableLiveData<List<Aparelho>>()
    val devices: LiveData<List<Aparelho>> get() = _devices

    private val _tariff = MutableLiveData<Double>()
    val tariff: LiveData<Double> get() = _tariff

    init {
        _devices.value = listOf() // Inicializa com uma lista vazia
        _tariff.value = 0.0 // Tarifa inicial
    }

    fun addAparelho(aparelho: Aparelho) {
        // Adiciona o novo aparelho à lista existente
        val updatedList = _devices.value?.toMutableList() ?: mutableListOf()
        updatedList.add(aparelho)
        _devices.value = updatedList // Atualiza o LiveData
    }

    fun setTariff(newTariff: Double) {
        _tariff.value = newTariff
    }

    // Método para carregar mais aparelhos (se necessário)
    fun loadMoreAparelhos() {

    }
}
