package com.ecoactivity.app.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class PainelViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is home Fragment"
    }
    val text: LiveData<String> = _text

    private val _selectedDate = MutableLiveData<String>()
    val selectedDate: LiveData<String> = _selectedDate

    val devices: LiveData<List<Aparelho>> = MutableLiveData() // Adapte conforme necessário

    private val _tariff = MutableLiveData<Double>().apply {
        value = 0.0 // Valor padrão, ajuste conforme necessário
    }
    val tariff: LiveData<Double> = _tariff

    fun setSelectedDate(date: String) {
        _selectedDate.value = date
    }

    fun filterDataByDate(date: String) {
        // Lógica para filtrar os dados no LiveData `devices`
        // Por exemplo, se você tem uma lista de aparelhos, filtre-a com base na data
    }
}
