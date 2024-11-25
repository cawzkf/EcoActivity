package com.ecoactivity.app.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseUser

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
                    val userId = auth.currentUser?.uid // ID do usuário autenticado
                    _loginState.value = LoginState.Success(userId)
                } else {
                    _loginState.value = LoginState.Error(handleLoginError(task.exception))
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
