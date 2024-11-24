package com.ecoactivity.app.ui

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import com.ecoactivity.app.MainActivity
import com.ecoactivity.app.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class RegisterFragment : Fragment() {

    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_register, container, false)

        // Inicializando Firebase Authentication
        auth = FirebaseAuth.getInstance()

        // Referências aos elementos do layout
        val emailEditText = view.findViewById<EditText>(R.id.editTextUsername)
        val passwordEditText = view.findViewById<EditText>(R.id.editTextPassword)
        val registerButton = view.findViewById<Button>(R.id.buttonRegister)
        val loginTextView = view.findViewById<TextView>(R.id.textLoginPrompt)
        val errorTextView = view.findViewById<TextView>(R.id.textViewError)

        // TextViews dos critérios de senha
        val conditionLength = view.findViewById<TextView>(R.id.conditionLength)
        val conditionUppercase = view.findViewById<TextView>(R.id.conditionUppercase)
        val conditionLowercase = view.findViewById<TextView>(R.id.conditionLowercase)
        val conditionDigit = view.findViewById<TextView>(R.id.conditionDigit)
        val conditionSpecial = view.findViewById<TextView>(R.id.conditionSpecial)

        // Validação em tempo real para a senha
        passwordEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                validatePasswordConditions(
                    s.toString(),
                    conditionLength,
                    conditionUppercase,
                    conditionLowercase,
                    conditionDigit,
                    conditionSpecial
                )
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        // Ação ao clicar no botão Registrar
        registerButton.setOnClickListener {
            val email = emailEditText.text.toString().trim()
            val password = passwordEditText.text.toString().trim()

            // Validação dos campos
            if (email.isEmpty() || password.isEmpty()) {
                showError(errorTextView, "Preencha todos os campos!")
                return@setOnClickListener
            }

            // Verifica se a senha é válida
            if (!isPasswordValid(password)) {
                showError(errorTextView, "A senha não atende aos critérios.")
                return@setOnClickListener
            }

            // Registro do usuário no Firebase
            // Modifique a chamada para saveUserToDatabase
            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val user = auth.currentUser
                        if (user != null) {
                            Log.d("RegisterFragment", "Usuário registrado com sucesso: ${user.uid}")
                            saveUserToDatabase(user.uid, email) // Salva os dados no Firebase Database
                        }
                        Toast.makeText(requireContext(), "Cadastro bem-sucedido!", Toast.LENGTH_SHORT).show()
                    } else {
                        val errorMessage = task.exception?.localizedMessage ?: "Erro desconhecido"
                        showError(errorTextView, "Falha no cadastro: $errorMessage")
                    }
                }


        }

        // Navega para o LoginFragment ao clicar no link
        loginTextView.setOnClickListener {
            if (activity is MainActivity) {
                (activity as MainActivity).showAuthFragment(LoginFragment())
            }
        }

        return view
    }

    /**
     * Salva os dados do usuário registrado no Firebase Database.
     */
    private fun saveUserToDatabase(userId: String, email: String) {
        val database = FirebaseDatabase.getInstance().getReference("users")
        val userData = mapOf(
            "id" to userId,
            "email" to email,
            "dateCreated" to System.currentTimeMillis(),
            "aparelhos" to emptyMap<String, Any>() // Inicializa aparelhos como vazio
        )

        Log.d("RegisterFragment", "Tentando salvar usuário: $userId com email: $email no Firebase")

        database.child(userId).setValue(userData)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d("RegisterFragment", "Dados do usuário salvos com sucesso no Firebase.")
                } else {
                    Log.e("RegisterFragment", "Erro ao salvar dados no Firebase: ${task.exception?.message}")
                }
            }
    }


    /**
     * Verifica se a senha atende aos critérios:
     * - Pelo menos 8 caracteres
     * - Pelo menos uma letra maiúscula
     * - Pelo menos uma letra minúscula
     * - Pelo menos um número
     * - Pelo menos um caractere especial
     */
    private fun isPasswordValid(password: String): Boolean {
        return password.length >= 8 &&
                password.any { it.isUpperCase() } &&
                password.any { it.isLowerCase() } &&
                password.any { it.isDigit() } &&
                password.any { !it.isLetterOrDigit() }
    }

    /**
     * Atualiza visualmente os critérios de senha com base no input do usuário.
     */
    private fun validatePasswordConditions(
        password: String,
        conditionLength: TextView,
        conditionUppercase: TextView,
        conditionLowercase: TextView,
        conditionDigit: TextView,
        conditionSpecial: TextView
    ) {
        conditionLength.setTextColor(getConditionColor(password.length >= 8))
        conditionUppercase.setTextColor(getConditionColor(password.any { it.isUpperCase() }))
        conditionLowercase.setTextColor(getConditionColor(password.any { it.isLowerCase() }))
        conditionDigit.setTextColor(getConditionColor(password.any { it.isDigit() }))
        conditionSpecial.setTextColor(getConditionColor(password.any { !it.isLetterOrDigit() }))
    }

    /**
     * Retorna a cor para indicar se uma condição foi atendida.
     */
    private fun getConditionColor(isValid: Boolean): Int {
        return if (isValid) {
            requireContext().getColor(R.color.green) // Verde para condições atendidas
        } else {
            requireContext().getColor(R.color.error_red) // Vermelho para condições não atendidas
        }
    }

    /**
     * Exibe uma mensagem de erro em um TextView.
     */
    private fun showError(errorTextView: TextView, message: String) {
        errorTextView.text = message
        errorTextView.visibility = View.VISIBLE
    }
}
