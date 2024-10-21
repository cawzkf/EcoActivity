package com.ecoactivity.app.ui

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.ecoactivity.app.MainActivity
import com.ecoactivity.app.R

class LoginFragment : Fragment() {

    // ViewModel para gerenciar o estado do login
    private val viewModel: LoginViewModel by viewModels()

    // Declaração dos elementos da interface
    private lateinit var editTextEmail: EditText
    private lateinit var editTextPassword: EditText
    private lateinit var buttonLogin: Button
    private lateinit var textViewError: TextView
    private lateinit var textViewRegistrationPrompt: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_login, container, false)

        // Inicializar as views
        editTextEmail = view.findViewById(R.id.editTextUsername)
        editTextPassword = view.findViewById(R.id.editTextPassword)
        buttonLogin = view.findViewById(R.id.buttonLogin)
        textViewError = view.findViewById(R.id.textViewError)
        textViewRegistrationPrompt = view.findViewById(R.id.textRegistration)

        // Adicionar evento para o botão de login
        buttonLogin.setOnClickListener {
            val email = editTextEmail.text.toString()
            val password = editTextPassword.text.toString()

            if (isValidInput(email, password)) {
                viewModel.loginUser(email, password) // Executa o login
            } else {
                showError("Email ou senha inválidos") // Exibe erro se input for inválido
            }
        }

        // Observar o estado do login
        viewModel.loginState.observe(viewLifecycleOwner, Observer { state ->
            when (state) {
                is LoginState.Success -> navigateToMainActivity() // Sucesso no login
                is LoginState.Error -> showError(state.message) // Erro no login
                is LoginState.Loading -> buttonLogin.isEnabled = false // Desativa botão durante o login
            }
        })

        return view
    }

    // Função para fazer a navegação depois do login
    private fun navigateToMainActivity() {
        val intent = Intent(activity, MainActivity::class.java)
        startActivity(intent)
        activity?.finish()
    }

    // Exibe mensagem de erro
    private fun showError(message: String) {
        textViewError.text = message
        textViewError.visibility = View.VISIBLE
        buttonLogin.isEnabled = true
    }

    // Função para validar os inputs de e-mail e senha
    private fun isValidInput(email: String, password: String): Boolean {
        // Valida se o e-mail é válido e se a senha tem pelo menos 8 caracteres
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches() && password.length >= 8
    }
}
