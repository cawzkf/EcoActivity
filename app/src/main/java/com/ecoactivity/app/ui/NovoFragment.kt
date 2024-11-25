package com.ecoactivity.app.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import com.ecoactivity.app.R

class NovoFragment : DialogFragment() {

    private lateinit var viewModel: AparelhoViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.new_aparelho_modal, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(requireActivity())[AparelhoViewModel::class.java]

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

        // Aumentar a quantidade
        botaoAumentar.setOnClickListener {
            val currentQuantity = editaQuantidade.text.toString().toIntOrNull() ?: 0
            editaQuantidade.setText((currentQuantity + 1).toString())
        }

        // Diminuir a quantidade
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

            // Valida os campos
            if (consumoAtual <= 0.0 || quantidade <= 0 || taxa <= 0.0) {
                Toast.makeText(requireContext(), "Preencha todos os campos corretamente.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Cria o objeto Aparelho
            val novoAparelho = Aparelho(
                tipo = tipoAparelho,
                consumoAtual = consumoAtual,
                consumoInicial = consumoAtual,
                quantidade = quantidade,
                custo = consumoAtual * quantidade * taxa,
                dataCadastro = System.currentTimeMillis()
            )

            // Adiciona ao Firestore via ViewModel
            viewModel.saveAparelho(novoAparelho)
            Toast.makeText(requireContext(), "Aparelho adicionado com sucesso!", Toast.LENGTH_SHORT).show()
            dismiss() // Fecha o modal
        }
    }
}
