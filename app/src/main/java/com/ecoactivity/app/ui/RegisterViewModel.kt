package com.ecoactivity.app.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.database.FirebaseDatabase

class RegisterViewModel : ViewModel() {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    private val _registrationError = MutableLiveData<String>()
    val registrationError: LiveData<String> get() = _registrationError

    private val _registrationSuccess = MutableLiveData<Boolean>() //  LiveData para sucesso
    val registrationSuccess: LiveData<Boolean> get() = _registrationSuccess

    fun registerUser(email: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    saveUserData(email)
                    _registrationSuccess.value = true // Indica que o registro foi bem-sucedido
                } else {
                    handleRegistrationError(task.exception)
                }
            }
    }

    private fun handleRegistrationError(exception: Exception?) {
        _registrationError.value = when (exception) {
            is FirebaseAuthInvalidCredentialsException -> {
                "Email inválido. Verifique e tente novamente."
            }
            is FirebaseAuthUserCollisionException -> {
                "Este email já está em uso."
            }
            else -> {
                "Erro ao registrar usuário: ${exception?.message}"
            }
        }
    }

    private fun saveUserData(email: String) {
        val database = FirebaseDatabase.getInstance()
        val myRef = database.getReference("usuarios")

        val userId = auth.currentUser?.uid ?: return
        val userData = User(userId, email)

        myRef.child(userId).setValue(userData)
            .addOnSuccessListener {
                // Dados salvos com sucesso
            }
            .addOnFailureListener {
                // Falha ao salvar dados
            }
    }
}

data class User(val id: String, val email: String)
