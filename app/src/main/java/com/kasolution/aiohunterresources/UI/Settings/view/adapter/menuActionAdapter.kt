package com.kasolution.aiohunterresources.UI.Settings.view.adapter

import android.app.AlertDialog
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.kasolution.aiohunterresources.R
import com.kasolution.aiohunterresources.UI.Settings.view.model.funcionModel
import com.kasolution.aiohunterresources.databinding.ItemMenuFunctionBinding


class menuActionAdapter(
    private val listaRecibida: ArrayList<funcionModel>,
    private val onClickListener: (funcionModel, Int) -> Unit
) : RecyclerView.Adapter<menuActionAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater =
            LayoutInflater.from(parent.context).inflate(R.layout.item_menu_function, parent, false)
        return ViewHolder(layoutInflater)
    }

    override fun getItemCount(): Int {
        return listaRecibida.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = listaRecibida[position]
        holder.render(item, onClickListener)
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val binding = ItemMenuFunctionBinding.bind(view)
        fun render(lista: funcionModel, onClickListener: (funcionModel, Int) -> Unit) {
            binding.ivIcon.setImageResource(lista.icon)
            binding.tvNameFunction.text = lista.name

            itemView.setOnClickListener {
                onClickListener(lista, bindingAdapterPosition)
            }
        }
    }

    fun limpiar() {
        listaRecibida.clear()
        notifyDataSetChanged()
    }
}