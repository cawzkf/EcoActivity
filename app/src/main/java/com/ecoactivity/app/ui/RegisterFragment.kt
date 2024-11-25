package com.ecoactivity.app.ui

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.fragment.app.commit
import com.ecoactivity.app.MainActivity
import com.ecoactivity.app.R

/**
 * Fragmento para registro de novos usuários.
 */

class RegisterFragment : Fragment() {

    private lateinit var registerViewModel: RegisterViewModel // ViewModel para gerenciar lógica do registro

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_register, container, false)

        registerViewModel = ViewModelProvider(this)[RegisterViewModel::class.java]

        val emailEditText = view.findViewById<EditText>(R.id.editTextUsername) // Campo de e-mail
        val passwordEditText = view.findViewById<EditText>(R.id.editTextPassword) // Campo de senha
        val registerButton = view.findViewById<Button>(R.id.buttonRegister) // Botão de registro
        val errorTextView = view.findViewById<TextView>(R.id.textViewError) // Exibe erros
        val loginPromptTextView = view.findViewById<TextView>(R.id.textLoginPrompt) // Link para login

        // Critérios de senha
        val conditionLength = view.findViewById<TextView>(R.id.conditionLength)
        val conditionDigit = view.findViewById<TextView>(R.id.conditionDigit)
        val conditionUppercase = view.findViewById<TextView>(R.id.conditionUppercase)
        val conditionLowercase = view.findViewById<TextView>(R.id.conditionLowercase)
        val conditionSpecial = view.findViewById<TextView>(R.id.conditionSpecial)

        // Observa mudanças no ViewModel para atualizar a interface
        registerViewModel.registrationError.observe(viewLifecycleOwner) { error ->
            error?.let {
                errorTextView.text = it
                errorTextView.visibility = View.VISIBLE
            }
        }

        registerViewModel.registrationSuccess.observe(viewLifecycleOwner) { success ->
            if (success) {
                // Redireciona para a MainActivity
                val intent = Intent(requireContext(), MainActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
            }
        }

        // Adiciona listener para validar a senha em tempo real
        passwordEditText.addTextChangedListener(object : android.text.TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                registerViewModel.validatePasswordConditions(
                    s.toString(),
                    conditionLength,
                    conditionUppercase,
                    conditionLowercase,
                    conditionDigit,
                    conditionSpecial
                )
            }

            override fun afterTextChanged(s: android.text.Editable?) {}
        })

        // Botão de registro
        registerButton.setOnClickListener {
            val email = emailEditText.text.toString().trim()
            val password = passwordEditText.text.toString().trim()
            registerViewModel.registerUser(email, password)
        }

        // Adiciona clique no TextView para navegar para LoginFragment
        loginPromptTextView.setOnClickListener {
            parentFragmentManager.commit {
                replace(R.id.auth_fragment_container, LoginFragment())
            }
        }

        return view
    }
}