package com.ecoactivity.app.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

/**
 * ViewModel para gerenciamento de conta do usuário.
 */
class ContaViewModel : ViewModel() {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()

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
     * Atualiza o e-mail do usuário e salva no Firestore.
     */
    fun updateEmail(newEmail: String, callback: (Boolean, String?) -> Unit) {
        val currentUser = auth.currentUser
        if (currentUser != null) {
            currentUser.updateEmail(newEmail).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Atualizar o e-mail no Firestore
                    val userId = currentUser.uid
                    firestore.collection("users").document(userId).update("email", newEmail)
                        .addOnSuccessListener {
                            callback(true, null)
                        }
                        .addOnFailureListener { exception ->
                            callback(false, exception.message)
                        }
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
    fun logoutUser(callback: (Boolean) -> Unit) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid

        if (userId != null) {
            // Referência ao Firestore
            val aparelhosRef = FirebaseFirestore.getInstance()
                .collection("users")
                .document(userId)
                .collection("aparelhos")

            // Atualiza o status dos aparelhos para false antes de realizar o logout
            aparelhosRef.get().addOnSuccessListener { documents ->
                for (document in documents) {
                    document.reference.update("statusAp", false).addOnFailureListener { e ->
                        Log.e("Logout", "Erro ao atualizar aparelho: ${e.message}")
                    }
                }
                // Realiza o logout após atualizar
                FirebaseAuth.getInstance().signOut()
                callback(true)
            }.addOnFailureListener { e ->
                Log.e("Logout", "Erro ao buscar aparelhos: ${e.message}")
                callback(false)
            }
        } else {
            // Caso o usuário já esteja deslogado
            callback(false)
        }
    }



}
