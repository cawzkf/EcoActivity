package com.ecoactivity.app.viewmodel

import androidx.lifecycle.ViewModel
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth

/**
 * ViewModel para gerenciamento de conta do usuário.
 */
class ContaViewModel : ViewModel() {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    /**
     * Obtém o usuário atual autenticado.
     */
    fun getCurrentUser() = auth.currentUser

    /**
     * Reautentica o usuário com as credenciais fornecidas.
     */
    fun reauthenticateUser(password: String, callback: (Boolean, String?) -> Unit) {
        val currentUser = auth.currentUser
        val email = currentUser?.email
        if (email != null) {
            val credential = EmailAuthProvider.getCredential(email, password)
            currentUser.reauthenticate(credential).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    callback(true, null)
                } else {
                    callback(false, task.exception?.message)
                }
            }
        } else {
            callback(false, "Usuário não autenticado.")
        }
    }

    /**
     * Atualiza a senha do usuário.
     */
    fun changePassword(newPassword: String, callback: (Boolean, String?) -> Unit) {
        val currentUser = auth.currentUser
        if (currentUser != null) {
            currentUser.updatePassword(newPassword).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    callback(true, null)
                } else {
                    callback(false, task.exception?.message)
                }
            }
        } else {
            callback(false, "Usuário não autenticado.")
        }
    }

    /**
     * Atualiza o e-mail do usuário.
     */
    fun updateEmail(newEmail: String, callback: (Boolean, String?) -> Unit) {
        val currentUser = auth.currentUser
        if (currentUser != null) {
            currentUser.updateEmail(newEmail).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    callback(true, null)
                } else {
                    callback(false, task.exception?.message)
                }
            }
        } else {
            callback(false, "Usuário não autenticado.")
        }
    }

    /**
     * Envia um e-mail para redefinir a senha.
     */
    fun resetPassword(callback: (Boolean, String?) -> Unit) {
        val email = auth.currentUser?.email
        if (email != null) {
            auth.sendPasswordResetEmail(email).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    callback(true, null)
                } else {
                    callback(false, task.exception?.message)
                }
            }
        } else {
            callback(false, "E-mail não encontrado.")
        }
    }

    /**
     * Faz logout do usuário.
     */
    fun logout() {
        auth.signOut()
    }
}
