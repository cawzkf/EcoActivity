package com.ecoactivity.app.ui

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore

class LoginViewModel : ViewModel() {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    private val _loginState = MutableLiveData<LoginState>()
    val loginState: LiveData<LoginState> get() = _loginState

    fun checkIfUserIsAuthenticated(): FirebaseUser? {
        return auth.currentUser
    }

    fun loginUser(email: String, password: String) {
        _loginState.value = LoginState.Loading

        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    _loginState.value = LoginState.Success(auth.currentUser?.uid)
                } else {
                    _loginState.value = LoginState.Error("Erro ao fazer login: ${task.exception?.message}")
                }
            }
    }

    private fun handleLoginError(exception: Exception?): String {
        return when (exception) {
            is FirebaseAuthInvalidUserException -> "Usuário não encontrado. Verifique seu email ou crie uma conta."
            is FirebaseAuthInvalidCredentialsException -> "Senha inválida. Tente novamente."
            is FirebaseAuthException -> "Erro ao tentar autenticar. Tente novamente mais tarde."
            else -> "Erro desconhecido. Tente novamente mais tarde."
        }
    }

    sealed class LoginState {
        data class Success(val userId: String?) : LoginState()
        data class Error(val message: String) : LoginState()
        object Loading : LoginState()
    }

}
