package com.ecoactivity.app.ui

import androidx.core.app.NotificationCompat.MessagingStyle.Message
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.handleCoroutineException
import java.lang.Exception

class LoginViewModel : ViewModel() {

    //autentica com firebase
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    //LiveData para representar o estado do login
    private val _loginState = MutableLiveData<LoginState>()
    val loginState: LiveData<LoginState> get() = _loginState

    //função para logar com o firebase
    fun loginUser(email: String, password: String){
        _loginState.value = LoginState.Loading

        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener {
                task ->
                if (task.isSuccessful){
                    _loginState.value = LoginState.Sucess
                }
                else{
                    _loginState.value = LoginState.Error(handleLoginError(task.exception))
                }
            }
    }
    //Tratamento de erros  de login
    private  fun handleLoginError(exception: Exception?): String{
        return when (exception){
            is FirebaseAuthInvalidUserException -> "Usuário não encontrado. Verifique seu email ou crie uma conta."
            is FirebaseAuthInvalidCredentialsException -> "Senha Inválida. Tente Novamente."
            else -> "Erro desconhecido. Tente novamente mais tarde."
        }
    }
    //Classe selada para vai representar o estado possíveis de login
    sealed class LoginState{
        object Sucess : LoginState()
        data class Error(val message: String): LoginState()
        object Loading : LoginState()
    }

}