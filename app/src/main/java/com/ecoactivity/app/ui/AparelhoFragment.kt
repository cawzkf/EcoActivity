package com.ecoactivity.app.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ecoactivity.app.R

class AparelhoFragment : Fragment() {

    private lateinit var viewModel: NovoViewModel
    private lateinit var aparelhoAdapter: AparelhoAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var selectAllButton: Button
    private lateinit var deleteSelectedButton: Button
    private lateinit var layoutSelectionControls: View

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_aparelho, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView = view.findViewById(R.id.recyclerViewAparelhos)
        selectAllButton = view.findViewById(R.id.buttonSelectAll)
        deleteSelectedButton = view.findViewById(R.id.buttonDeleteSelected)
        layoutSelectionControls = view.findViewById(R.id.layoutSelectionControls)

        recyclerView.layoutManager = LinearLayoutManager(context)

        viewModel = ViewModelProvider(requireActivity())[NovoViewModel::class.java]
        aparelhoAdapter = AparelhoAdapter(
            aparelhos = listOf(),
            tariff = 0.0,
            showDeleteButton = true, // Habilitar botão de exclusão
            onDeleteClick = { aparelho ->
                viewModel.deleteAparelho(aparelho)
            }
        )

        recyclerView.adapter = aparelhoAdapter

        // Observar as mudanças nos aparelhos no ViewModel
        viewModel.devices.observe(viewLifecycleOwner) { aparelhos ->
            if (aparelhos != null) {
                aparelhoAdapter.updateAparelhos(aparelhos)
            }
        }

        // Observação de alterações na tarifa
        viewModel.tariff.observe(viewLifecycleOwner) { newTariff ->
            aparelhoAdapter.updateTariff(newTariff)
        }

        // Ações do botão "Selecionar Tudo"
        selectAllButton.setOnClickListener {
            aparelhoAdapter.selectAll()
        }

        // Ações do botão "Excluir Selecionados"
        deleteSelectedButton.setOnClickListener {
            val selectedItems = aparelhoAdapter.getSelectedItems()
            viewModel.deleteAparelhos(selectedItems) // Excluir do ViewModel e Firebase
            layoutSelectionControls.visibility = View.GONE
        }
    }
}
