package com.ecoactivity.app.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class NovoViewModel : ViewModel() {

    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    private val _devices = MutableLiveData<List<Aparelho>>()
    val devices: LiveData<List<Aparelho>> get() = _devices

    private val _tariff = MutableLiveData<Double>()
    val tariff: LiveData<Double> get() = _tariff

    init {
        loadAparelhosFromFirestore()
        loadTariffFromFirestore()
    }

    /**
     * Carrega os aparelhos do Firestore.
     */
    private fun loadAparelhosFromFirestore() {
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

    /**
     * Adiciona um aparelho ao Firestore.
     */
    fun addAparelho(aparelho: Aparelho) {
        val userId = auth.currentUser?.uid ?: return

        val newDoc = firestore.collection("users")
            .document(userId)
            .collection("aparelhos")
            .document()

        val novoAparelho = aparelho.copy(id = newDoc.id)
        newDoc.set(novoAparelho)
    }

    /**
     * Carrega a tarifa do Firestore.
     */
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

    /**
     * Define a tarifa no Firestore.
     */
    fun setTariff(newTariff: Double) {
        val userId = auth.currentUser?.uid ?: return
        _tariff.value = newTariff

        firestore.collection("users")
            .document(userId)
            .update("tariff", newTariff)
    }
}
