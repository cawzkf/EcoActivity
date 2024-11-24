package com.ecoactivity.app.ui.conta

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.ecoactivity.app.MainActivity
import com.ecoactivity.app.R
import com.google.firebase.auth.FirebaseAuth

class ContaFragment : Fragment() {

    private lateinit var auth: FirebaseAuth
    private lateinit var buttonLogout: Button
    private lateinit var textViewEmail: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_conta, container, false)

        // Inicializa FirebaseAuth
        auth = FirebaseAuth.getInstance()

        // Mapeia os componentes do layout
        buttonLogout = view.findViewById(R.id.buttonLogout)
        textViewEmail = view.findViewById(R.id.textViewEmail)

        // Verifica se o usuário está autenticado e exibe o e-mail
        val currentUser = auth.currentUser
        if (currentUser != null) {
            textViewEmail.text = currentUser.email ?: "E-mail não encontrado"
        } else {
            Toast.makeText(context, "Usuário não autenticado.", Toast.LENGTH_SHORT).show()
        }

        // Configura o botão de logout
        buttonLogout.setOnClickListener {
            logout()
        }

        return view
    }

    private fun logout() {
        auth.signOut() // Encerra a sessão do FirebaseAuth
        Toast.makeText(context, "Logout realizado com sucesso!", Toast.LENGTH_SHORT).show()
        (activity as? MainActivity)?.logoutUser() // Invoca o logout na MainActivity
    }
}
