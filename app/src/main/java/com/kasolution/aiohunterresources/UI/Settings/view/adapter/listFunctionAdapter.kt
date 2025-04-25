package com.kasolution.aiohunterresources.UI.Settings.view.adapter


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.kasolution.aiohunterresources.R
import com.kasolution.aiohunterresources.UI.Settings.view.model.funcionModelList
import com.kasolution.aiohunterresources.databinding.ItemListFunctionBinding


class listFunctionAdapter(
    private val listaRecibida: ArrayList<funcionModelList>,
    private val onClickListener: (funcionModelList, Int) -> Unit
) : RecyclerView.Adapter<listFunctionAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater =
            LayoutInflater.from(parent.context).inflate(R.layout.item_list_function, parent, false)
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
        val binding = ItemListFunctionBinding.bind(view)
        fun render(lista: funcionModelList, onClickListener: (funcionModelList, Int) -> Unit) {
            binding.ivIcon.setImageResource(lista.icon)
            binding.tvNameFunction.text = lista.name
            binding.tvItemContent.text = lista.content

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