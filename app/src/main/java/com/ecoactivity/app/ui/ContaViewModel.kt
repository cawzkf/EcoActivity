package com.ecoactivity.app.viewmodel

import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth

class ContaViewModel : ViewModel() {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    // Função para fazer logout do usuário
    fun logout() {
        auth.signOut() // Faz logout do usuário
    }
}
