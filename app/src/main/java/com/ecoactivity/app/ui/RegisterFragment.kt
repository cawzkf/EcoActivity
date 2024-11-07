package com.ecoactivity.app.ui

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.ecoactivity.app.MainActivity
import com.ecoactivity.app.R
import com.ecoactivity.app.viewmodel.RegisterViewModel

class RegisterFragment : Fragment() {

    private val viewModel: RegisterViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_register, container, false)

        val editTextEmail = view.findViewById<EditText>(R.id.editTextUsername)
        val editTextPassword = view.findViewById<EditText>(R.id.editTextPassword)
        val buttonRegister = view.findViewById<Button>(R.id.buttonRegister)
        val textViewError = view.findViewById<TextView>(R.id.textViewError)

        // Observe o erro de registro
        viewModel.registrationError.observe(viewLifecycleOwner, Observer { errorMessage ->
            textViewError.text = errorMessage
            textViewError.visibility = if (errorMessage.isNotEmpty()) View.VISIBLE else View.GONE
        })

        // Observe o sucesso do registro
        viewModel.registrationSuccess.observe(viewLifecycleOwner, Observer { isSuccess ->
            if (isSuccess) {
                // Navega para MainActivity
                val intent = Intent(requireContext(), MainActivity::class.java)
                startActivity(intent)
                activity?.finish() // Opcional: Fecha a tela de registro
            }
        })

        // Condições da senha
        val conditionLength = view.findViewById<TextView>(R.id.conditionLength)
        val conditionUppercase = view.findViewById<TextView>(R.id.conditionUppercase)
        val conditionLowercase = view.findViewById<TextView>(R.id.conditionLowercase)
        val conditionDigit = view.findViewById<TextView>(R.id.conditionDigit)
        val conditionSpecial = view.findViewById<TextView>(R.id.conditionSpecial)

        // Adiciona listeners para validar a senha enquanto o usuário digita
        editTextPassword.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                validatePasswordConditions(s.toString(), conditionLength, conditionUppercase, conditionLowercase, conditionDigit, conditionSpecial)
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        buttonRegister.setOnClickListener {
            val email = editTextEmail.text.toString().trim()
            val password = editTextPassword.text.toString().trim()

            if (isValidInput(email, password)) {
                if (isValidPassword(password)) {
                    viewModel.registerUser(email, password)
                } else {
                    textViewError.text = "A senha deve atender a todos os critérios."
                    textViewError.visibility = View.VISIBLE
                }
            } else {
                textViewError.text = "Email ou senha inválidos"
                textViewError.visibility = View.VISIBLE
            }
        }

        val textViewRegistrationPrompt = view.findViewById<TextView>(R.id.textLoginPrompt)
        textViewRegistrationPrompt.setOnClickListener {
                (activity as MainActivity).showAuthFragment(LoginFragment()) // Chama o método na MainActivity
            }

        return view
    }

    private fun validatePasswordConditions(password: String, vararg conditions: TextView) {
        // Verifica cada condição e ajusta a cor
        conditions[0].apply {
            setTextColor(if (password.length >= 8) requireContext().getColor(R.color.green) else requireContext().getColor(R.color.error_red))
        }
        conditions[1].apply {
            setTextColor(if (password.any { it.isUpperCase() }) requireContext().getColor(R.color.green) else requireContext().getColor(R.color.error_red))
        }
        conditions[2].apply {
            setTextColor(if (password.any { it.isLowerCase() }) requireContext().getColor(R.color.green) else requireContext().getColor(R.color.error_red))
        }
        conditions[3].apply {
            setTextColor(if (password.any { it.isDigit() }) requireContext().getColor(R.color.green) else requireContext().getColor(R.color.error_red))
        }
        conditions[4].apply {
            setTextColor(if (password.any { !it.isLetterOrDigit() }) requireContext().getColor(R.color.green) else requireContext().getColor(R.color.error_red))
        }
    }

    private fun isValidInput(email: String, password: String): Boolean {
        return isValidEmail(email) && isValidPassword(password)
    }

    private fun isValidEmail(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    private fun isValidPassword(password: String): Boolean {
        return password.length >= 8 &&
                password.any { it.isUpperCase() } &&
                password.any { it.isLowerCase() } &&
                password.any { it.isDigit() } &&
                password.any { !it.isLetterOrDigit() }
    }
}
