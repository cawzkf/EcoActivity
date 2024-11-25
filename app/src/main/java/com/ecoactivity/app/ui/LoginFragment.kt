package com.ecoactivity.app.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.ecoactivity.app.MainActivity
import com.ecoactivity.app.R

class LoginFragment : Fragment() {

    private lateinit var loginViewModel: LoginViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_login, container, false)

        loginViewModel = ViewModelProvider(this)[LoginViewModel::class.java]

        val emailEditText = view.findViewById<EditText>(R.id.editTextUsername)
        val passwordEditText = view.findViewById<EditText>(R.id.editTextPassword)
        val loginButton = view.findViewById<Button>(R.id.buttonLogin)
        val registerTextView = view.findViewById<TextView>(R.id.textRegistration)

        loginButton.setOnClickListener {
            val email = emailEditText.text.toString().trim()
            val password = passwordEditText.text.toString().trim()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(requireContext(), "Preencha todos os campos!", Toast.LENGTH_SHORT).show()
            } else {
                loginViewModel.loginUser(email, password)
            }
        }

        loginViewModel.loginState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is LoginViewModel.LoginState.Loading -> {
                    Toast.makeText(requireContext(), "Entrando...", Toast.LENGTH_SHORT).show()
                }
                is LoginViewModel.LoginState.Success -> {
                    // Fecha o fragmento e acessa a MainActivity
                    (activity as MainActivity).hideAuthFragment()
                }
                is LoginViewModel.LoginState.Error -> {
                    Toast.makeText(requireContext(), state.message, Toast.LENGTH_SHORT).show()
                }
            }
        }

        registerTextView.setOnClickListener {
            (activity as MainActivity).showAuthFragment(RegisterFragment())
        }

        return view
    }
}
