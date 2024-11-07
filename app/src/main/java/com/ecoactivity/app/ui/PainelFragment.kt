package com.ecoactivity.app.ui

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.ecoactivity.app.databinding.FragmentPainelBinding
import com.google.firebase.database.*
import java.util.*
import com.ecoactivity.app.R


class PainelFragment : Fragment() {

    private var _binding: FragmentPainelBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: PainelViewModel // Mudança para usar PainelViewModel
    private lateinit var adapter: AparelhoAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewModel = ViewModelProvider(requireActivity()).get(PainelViewModel::class.java)

        _binding = FragmentPainelBinding.inflate(inflater, container, false)
        val root: View = binding.root

        // Configura o RecyclerView
        binding.recyclerViewAparelhos.layoutManager = LinearLayoutManager(context)
        adapter = AparelhoAdapter(emptyList(), viewModel.tariff.value ?: 0.0)
        binding.recyclerViewAparelhos.adapter = adapter

        // Observe mudanças nos aparelhos
        viewModel.devices.observe(viewLifecycleOwner) { aparelhos ->
            adapter.updateAparelhos(aparelhos)
        }

        // Observe mudanças na tarifa
        viewModel.tariff.observe(viewLifecycleOwner) { newTariff ->
            adapter.updateTariff(newTariff) // Atualiza a tarifa no adapter
        }

        // Configura o botão do calendário
        binding.calendarButton.setOnClickListener {
            showDatePickerDialog()
        }

        // Observe a data selecionada
        viewModel.selectedDate.observe(viewLifecycleOwner) { date ->
            binding.editTextFilterDate.setText(date)
            // Atualiza o RecyclerView com os dados filtrados
            viewModel.filterDataByDate(date)
        }

        return root
    }

    private fun showDatePickerDialog() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        // Aplica o estilo customizado
        val datePickerDialog = DatePickerDialog(
            requireContext(),
            R.style.CustomDatePickerDialog,  // Usando o estilo customizado
            { _, selectedYear, selectedMonth, selectedDay ->
                // Manipulação da data selecionada
                val selectedDate = String.format("%04d-%02d-%02d", selectedYear, selectedMonth + 1, selectedDay)
                viewModel.setSelectedDate(selectedDate)
            },
            year,
            month,
            day
        )

        // Exibe o dialog
        datePickerDialog.show()
    }



    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
