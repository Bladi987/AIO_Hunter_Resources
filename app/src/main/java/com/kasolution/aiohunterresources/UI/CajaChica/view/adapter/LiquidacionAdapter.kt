package com.kasolution.aiohunterresources.UI.CajaChica.view.adapter

import android.content.res.ColorStateList
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.kasolution.aiohunterresources.R
import com.kasolution.aiohunterresources.UI.CajaChica.view.model.liquidacion
import com.kasolution.aiohunterresources.UI.FichasTecnicas.view.model.menuOption
import com.kasolution.aiohunterresources.core.PopupMenuHelper
import com.kasolution.aiohunterresources.databinding.ItemLiquidacionBinding
import java.util.Locale

class LiquidacionAdapter(
    private val listaRecibida: ArrayList<liquidacion>,
    private val onClickListener: (liquidacion, Int, Int) -> Unit,
    private val onClickDeselect: () -> Unit
) : RecyclerView.Adapter<LiquidacionAdapter.viewHolder>() {
    private var selectedItemPosition: Int? = null

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): LiquidacionAdapter.viewHolder {
        val layoutInflater =
            LayoutInflater.from(parent.context).inflate(R.layout.item_liquidacion, parent, false)
        return viewHolder(layoutInflater)
    }

    override fun getItemCount(): Int {
        return listaRecibida.size
    }

    override fun onBindViewHolder(holder: viewHolder, position: Int) {
        val item = listaRecibida[position]
        holder.render(item, position == selectedItemPosition, onClickListener, onClickDeselect)
    }

    fun limpiar() {
        listaRecibida.clear()
    }

    inner class viewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val binding = ItemLiquidacionBinding.bind(view)
        fun render(
            lista: liquidacion,
            isSelected: Boolean,
            onclickListener: (liquidacion, Int, Int) -> Unit,
            onClickDeselect: () -> Unit
        ) {
            // Actualizar el fondo del ítem usando backgroundTintList según si está seleccionado o no
            val tintColor = if (isSelected) {
                Color.parseColor("#34495E")  // Color de selección
            } else {
                Color.parseColor("#FFFFFF") // Color o predeterminado
            }
            val tintColorEstate = if (lista.estado == "Enviado") {
                Color.parseColor("#A61313")  // Color de selección
            } else {
                Color.parseColor("#4CAF50") // Color o predeterminado
            }
            binding.cardView1.backgroundTintList = ColorStateList.valueOf(tintColor)
            binding.viewColor.backgroundTintList = ColorStateList.valueOf(tintColorEstate)
            //binding.viewColor.setTextColor(tintColorEstate)
            binding.lblFecha.text = lista.fecha
            binding.lblConcepto.text = lista.concepto
            binding.lblMonto.text = String.format(
                Locale.getDefault(),
                "S/ %.2f",
                lista.monto.replace("[^\\d.]".toRegex(), "").toDoubleOrNull() ?: 0.0
            )
            binding.lblEstado.text = lista.estado
            itemView.setOnClickListener() {
                limpiarSeleccion()
            }
            itemView.setOnLongClickListener() {
                // Seleccionar el elemento
                if (selectedItemPosition != null) limpiarSeleccion() else {
                    selectedItemPosition = adapterPosition
                    notifyDataSetChanged()
                    val estado = if (lista.estado == "Enviado") 1 else 2
                    onclickListener(lista, estado, position)
                }
                true
            }
            binding.btnDownload.setOnClickListener() {
                onclickListener(lista, 3, position)
            }
        }
    }

    fun limpiarSeleccion() {
        selectedItemPosition = null
        onClickDeselect()
        notifyDataSetChanged()
    }
}
