package com.kasolution.aiohunterresources.UI.CajaChica.view.adapter

import android.content.res.ColorStateList
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.kasolution.aiohunterresources.R
import com.kasolution.aiohunterresources.UI.CajaChica.view.model.file
import com.kasolution.aiohunterresources.databinding.ItemArchivosBinding

class FileAdapter(
    private val listaRecibida: ArrayList<file>,
    private val onClickListener: (file, Int, Int) -> Unit,
    private val onClickDeselect: () -> Unit
) : RecyclerView.Adapter<FileAdapter.ViewHolder>() {
    private var selectedItemPosition: Int? = null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FileAdapter.ViewHolder {
        val layoutInflater =
            LayoutInflater.from(parent.context).inflate(R.layout.item_archivos, parent, false)
        return ViewHolder(layoutInflater)
    }

    override fun getItemCount(): Int {
        return listaRecibida.size
    }

    override fun onBindViewHolder(holder: FileAdapter.ViewHolder, position: Int) {
        val item = listaRecibida[position]
        holder.render(item, position == selectedItemPosition, onClickListener, onClickDeselect)
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val binding = ItemArchivosBinding.bind(view)

        fun render(
            lista: file,
            isSelected: Boolean,  // Estado de selección
            onClickListener: (file, Int, Int) -> Unit,
            onClickDeselect: () -> Unit
        ) {
            // Actualizar el fondo del ítem usando backgroundTintList según si está seleccionado o no
            val tintColor = if (isSelected) {
                Color.parseColor("#34495E")  // Color de selección
            } else {
                Color.parseColor("#FFFFFF") // Color o predeterminado
            }
            binding.lblNombre.text = lista.nombre
            binding.cardView1.backgroundTintList = ColorStateList.valueOf(tintColor)
            itemView.setOnClickListener() {
                if (selectedItemPosition!=null)
                    limpiarSeleccion()
                else {
                    onClickListener(lista, 1, adapterPosition)
                }
            }
            itemView.setOnLongClickListener() {
                if(selectedItemPosition!=null) limpiarSeleccion()else{
                    selectedItemPosition = adapterPosition
                    notifyDataSetChanged() // Actualizamos la vista
                    onClickListener(lista, 2, position)
                }
                true
            }
        }
    }

    fun limpiar() {
        listaRecibida.clear()
    }

    fun limpiarSeleccion() {
        selectedItemPosition = null
        onClickDeselect()
        notifyDataSetChanged()
    }
}