package com.ecoactivity.app.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException

class LoginViewModel : ViewModel() {

    // Autentica com Firebase
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    // LiveData para representar o estado do login
    private val _loginState = MutableLiveData<LoginState>()
    val loginState: LiveData<LoginState> get() = _loginState

    // Função para logar com o Firebase
    fun loginUser(email: String, password: String) {
        _loginState.value = LoginState.Loading

        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    _loginState.value = LoginState.Success
                } else {
                    _loginState.value = LoginState.Error(handleLoginError(task.exception))
                }
            }
    }

    // Tratamento de erros de login
    private fun handleLoginError(exception: Exception?): String {
        return when (exception) {
            is FirebaseAuthInvalidUserException -> "Usuário não encontrado. Verifique seu email ou crie uma conta."
            is FirebaseAuthInvalidCredentialsException -> "Senha Inválida. Tente Novamente."
            else -> "Erro desconhecido. Tente novamente mais tarde."
        }
    }

    // Classe selada para representar os estados possíveis de login
    sealed class LoginState {
        object Success : LoginState()
        data class Error(val message: String) : LoginState()
        object Loading : LoginState()
    }
}
