package com.ecoactivity.app.ui.conta

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.ecoactivity.app.R
import com.ecoactivity.app.viewmodel.ContaViewModel

/**
 * Fragmento para gerenciamento da conta do usuário.
 */
class ContaFragment : Fragment() {

    private lateinit var contaViewModel: ContaViewModel

    private lateinit var textViewEmail: TextView
    private lateinit var editTextEmail: EditText
    private lateinit var editTextSenha: EditText
    private lateinit var editTextCurrentPassword: EditText
    private lateinit var buttonResetEmail: Button
    private lateinit var buttonResetPassword: Button
    private lateinit var buttonChangePassword: Button
    private lateinit var buttonLogout: Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_conta, container, false)

        // Inicializa o ViewModel
        contaViewModel = ViewModelProvider(this)[ContaViewModel::class.java]

        // Mapeia os componentes do layout
        textViewEmail = view.findViewById(R.id.textViewEmail)
        editTextEmail = view.findViewById(R.id.editTextEmail)
        editTextSenha = view.findViewById(R.id.editTextSenha)
        editTextCurrentPassword = view.findViewById(R.id.editTextCurrentPassword)
        buttonResetEmail = view.findViewById(R.id.buttonResetEmail)
        buttonResetPassword = view.findViewById(R.id.buttonResetPassword)
        buttonChangePassword = view.findViewById(R.id.buttonChangePassword)
        buttonLogout = view.findViewById(R.id.buttonLogout)

        // Exibe o e-mail atual do usuário
        val currentUser = contaViewModel.getCurrentUser()
        textViewEmail.text = currentUser?.email ?: "E-mail não encontrado"

        // Configura o botão para redefinir o e-mail
        buttonResetEmail.setOnClickListener {
            val newEmail = editTextEmail.text.toString().trim()
            if (newEmail.isNotEmpty()) {
                contaViewModel.updateEmail(newEmail) { success, message ->
                    if (success) {
                        textViewEmail.text = newEmail
                        Toast.makeText(context, "E-mail atualizado com sucesso!", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(context, "Erro: $message", Toast.LENGTH_SHORT).show()
                    }
                }
            } else {
                Toast.makeText(context, "Por favor, insira um novo e-mail.", Toast.LENGTH_SHORT).show()
            }
        }

        // Configura o botão para redefinir a senha
        buttonResetPassword.setOnClickListener {
            contaViewModel.resetPassword { success, message ->
                if (success) {
                    Toast.makeText(context, "E-mail para redefinição de senha enviado.", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(context, "Erro: $message", Toast.LENGTH_SHORT).show()
                }
            }
        }

        // Configura o botão para alterar a senha
        buttonChangePassword.setOnClickListener {
            val newPassword = editTextSenha.text.toString().trim()
            val currentPassword = editTextCurrentPassword.text.toString().trim()
            if (newPassword.length >= 8 && currentPassword.isNotEmpty()) {
                contaViewModel.reauthenticateUser(currentPassword) { reauthSuccess, reauthMessage ->
                    if (reauthSuccess) {
                        contaViewModel.changePassword(newPassword) { success, message ->
                            if (success) {
                                Toast.makeText(context, "Senha alterada com sucesso!", Toast.LENGTH_SHORT).show()
                            } else {
                                Toast.makeText(context, "Erro ao alterar senha: $message", Toast.LENGTH_SHORT).show()
                            }
                        }
                    } else {
                        Toast.makeText(context, "Erro de reautenticação: $reauthMessage", Toast.LENGTH_SHORT).show()
                    }
                }
            } else {
                Toast.makeText(context, "A senha deve ter pelo menos 8 caracteres.", Toast.LENGTH_SHORT).show()
            }
        }

        // Configura o botão de logout
        buttonLogout.setOnClickListener {
            contaViewModel.logout()
            Toast.makeText(context, "Logout realizado com sucesso!", Toast.LENGTH_SHORT).show()
            activity?.finish()
        }

        return view
    }
}
