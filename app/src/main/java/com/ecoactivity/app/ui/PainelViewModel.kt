package com.ecoactivity.app.ui

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.Locale

class PainelViewModel : ViewModel() {

    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    private val _devices = MutableLiveData<List<Aparelho>>()
    val devices: LiveData<List<Aparelho>> get() = _devices

    private val _selectedDate = MutableLiveData<String>()
    val selectedDate: LiveData<String> get() = _selectedDate

    private val _tariff = MutableLiveData<Double>()
    val tariff: LiveData<Double> get() = _tariff

    private var allDevices: List<Aparelho> = emptyList() // Mantém a lista completa para restauração após o filtro

    init {
        loadDevicesFromFirestore()
        loadTariffFromFirestore()
    }

    /**
     * Carrega os aparelhos do Firestore.
     */
    private fun loadDevicesFromFirestore() {
        val userId = auth.currentUser?.uid ?: run {
            Log.e("PainelViewModel", "Usuário não autenticado.")
            return
        }

        firestore.collection("users")
            .document(userId)
            .collection("aparelhos")
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    Log.e("PainelViewModel", "Erro ao carregar aparelhos: ${e.message}")
                    return@addSnapshotListener
                }

                val aparelhosList = snapshot?.documents?.mapNotNull { document ->
                    try {
                        document.toObject(Aparelho::class.java)?.copy(id = document.id)
                    } catch (ex: Exception) {
                        Log.e("PainelViewModel", "Erro ao mapear aparelhos: ${ex.message}")
                        null
                    }
                } ?: emptyList()

                allDevices = aparelhosList // Atualiza a lista completa
                _devices.value = aparelhosList
                Log.d("PainelViewModel", "Aparelhos carregados: $aparelhosList")
            }
    }

    /**
     * Carrega a tarifa do Firestore.
     */
    private fun loadTariffFromFirestore() {
        val userId = auth.currentUser?.uid ?: run {
            Log.e("PainelViewModel", "Usuário não autenticado.")
            return
        }

        firestore.collection("users")
            .document(userId)
            .get()
            .addOnSuccessListener { document ->
                val tariff = document.getDouble("tariff") ?: 0.0
                _tariff.value = tariff
                Log.d("PainelViewModel", "Tarifa carregada: $tariff")
            }
            .addOnFailureListener { e ->
                Log.e("PainelViewModel", "Erro ao carregar tarifa: ${e.message}")
            }
    }

    /**
     * Define a data selecionada.
     */
    fun setSelectedDate(date: String) {
        _selectedDate.value = date
        filterDataByDate(date)
    }

    /**
     * Filtra os aparelhos pela data.
     */
    fun filterDataByDate(date: String) {
        if (date.isEmpty()) {
            // Se a data estiver vazia, restaura a lista completa
            _devices.value = allDevices
            return
        }

        val filteredDevices = allDevices.filter { aparelho ->
            val dataCadastro = aparelho.dataCadastro
            // Converte o timestamp para formato de data (se necessário)
            val formattedDate = formatDate(dataCadastro)
            formattedDate.contains(date)
        }

        _devices.value = filteredDevices
        Log.d("PainelViewModel", "Aparelhos filtrados por data: $filteredDevices")
    }

    /**
     * Formata o timestamp em uma string de data (ex: "2024-11-27").
     */
    private fun formatDate(timestamp: Long): String {
        val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return formatter.format(timestamp)
    }
}
