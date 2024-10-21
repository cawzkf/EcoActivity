package com.ecoactivity.app.ui

import androidx.fragment.app.viewModels
import android.os.Bundle
import android.provider.ContactsContract.CommonDataKinds.Email
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.lifecycle.ViewModel
import com.ecoactivity.app.R

class LoginFragment : Fragment() {

    companion object {
        fun newInstance() = LoginFragment()
    }

    //viewmodel para gerenciar o estado do login
    private val viewModel: LoginViewModel by viewModels()

    //declaração dos elementos da interface
    private lateinit var editTextEmail: EditText
    private lateinit var editTextPassword: EditText
    private lateinit var buttonLogin: Button
    private lateinit var textViewError: TextView
    private lateinit var textViewRegistrationPrompt: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // TODO: Use the ViewModel
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_login, container, false)

        //inicializar as views
        editTextEmail = view.findViewById(R.id.editTextUsername)
        editTextPassword =view.findViewById(R.id.editTextPassword)
        buttonLogin = view.findViewById(R.id.buttonLogin)
        textViewError = view.findViewById(R.id.textViewError)
        textViewRegistrationPrompt= view.findViewById(R.id.textRegistration)

    }
}