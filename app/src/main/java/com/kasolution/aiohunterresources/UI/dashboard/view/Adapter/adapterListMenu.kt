package com.kasolution.aiohunterresources.UI.dashboard.view.Adapter


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.kasolution.aiohunterresources.R
import com.kasolution.aiohunterresources.UI.dashboard.view.Model.itemGrid
import com.kasolution.aiohunterresources.databinding.ItemGridBinding


class adapterListMenu(
    private val listaRecibida: ArrayList<itemGrid>,
    private val OnClickListener: (itemGrid) -> Unit
) : RecyclerView.Adapter<adapterListMenu.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): adapterListMenu.ViewHolder {
        val layoutInflater =
            LayoutInflater.from(parent.context).inflate(R.layout.item_grid, parent, false)
        return ViewHolder(layoutInflater)
    }

    override fun getItemCount(): Int {
        return listaRecibida.size
    }

    override fun onBindViewHolder(holder: adapterListMenu.ViewHolder, position: Int) {
        val item = listaRecibida[position]
        holder.render(item, OnClickListener)
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val binding = ItemGridBinding.bind(view)

        fun render(
            lista: itemGrid,
            OnClickListener: (itemGrid) -> Unit
        ) {
            itemView.setOnClickListener { OnClickListener(lista) }
            binding.ivIconActionMenu.setImageResource(lista.icon!!)
            binding.lblNombre.text = lista.name
        }
    }
}