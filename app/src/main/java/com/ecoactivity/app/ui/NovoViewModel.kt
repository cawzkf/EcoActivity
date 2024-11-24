package com.ecoactivity.app.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class NovoViewModel : ViewModel() {

    // Lista de aparelhos cadastrados
    private val _devices = MutableLiveData<List<Aparelho>?>()
    val devices: LiveData<List<Aparelho>?> get() = _devices

    // Tarifa utilizada no cálculo do custo
    private val _tariff = MutableLiveData<Double>()
    val tariff: LiveData<Double> get() = _tariff

    private val database: DatabaseReference = FirebaseDatabase.getInstance().getReference("aparelhos")
    private val userId: String? = FirebaseAuth.getInstance().currentUser?.uid

    init {
        // Inicializa com uma lista vazia de aparelhos
        _devices.value = listOf()
        _tariff.value = 0.0  // Inicializa a tarifa com 0
        loadAparelhosFromFirebase()  // Carregar os aparelhos do Firebase no início
    }

    // Função para carregar aparelhos do Firebase
    private fun loadAparelhosFromFirebase() {
        if (userId == null) return // Retorna se o usuário não estiver autenticado

        database.child(userId).child("aparelhos").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val aparelhosList = mutableListOf<Aparelho>()
                for (dataSnapshot in snapshot.children) {
                    val aparelho = dataSnapshot.getValue(Aparelho::class.java)
                    aparelho?.let { aparelhosList.add(it) }
                }
                _devices.value = aparelhosList // Atualiza o LiveData com a lista recuperada
            }

            override fun onCancelled(error: DatabaseError) {
                // Lida com erros, como problemas de conexão ou permissões
                // Adicione logs ou notificações se necessário
            }
        })
    }

    // Função para adicionar um aparelho ao Firebase e atualizar o LiveData
    fun addAparelho(aparelho: Aparelho) {
        if (userId == null) return // Retorna se o usuário não estiver autenticado

        val userAparelhosRef = database.child(userId).child("aparelhos")
        val deviceId = userAparelhosRef.push().key ?: return

        val novoAparelho = aparelho.copy(id = deviceId) // Garante que o ID seja único
        userAparelhosRef.child(deviceId).setValue(novoAparelho).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val updatedList = _devices.value?.toMutableList() ?: mutableListOf()
                updatedList.add(novoAparelho)
                _devices.value = updatedList // Atualiza o LiveData local
            }
        }
    }

    // Função para excluir um aparelho específico
    fun deleteAparelho(aparelho: Aparelho) {
        if (userId == null) return // Retorna se o usuário não estiver autenticado

        // Remove do Firebase
        database.child(userId).child("aparelhos").child(aparelho.id).removeValue().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                // Atualiza o LiveData local
                val updatedList = _devices.value?.toMutableList() // Verifica se _devices.value não é nulo
                updatedList?.let {
                    it.remove(aparelho)
                    _devices.value = it
                }
            }
        }
    }

    // Função para excluir múltiplos aparelhos
    fun deleteAparelhos(aparelhos: List<Aparelho>) {
        for (aparelho in aparelhos) {
            deleteAparelho(aparelho)
        }
    }

    // Função para atualizar a tarifa
    fun setTariff(newTariff: Double) {
        _tariff.value = newTariff
    }
}
