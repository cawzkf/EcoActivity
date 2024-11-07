package com.ecoactivity.app.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.ecoactivity.app.R

// Adapter para a RecyclerView
class AparelhoAdapter(private var aparelhos: List<Aparelho>, private var tariff: Double) : RecyclerView.Adapter<AparelhoAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val typeTextView: TextView = view.findViewById(R.id.textAparelhoType)
        val consumptionTextView: TextView = view.findViewById(R.id.textAparelhoConsumption)
        val costTextView: TextView = view.findViewById(R.id.textAparelhoCost)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.aparelho_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val aparelho = aparelhos[position]
        holder.typeTextView.text = aparelho.type
        holder.consumptionTextView.text = aparelho.consumption.toString()
        holder.costTextView.text = calculateCost(aparelho.consumption).toString()
    }

    override fun getItemCount() = aparelhos.size

    fun updateAparelhos(newAparelhos: List<Aparelho>) {
        aparelhos = newAparelhos
        notifyDataSetChanged()
    }

    fun updateTariff(newTariff: Double) {
        tariff = newTariff
        notifyDataSetChanged()
    }

    private fun calculateCost(consumption: Double): Double {
        return consumption * tariff
    }
}
