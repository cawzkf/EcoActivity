package com.ecoactivity.app.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.handleCoroutineException

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
                    _loginState.value = LoginState.Error(handleLoginError(task.exception)
                }
            }
    }


}