package com.kasolution.aiohunterresources.UI.ControlEquipos.view.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.kasolution.aiohunterresources.R
import com.kasolution.aiohunterresources.UI.ControlEquipos.view.model.itemResumen
import com.kasolution.aiohunterresources.databinding.ItemResumenBinding


class resumenAdapter(
    private val listaRecibida: ArrayList<itemResumen>
) : RecyclerView.Adapter<resumenAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater =
            LayoutInflater.from(parent.context).inflate(R.layout.item_resumen, parent, false)
        return ViewHolder(layoutInflater)
    }

    override fun getItemCount(): Int {
        return listaRecibida.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = listaRecibida[position]
        holder.render(item)
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val binding = ItemResumenBinding.bind(view)
        fun render(lista: itemResumen) {
            binding.tvMarca.text = lista.marca
            binding.tvModelo.text = lista.modelo
            binding.tvCantidad.text = lista.cantidad
        }
    }

    fun limpiar() {
        listaRecibida.clear()
        notifyDataSetChanged()
    }
}