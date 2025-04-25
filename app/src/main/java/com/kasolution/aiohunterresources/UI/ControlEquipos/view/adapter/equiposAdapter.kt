package com.kasolution.aiohunterresources.UI.ControlEquipos.view.adapter

import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.PorterDuff
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.TooltipCompat
import androidx.recyclerview.widget.RecyclerView
import com.kasolution.aiohunterresources.R
import com.kasolution.aiohunterresources.UI.ControlEquipos.view.model.equipos
import com.kasolution.aiohunterresources.databinding.ItemEquiposBinding


class equiposAdapter(
    private val listaRecibida: ArrayList<equipos>,
    private val onClickListener: (equipos, Int, Int) -> Unit,
    private val onClickDeselect: () -> Unit
) : RecyclerView.Adapter<equiposAdapter.ViewHolder>() {
    private var selectedItemPosition: Int? = null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater =
            LayoutInflater.from(parent.context).inflate(R.layout.item_equipos, parent, false)
        return ViewHolder(layoutInflater)
    }

    override fun getItemCount(): Int {
        return listaRecibida.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = listaRecibida[position]
        holder.render(item, position == selectedItemPosition, onClickListener, onClickDeselect)
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        val binding = ItemEquiposBinding.bind(view)

        fun render(
            lista: equipos,
            isSelected: Boolean,
            onClickListener: (equipos, Int, Int) -> Unit,
            onClickDeselect: () -> Unit
        ) {
            // Actualizar el fondo del ítem usando backgroundTintList según si está seleccionado o no
            if (lista.estado == "Disponible") {
                val tintColor = if (isSelected) {
                    Color.parseColor("#34495E")  // Color de selección
                } else {
                    Color.parseColor("#FFFFFF") // Color o predeterminado
                }
                binding.cardView1.backgroundTintList = ColorStateList.valueOf(tintColor)
            }

            //itemView.setOnClickListener { OnClickListener(lista) }
            binding.tvVid.text = lista.vid
            binding.tvMarca.text = lista.marca
            binding.tvModel.text = lista.modelo

            when (lista.estado) {
                "Dañado" -> {
                    binding.ivFlag.visibility = View.VISIBLE
                    binding.ivFlag.setImageResource(R.drawable.ic_flag)
                    binding.ivFlag.setColorFilter(Color.parseColor("#FF0000"))

                }

                "No disponible" -> {
                    binding.ivFlag.visibility = View.VISIBLE
                    binding.ivFlag.setImageResource(R.drawable.ic_flag)
                    binding.ivFlag.setColorFilter(Color.parseColor("#FF9800"))
                }

                "Cambio" -> {
                    binding.ivFlag.visibility = View.VISIBLE
                    binding.ivFlag.setImageResource(R.drawable.icon_change)
                    binding.ivFlag.setColorFilter(Color.parseColor("#23F800"))
                }

                else -> {
                    binding.ivFlag.visibility = View.INVISIBLE
                }
            }
            binding.ivFlag.setOnClickListener(){
                TooltipCompat.setTooltipText(it, lista.estado)
            }
            itemView.setOnClickListener() {
                if (selectedItemPosition != null) {
                    limpiarSeleccion()
                    onClickListener(lista, 0, adapterPosition)
                } else {
                    onClickListener(lista, 1, adapterPosition)
                }
            }

            itemView.setOnLongClickListener {
                if (lista.estado == "Disponible") {
                    if (selectedItemPosition != null) limpiarSeleccion() else {
                        selectedItemPosition = adapterPosition
                        notifyDataSetChanged() // Actualizamos la vista
                        onClickListener(lista, 2, position)
                    }
                }
                true
            }
        }
    }

    fun limpiar() {
        listaRecibida.clear()
        notifyDataSetChanged()
    }

    fun limpiarSeleccion() {
        selectedItemPosition = null
        onClickDeselect()
        notifyDataSetChanged()
    }
}