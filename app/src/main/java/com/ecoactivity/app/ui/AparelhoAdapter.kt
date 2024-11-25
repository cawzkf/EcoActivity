package com.ecoactivity.app.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.ecoactivity.app.R
import com.google.firebase.firestore.FirebaseFirestore

class AparelhoAdapter(
    private var aparelhos: List<Aparelho>,
    private var tariff: Double,
    private val showDeleteButton: Boolean = false,
    private val onDeleteClick: ((Aparelho) -> Unit)? = null
) : RecyclerView.Adapter<AparelhoAdapter.ViewHolder>() {

    private val firestore = FirebaseFirestore.getInstance()
    private val selectedItems = mutableSetOf<Aparelho>()

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val typeTextView: TextView = view.findViewById(R.id.textAparelhoType)
        val consumptionTextView: TextView = view.findViewById(R.id.textAparelhoConsumption)
        val costTextView: TextView = view.findViewById(R.id.textAparelhoCost)
        val deleteButton: ImageButton = view.findViewById(R.id.buttonDelete)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.aparelho_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val aparelho = aparelhos[position]

        holder.typeTextView.text = aparelho.tipo
        holder.consumptionTextView.text = "${aparelho.consumoAtual} kWh"
        holder.costTextView.text = "R$ ${calculateCost(aparelho)}"

        // Botão de exclusão
        holder.deleteButton.visibility = if (showDeleteButton) View.VISIBLE else View.GONE
        holder.deleteButton.setOnClickListener {
            deleteAparelhoFromFirestore(aparelho)
            onDeleteClick?.invoke(aparelho)
        }

        // Controle de seleção
        holder.itemView.isSelected = selectedItems.contains(aparelho)
        holder.itemView.setOnClickListener {
            toggleSelection(aparelho)
        }

        // Salva ou atualiza o aparelho no Firestore
        saveOrUpdateAparelhoInFirestore(aparelho)
    }

    override fun getItemCount() = aparelhos.size

    fun updateAparelhos(newAparelhos: List<Aparelho>) {
        aparelhos = newAparelhos
        selectedItems.clear() // Limpa a seleção
        notifyDataSetChanged()
    }

    fun updateTariff(newTariff: Double) {
        tariff = newTariff
        notifyDataSetChanged()
    }

    private fun calculateCost(aparelho: Aparelho): String {
        val cost = aparelho.consumoAtual * aparelho.quantidade * tariff
        return String.format("%.2f", cost)
    }

    fun getSelectedItems(): List<Aparelho> = selectedItems.toList()

    fun selectAll() {
        selectedItems.addAll(aparelhos)
        notifyDataSetChanged()
    }

    private fun toggleSelection(aparelho: Aparelho) {
        if (selectedItems.contains(aparelho)) {
            selectedItems.remove(aparelho)
        } else {
            selectedItems.add(aparelho)
        }
        notifyDataSetChanged()
    }

    /**
     * Salva ou atualiza um aparelho no Firestore.
     */
    private fun saveOrUpdateAparelhoInFirestore(aparelho: Aparelho) {
        val userId = "user_id_example" // Substitua pelo ID real do usuário logado
        val collectionRef = firestore.collection("users").document(userId).collection("aparelhos")

        collectionRef.document(aparelho.id).set(aparelho)
            .addOnSuccessListener {
                // Log ou mensagem de sucesso (opcional)
            }
            .addOnFailureListener { exception ->
                // Log ou mensagem de erro
            }
    }

    /**
     * Remove um aparelho do Firestore.
     */
    private fun deleteAparelhoFromFirestore(aparelho: Aparelho) {
        val userId = "user_id_example" // Substitua pelo ID real do usuário logado
        val collectionRef = firestore.collection("users").document(userId).collection("aparelhos")

        collectionRef.document(aparelho.id).delete()
            .addOnSuccessListener {
                // Log ou mensagem de sucesso (opcional)
            }
            .addOnFailureListener { exception ->
                // Log ou mensagem de erro
            }
    }
}
