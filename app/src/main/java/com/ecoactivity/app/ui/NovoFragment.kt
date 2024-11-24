package com.ecoactivity.app.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import com.ecoactivity.app.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class NovoFragment : DialogFragment() {

    private lateinit var viewModel: NovoViewModel
    private lateinit var database: DatabaseReference

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.new_aparelho_modal, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Inicializa o ViewModel e a referência ao Firebase
        viewModel = ViewModelProvider(requireActivity())[NovoViewModel::class.java]
        database = FirebaseDatabase.getInstance().getReference("users")

        // Referências aos elementos do layout
        val spinnerTipoAparelho: Spinner = view.findViewById(R.id.spinnerTipoAparelho)
        val editaConsumo: EditText = view.findViewById(R.id.editaConsumo)
        val editaQuantidade: EditText = view.findViewById(R.id.editaQuantidade)
        val editaTaxa: EditText = view.findViewById(R.id.editaTaxa)
        val botaoAumentar: Button = view.findViewById(R.id.botaoAumentar)
        val botaoDiminuir: Button = view.findViewById(R.id.botaoDiminuir)
        val botaoAdicionarAparelho: Button = view.findViewById(R.id.botaoAdicionarAparelho)

        // Configura o Spinner com os tipos de aparelhos
        val adapter = ArrayAdapter.createFromResource(
            requireContext(),
            R.array.tipos_de_aparelhos,
            android.R.layout.simple_spinner_dropdown_item
        )
        spinnerTipoAparelho.adapter = adapter

        // Botões para aumentar e diminuir a quantidade
        botaoAumentar.setOnClickListener {
            val currentQuantity = editaQuantidade.text.toString().toIntOrNull() ?: 0
            editaQuantidade.setText((currentQuantity + 1).toString())
        }

        botaoDiminuir.setOnClickListener {
            val currentQuantity = editaQuantidade.text.toString().toIntOrNull() ?: 0
            if (currentQuantity > 0) {
                editaQuantidade.setText((currentQuantity - 1).toString())
            }
        }

        // Botão para adicionar o aparelho
        botaoAdicionarAparelho.setOnClickListener {
            val tipoAparelho = spinnerTipoAparelho.selectedItem.toString()
            val consumoAtual = editaConsumo.text.toString().toDoubleOrNull() ?: 0.0
            val quantidade = editaQuantidade.text.toString().toIntOrNull() ?: 0
            val taxa = editaTaxa.text.toString().toDoubleOrNull() ?: 0.0

            // Validação dos campos
            if (consumoAtual <= 0.0 || quantidade <= 0 || taxa <= 0.0) {
                Toast.makeText(requireContext(), "Preencha todos os campos corretamente.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val userId = FirebaseAuth.getInstance().currentUser?.uid
            if (userId == null) {
                Toast.makeText(requireContext(), "Usuário não autenticado!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Referência para o nó de aparelhos do usuário
            val userAparelhosRef = database.child(userId).child("aparelhos")
            val deviceId = userAparelhosRef.push().key

            if (deviceId == null) {
                Log.e("NovoFragment", "Falha ao gerar ID único para o aparelho.")
                Toast.makeText(requireContext(), "Erro interno. Tente novamente.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Cria um novo objeto de Aparelho
            val novoAparelho = Aparelho(
                id = deviceId,
                tipo = tipoAparelho,
                consumoAtual = consumoAtual,
                consumoInicial = consumoAtual, // Inicialmente, consumo inicial é igual ao consumo atual
                custo = 0.0, // Calculado no ViewModel se necessário
                quantidade = quantidade,
                dataCadastro = System.currentTimeMillis() // Timestamp em formato String
            )

            // Adiciona o aparelho ao Firebase
            userAparelhosRef.child(deviceId).setValue(novoAparelho)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Log.d("NovoFragment", "Aparelho adicionado ao Firebase: $novoAparelho")
                        viewModel.addAparelho(novoAparelho) // Atualiza o ViewModel
                        Toast.makeText(requireContext(), "Aparelho adicionado com sucesso!", Toast.LENGTH_SHORT).show()
                        dismiss() // Fecha o modal
                    } else {
                        Log.e("NovoFragment", "Erro ao adicionar aparelho: ${task.exception}")
                        Toast.makeText(requireContext(), "Erro ao adicionar o aparelho!", Toast.LENGTH_SHORT).show()
                    }
                }
        }
    }
}
