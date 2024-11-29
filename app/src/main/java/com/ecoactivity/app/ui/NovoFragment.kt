package com.ecoactivity.app.ui

import NovoViewModel
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import com.ecoactivity.app.R

class NovoFragment : DialogFragment() {

    private lateinit var viewModel: NovoViewModel
    private lateinit var botaoAdicionarAparelho: Button

    // Variável para armazenar a última taxa digitada
    private var ultimaTaxa: Double = 0.0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.new_aparelho_modal, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(this)[NovoViewModel::class.java]

        // Referências aos elementos do layout
        val spinnerTipoAparelho: Spinner = view.findViewById(R.id.spinnerTipoAparelho)
        val editaConsumo: EditText = view.findViewById(R.id.editaConsumo)
        val editaQuantidade: EditText = view.findViewById(R.id.editaQuantidade)
        val editaTaxa: EditText = view.findViewById(R.id.editaTaxa)
        val botaoAumentar: Button = view.findViewById(R.id.botaoAumentar)
        val botaoDiminuir: Button = view.findViewById(R.id.botaoDiminuir)
        botaoAdicionarAparelho = view.findViewById(R.id.botaoAdicionarAparelho)

        // Configura o Spinner com os tipos de aparelhos
        val adapter = ArrayAdapter.createFromResource(
            requireContext(),
            R.array.tipos_de_aparelhos,
            android.R.layout.simple_spinner_dropdown_item
        )
        spinnerTipoAparelho.adapter = adapter

        // Define a quantidade inicial como 1
        editaQuantidade.setText("1")

        // Define a taxa com o valor da última taxa
        editaTaxa.setText(ultimaTaxa.toString())

        // Aumentar a quantidade
        botaoAumentar.setOnClickListener {
            val currentQuantity = editaQuantidade.text.toString().toIntOrNull() ?: 1
            editaQuantidade.setText((currentQuantity + 1).toString())
        }

        // Diminuir a quantidade, mas nunca permitir que ela seja menor que 1
        botaoDiminuir.setOnClickListener {
            val currentQuantity = editaQuantidade.text.toString().toIntOrNull() ?: 1
            if (currentQuantity > 1) {
                editaQuantidade.setText((currentQuantity - 1).toString())
            }
        }

        // Listener para habilitar/desabilitar o botão "Adicionar"
        val textWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                validarCampos(
                    editaConsumo.text.toString(),
                    editaQuantidade.text.toString(),
                    editaTaxa.text.toString()
                )
            }

            override fun afterTextChanged(s: Editable?) {}
        }

        editaConsumo.addTextChangedListener(textWatcher)
        editaQuantidade.addTextChangedListener(textWatcher)
        editaTaxa.addTextChangedListener(textWatcher)

        // Botão para adicionar o aparelho
        botaoAdicionarAparelho.setOnClickListener {
            val tipoAparelho = spinnerTipoAparelho.selectedItem.toString()
            val consumoInicial = editaConsumo.text.toString().toDoubleOrNull() ?: 0.0
            val quantidade = editaQuantidade.text.toString().toIntOrNull() ?: 1
            val taxa = editaTaxa.text.toString().toDoubleOrNull() ?: ultimaTaxa

            // Atualiza a última taxa digitada
            ultimaTaxa = taxa

            // Cria o objeto Aparelho
            val novoAparelho = Aparelho(
                tipo = tipoAparelho,
                consumoInicial = consumoInicial,
                consumoAtual = consumoInicial,
                quantidade = quantidade,
                custo = consumoInicial * quantidade * taxa,
                dataCadastro = System.currentTimeMillis(),
                statusAp = true
            )

            // Salva o aparelho usando o ViewModel
            viewModel.addAparelho(novoAparelho)
            Toast.makeText(requireContext(), "Aparelho adicionado com sucesso!", Toast.LENGTH_SHORT).show()
            dismiss() // Fecha o modal
        }
    }

    /**
     * Valida os campos e habilita/desabilita o botão de adicionar.
     */
    private fun validarCampos(consumo: String, quantidade: String, taxa: String) {
        botaoAdicionarAparelho.isEnabled = consumo.isNotEmpty() &&
                quantidade.isNotEmpty() &&
                taxa.isNotEmpty() &&
                consumo.toDoubleOrNull() != null &&
                quantidade.toIntOrNull() != null &&
                taxa.toDoubleOrNull() != null
    }
}
