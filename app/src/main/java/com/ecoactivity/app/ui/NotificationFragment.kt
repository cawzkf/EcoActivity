package com.ecoactivity.app.ui.notification

import android.app.TimePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.ecoactivity.app.R
import com.ecoactivity.app.databinding.FragmentNotificationBinding
import com.ecoactivity.app.ui.NotificationViewModel

class NotificationFragment : Fragment() {

    private var _binding: FragmentNotificationBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: NotificationViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewModel = ViewModelProvider(this).get(NotificationViewModel::class.java)

        _binding = FragmentNotificationBinding.inflate(inflater, container, false)
        val root = binding.root

        setupObservers()
        setupListeners()

        return root
    }

    private fun setupObservers() {
        viewModel.notificationEnabled.observe(viewLifecycleOwner) { isEnabled ->
            binding.switchNotifications.isChecked = isEnabled
        }

        viewModel.timeSelected.observe(viewLifecycleOwner) { time ->
            binding.timePicker.hour = time.first
            binding.timePicker.minute = time.second
        }

        viewModel.frequency.observe(viewLifecycleOwner) { frequency ->
            when (frequency) {
                "Diariamente" -> binding.radioGroupFrequency.check(R.id.radio_daily)
                "Semanalmente" -> binding.radioGroupFrequency.check(R.id.radio_weekly)
                "Mensalmente" -> binding.radioGroupFrequency.check(R.id.radio_monthly)
            }
        }

        viewModel.customDays.observe(viewLifecycleOwner) { days ->
            binding.layoutCustomDays.visibility =
                if (days.isNotEmpty()) View.VISIBLE else View.GONE
            // Atualize o estado dos ToggleButtons aqui
        }
    }

    private fun setupListeners() {
        binding.switchNotifications.setOnCheckedChangeListener { _, isChecked ->
            val currentHour = binding.timePicker.hour
            val currentMinute = binding.timePicker.minute
            val frequency = when (binding.radioGroupFrequency.checkedRadioButtonId) {
                R.id.radio_daily -> "Diariamente"
                R.id.radio_weekly -> "Semanalmente"
                R.id.radio_monthly -> "Mensalmente"
                else -> "Diariamente"
            }

            val customDays = mutableSetOf<String>()
            if (binding.checkCustomDays.isChecked) {
                if (binding.toggleSunday.isChecked) customDays.add("Domingo")
                if (binding.toggleMonday.isChecked) customDays.add("Segunda")
                if (binding.toggleTuesday.isChecked) customDays.add("Terça")
                if (binding.toggleWednesday.isChecked) customDays.add("Quarta")
                if (binding.toggleThursday.isChecked) customDays.add("Quinta")
                if (binding.toggleFriday.isChecked) customDays.add("Sexta")
                if (binding.toggleSaturday.isChecked) customDays.add("Sábado")
            }

            viewModel.saveNotificationSettings(
                requireContext(),
                isEnabled = isChecked,
                hour = currentHour,
                minute = currentMinute,
                frequency = frequency,
                days = customDays
            )

            Toast.makeText(
                requireContext(),
                "Configurações salvas com sucesso!",
                Toast.LENGTH_SHORT
            ).show()
        }

        binding.checkCustomDays.setOnCheckedChangeListener { _, isChecked ->
            binding.layoutCustomDays.visibility = if (isChecked) View.VISIBLE else View.GONE
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
