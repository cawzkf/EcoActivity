package com.ecoactivity.app.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.ecoactivity.app.R

class AparelhoAdapter(
    private var aparelhos: List<Aparelho>,
    private var tariff: Double,
    private val showDeleteButton: Boolean = false,
    private val onDeleteClick: ((Aparelho) -> Unit)? = null
) : RecyclerView.Adapter<AparelhoAdapter.ViewHolder>() {

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
            onDeleteClick?.invoke(aparelho)
        }

        // Controle de seleção
        holder.itemView.isSelected = selectedItems.contains(aparelho)
        holder.itemView.setOnClickListener {
            toggleSelection(aparelho)
        }
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
}
