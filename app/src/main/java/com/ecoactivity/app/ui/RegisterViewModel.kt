package com.ecoactivity.app.ui

import android.content.Context
import android.widget.TextView
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ecoactivity.app.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.firestore.FirebaseFirestore

/**
 * ViewModel para gerenciar o registro de novos usuários.
 */
class RegisterViewModel : ViewModel() {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance() // Instância do Firebase Authentication
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance() // Instância do Firestore

    private val _registrationError = MutableLiveData<String>() // Mensagem de erro de registro
    val registrationError: LiveData<String> get() = _registrationError

    private val _registrationSuccess = MutableLiveData<Boolean>() // Sucesso no registro
    val registrationSuccess: LiveData<Boolean> get() = _registrationSuccess

    /**
     * Registra um novo usuário com e-mail e senha.
     */
    fun registerUser(email: String, password: String) {
        if (email.isEmpty() || password.isEmpty()) {
            _registrationError.value = "Preencha todos os campos!"
            return
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _registrationError.value = "E-mail inválido!"
            return
        }

        if (!isPasswordValid(password)) {
            _registrationError.value = "A senha não atende aos critérios."
            return
        }

        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val user = auth.currentUser
                user?.let {
                    saveUserToFirestore(Usuarios(it.uid, email, System.currentTimeMillis())) // Salva usando a classe Usuarios
                    _registrationSuccess.value = true
                }
            } else {
                handleRegistrationError(task.exception)
            }
        }
    }

    /**
     * Salva os dados do usuário no Firestore.
     */
    private fun saveUserToFirestore(usuario: Usuarios) {
        firestore.collection("users") // Coleção chamada "users"
            .document(usuario.id) // Documento com ID do usuário
            .set(usuario) // Salva os dados diretamente como objeto
            .addOnSuccessListener {
                // Sucesso ao salvar
            }
            .addOnFailureListener { e ->
                // Erro ao salvar
                _registrationError.value = "Erro ao salvar dados no Firestore: ${e.message}"
            }
    }

    /**
     * Trata erros de registro do Firebase Authentication.
     */
    private fun handleRegistrationError(exception: Exception?) {
        val errorMessage = when (exception) {
            is FirebaseAuthInvalidCredentialsException -> "E-mail inválido."
            is FirebaseAuthUserCollisionException -> "Este e-mail já está em uso."
            else -> "Erro desconhecido. Tente novamente."
        }
        _registrationError.value = errorMessage
    }

    /**
     * Valida se a senha atende aos critérios de segurança.
     */
    private fun isPasswordValid(password: String): Boolean {
        return password.length >= 8 &&
                password.any { it.isUpperCase() } &&
                password.any { it.isLowerCase() } &&
                password.any { it.isDigit() } &&
                password.any { !it.isLetterOrDigit() }
    }

    /**
     * Atualiza os critérios de validação da senha dinamicamente.
     */
    fun validatePasswordConditions(
        password: String,
        conditionLength: TextView,
        conditionUppercase: TextView,
        conditionLowercase: TextView,
        conditionDigit: TextView,
        conditionSpecial: TextView
    ) {
        conditionLength.setTextColor(getConditionColor(password.length >= 8, conditionLength.context))
        conditionUppercase.setTextColor(getConditionColor(password.any { it.isUpperCase() }, conditionLength.context))
        conditionLowercase.setTextColor(getConditionColor(password.any { it.isLowerCase() }, conditionLength.context))
        conditionDigit.setTextColor(getConditionColor(password.any { it.isDigit() }, conditionLength.context))
        conditionSpecial.setTextColor(getConditionColor(password.any { !it.isLetterOrDigit() }, conditionLength.context))
    }

    /**
     * Retorna a cor com base na validade do critério.
     */
    private fun getConditionColor(isValid: Boolean, context: Context): Int {
        return if (isValid) {
            context.getColor(R.color.green) // Cor para critério atendido
        } else {
            context.getColor(R.color.error_red) // Cor para critério não atendido
        }
    }
}
