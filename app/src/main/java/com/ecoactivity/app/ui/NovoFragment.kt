package com.ecoactivity.app.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import com.ecoactivity.app.R
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class NovoFragment : DialogFragment() {

    private lateinit var viewModel: NovoViewModel
    private lateinit var database: DatabaseReference // Referência ao banco de dados do Firebase

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.new_aparelho_modal, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(requireActivity()).get(NovoViewModel::class.java)
        database = FirebaseDatabase.getInstance().getReference("aparelhos")

        val spinnerDeviceType: Spinner = view.findViewById(R.id.spinnerDeviceType)
        val editTextConsumption: EditText = view.findViewById(R.id.editTextConsumption)
        val editTextQuantity: EditText = view.findViewById(R.id.editTextQuantity)
        val buttonIncrease: Button = view.findViewById(R.id.buttonIncrease)
        val buttonDecrease: Button = view.findViewById(R.id.buttonDecrease)
        val buttonAddDevice: Button = view.findViewById(R.id.buttonAddDevice)

        buttonIncrease.setOnClickListener {
            val currentQuantity = editTextQuantity.text.toString().toIntOrNull() ?: 0
            editTextQuantity.setText((currentQuantity + 1).toString())
        }

        buttonDecrease.setOnClickListener {
            val currentQuantity = editTextQuantity.text.toString().toIntOrNull() ?: 0
            if (currentQuantity > 0) {
                editTextQuantity.setText((currentQuantity - 1).toString())
            }
        }

        buttonAddDevice.setOnClickListener {
            val deviceType = spinnerDeviceType.selectedItem.toString()
            val consumption = editTextConsumption.text.toString().toDoubleOrNull() ?: 0.0
            val quantity = editTextQuantity.text.toString().toIntOrNull() ?: 0

            // Cria um novo aparelho
            val newDevice = Aparelho(deviceType, consumption, quantity)

            // Chama o método do ViewModel para adicionar o aparelho
            viewModel.addAparelho(newDevice) // Atualiza o ViewModel

            // Adiciona o aparelho ao Firebase
            val deviceId = database.push().key // Gera uma chave única
            deviceId?.let {
                database.child(it).setValue(newDevice) // Adiciona o aparelho no Firebase
            }

            // Fecha o modal
            dismiss()
        }
    }
}
