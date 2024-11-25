package com.ecoactivity.app.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class PainelViewModel : ViewModel() {

    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    private val _devices = MutableLiveData<List<Aparelho>>()
    val devices: LiveData<List<Aparelho>> = _devices

    private val _selectedDate = MutableLiveData<String>()
    val selectedDate: LiveData<String> = _selectedDate

    private val _tariff = MutableLiveData<Double>()
    val tariff: LiveData<Double> = _tariff

    init {
        loadDevicesFromFirestore()
        loadTariffFromFirestore()
    }

    private fun loadDevicesFromFirestore() {
        val userId = auth.currentUser?.uid ?: return

        firestore.collection("users")
            .document(userId)
            .collection("aparelhos")
            .addSnapshotListener { snapshot, e ->
                if (e != null) return@addSnapshotListener

                val aparelhosList = snapshot?.documents?.mapNotNull { document ->
                    document.toObject(Aparelho::class.java)?.copy(id = document.id)
                } ?: listOf()

                _devices.value = aparelhosList
            }
    }

    private fun loadTariffFromFirestore() {
        val userId = auth.currentUser?.uid ?: return

        firestore.collection("users")
            .document(userId)
            .get()
            .addOnSuccessListener { document ->
                val tariff = document.getDouble("tariff") ?: 0.0
                _tariff.value = tariff
            }
    }

    fun setSelectedDate(date: String) {
        _selectedDate.value = date
    }

    fun filterDataByDate(date: String) {
        val filteredDevices = _devices.value?.filter {
            // Filtrar aparelhos baseados na data de cadastro
            it.dataCadastro.toString().contains(date)
        }
        _devices.value = filteredDevices
    }
}
