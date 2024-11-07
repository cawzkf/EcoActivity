package com.ecoactivity.app.ui.conta

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.ecoactivity.app.R
import com.ecoactivity.app.viewmodel.ContaViewModel
import com.google.firebase.auth.FirebaseAuth

class ContaFragment : Fragment() {

    private lateinit var auth: FirebaseAuth
    private lateinit var contaViewModel: ContaViewModel
    private lateinit var editTextEmail: EditText
    private lateinit var editTextSenha: EditText
    private lateinit var buttonLogout: Button
    private lateinit var buttonResetEmail: Button
    private lateinit var buttonResetPassword: Button
    private lateinit var textViewEmail: TextView
    private lateinit var textViewSenha: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_conta, container, false)
        auth = FirebaseAuth.getInstance()
        contaViewModel = ViewModelProvider(this).get(ContaViewModel::class.java)

        editTextEmail = view.findViewById(R.id.editTextEmail)
        editTextSenha = view.findViewById(R.id.editTextSenha)
        buttonLogout = view.findViewById(R.id.buttonLogout)
        buttonResetEmail = view.findViewById(R.id.buttonResetEmail)
        buttonResetPassword = view.findViewById(R.id.buttonResetPassword)
        textViewEmail = view.findViewById(R.id.textViewEmail)
        textViewSenha = view.findViewById(R.id.textViewSenha)

        val currentUser = auth.currentUser

        currentUser?.let { user ->
            textViewEmail.text = user.email ?: "E-mail não encontrado"
            textViewSenha.text = "******"
        }

        buttonLogout.setOnClickListener {
            contaViewModel.logout()
            findNavController().navigate(R.id.nav_login)
        }

        buttonResetEmail.setOnClickListener {
            val newEmail = editTextEmail.text.toString()
            if (newEmail.isNotEmpty()) {
                val user = auth.currentUser
                user?.updateEmail(newEmail)
                    ?.addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            textViewEmail.text = newEmail
                        } else {

                            textViewEmail.text = "Falha ao atualizar e-mail"
                        }
                    }
            } else {

                textViewEmail.text = "Digite um novo e-mail"
            }
        }

        buttonResetPassword.setOnClickListener {
            val newPassword = editTextSenha.text.toString()
            if (newPassword.isNotEmpty()) {
                val user = auth.currentUser
                user?.updatePassword(newPassword)
                    ?.addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            textViewSenha.text = "******"
                        } else {

                            textViewSenha.text = "Falha ao atualizar senha"
                        }
                    }
            } else {

                textViewSenha.text = "Digite uma nova senha"
            }
        }

        return view
    }
}
