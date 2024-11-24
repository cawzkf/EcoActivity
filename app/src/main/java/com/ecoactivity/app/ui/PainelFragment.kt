package com.ecoactivity.app.ui

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import java.util.Calendar
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ecoactivity.app.R
import com.ecoactivity.app.databinding.FragmentPainelBinding

class PainelFragment : Fragment() {

    private var _binding: FragmentPainelBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: PainelViewModel
    private lateinit var adapter: AparelhoAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Configura o ViewModel associado ao fragmento
        viewModel = ViewModelProvider(this).get(PainelViewModel::class.java)

        // Configura o binding do layout
        _binding = FragmentPainelBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Configura o RecyclerView
        binding.recyclerViewAparelhos.layoutManager = LinearLayoutManager(context)
        adapter = AparelhoAdapter(
            aparelhos = listOf(), // Inicialmente vazio
            tariff = 0.0, // Tarifa padrão
            showDeleteButton = false // Botão de exclusão desabilitado no PainelFragment
        )
        binding.recyclerViewAparelhos.adapter = adapter

        // Observa as alterações nos aparelhos
        viewModel.devices.observe(viewLifecycleOwner) { aparelhos ->
            adapter.updateAparelhos(aparelhos)
        }

        // Observa as alterações na tarifa
        viewModel.tariff.observe(viewLifecycleOwner) { newTariff ->
            adapter.updateTariff(newTariff)
        }

        // Configura o botão de calendário
        binding.calendarButton.setOnClickListener {
            showDatePickerDialog()
        }

        // Observa a data selecionada
        viewModel.selectedDate.observe(viewLifecycleOwner) { date ->
            binding.editTextFilterDate.setText(date)
            viewModel.filterDataByDate(date) // Atualiza os dados com base na data
        }
    }

    private fun showDatePickerDialog() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            requireContext(),
            { _, selectedYear, selectedMonth, selectedDay ->
                val selectedDate = String.format(
                    "%04d-%02d-%02d", selectedYear, selectedMonth + 1, selectedDay
                )
                viewModel.setSelectedDate(selectedDate)
            },
            year,
            month,
            day
        )
        datePickerDialog.show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
