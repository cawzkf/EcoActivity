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
import androidx.navigation.fragment.findNavController
import com.ecoactivity.app.MainActivity
import com.ecoactivity.app.R

class LoginFragment : Fragment() {

    private val viewModel: LoginViewModel by viewModels()

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

        editTextEmail = view.findViewById(R.id.editTextUsername)
        editTextPassword = view.findViewById(R.id.editTextPassword)
        buttonLogin = view.findViewById(R.id.buttonLogin)
        textViewError = view.findViewById(R.id.textViewError)
        textViewRegistrationPrompt = view.findViewById(R.id.textRegistration)

        buttonLogin.setOnClickListener {
            val email = editTextEmail.text.toString().trim()
            val password = editTextPassword.text.toString().trim()

            if (isValidInput(email, password)) {
                viewModel.loginUser(email, password)
            } else {
                showError("Email ou senha inválidos")
            }
        }

        viewModel.loginState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is LoginViewModel.LoginState.Success -> navigateToMainActivity()
                is LoginViewModel.LoginState.Error -> showError(state.message)
                is LoginViewModel.LoginState.Loading -> buttonLogin.isEnabled = false
            }
        }

        val textViewRegistrationPrompt = view.findViewById<TextView>(R.id.textRegistration)
        textViewRegistrationPrompt.setOnClickListener {
            (activity as MainActivity).showAuthFragment(RegisterFragment()) // Chama o método na MainActivity
        }

        return view
    }

    private fun navigateToMainActivity() {
        val intent = Intent(activity, MainActivity::class.java)
        startActivity(intent)
        activity?.finish()
    }

    private fun showError(message: String) {
        textViewError.text = message
        textViewError.visibility = View.VISIBLE
        buttonLogin.isEnabled = true
    }

    private fun isValidInput(email: String, password: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches() && password.length >= 8
    }
}
