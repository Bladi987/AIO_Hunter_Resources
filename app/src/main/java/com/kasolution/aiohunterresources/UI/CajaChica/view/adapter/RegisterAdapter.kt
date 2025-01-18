package com.kasolution.aiohunterresources.UI.CajaChica.view.adapter

import android.content.res.ColorStateList
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.kasolution.aiohunterresources.R
import com.kasolution.aiohunterresources.UI.CajaChica.view.model.register
import com.kasolution.aiohunterresources.databinding.ItemRegisterBinding
import java.util.Locale

class RegisterAdapter(
    private val listaRecibida: ArrayList<register>,
    private val onClickListener: (register, Int, Int) -> Unit,
    private val onClickDeselect: () -> Unit
) : RecyclerView.Adapter<RegisterAdapter.viewHolder>() {
    private var selectedItemPosition: Int? = null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RegisterAdapter.viewHolder {
        val layoutInflater =
            LayoutInflater.from(parent.context).inflate(R.layout.item_register, parent, false)
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
        val binding = ItemRegisterBinding.bind(view)
        fun render(
            lista: register,
            isSelected: Boolean,  // Estado de selección
            onClickListener: (register, Int, Int) -> Unit,
            onClickDeselect: () -> Unit
        ) {
            // Actualizar el fondo del ítem usando backgroundTintList según si está seleccionado o no
            val tintColor = if (isSelected) {
                Color.parseColor("#34495E")  // Color de selección
            } else {
                Color.parseColor("#FFFFFF") // Color o predeterminado
            }
            binding.cardView1.backgroundTintList = ColorStateList.valueOf(tintColor)
            // Mostrar u ocultar los iconos de acción según el estado de selección
            binding.llActions.visibility = if (isSelected) View.VISIBLE else View.GONE
            binding.lblfecha.visibility = if (isSelected) View.GONE else View.VISIBLE

            binding.lblid.text = lista.id
            binding.lblfecha.text = lista.fecha
            binding.lblciudad.text = lista.ciudad
            binding.lbltipoDoc.text = lista.tipoDoc
            binding.lblNroDoc.text = lista.nroDoc
            binding.lblProveedor.text = lista.proveedor
            binding.lblDescripcion.text = lista.descripcion

            // Manejo de tipo de gasto


            val tipoGastos = when {
                lista.c_movilidad.isNotEmpty() -> Pair("Con sustento", "Movilidad")
                lista.c_alimentacion.isNotEmpty() -> Pair("Con sustento", "Alimentacion")
                lista.c_alojamiento.isNotEmpty() -> Pair("Con sustento", "Alojamiento")
                lista.c_otros.isNotEmpty() -> Pair("Con sustento", "Otros")
                lista.s_movilidad.isNotEmpty() -> Pair("Sin sustento", "Movilidad")
                lista.s_alimentacion.isNotEmpty() -> Pair("Sin sustento", "Alimentacion")
                lista.s_alojamiento.isNotEmpty() -> Pair("Sin sustento", "Alojamiento")
                lista.s_otros.isNotEmpty() -> Pair("Sin sustento", "Otros")
                else -> null
            }

            val (tipoSustento, tipoGastoDetalle) = tipoGastos ?: Pair("", "")
            binding.lbltipoGasto.text = tipoSustento
            binding.lbltipoGastoDetalle.text = tipoGastoDetalle
            when (tipoGastoDetalle) {
                "Movilidad" -> binding.imgIcon.setImageResource(R.drawable.car_icon)
                "Alimentacion" -> binding.imgIcon.setImageResource(R.drawable.comida_icon)
                "Alojamiento" -> binding.imgIcon.setImageResource(R.drawable.hotel_icon)
                else -> binding.imgIcon.setImageResource(R.drawable.otros_icon)
            }
            // Manejo de monto
            val monto = when (tipoGastoDetalle) {
                "Movilidad" -> lista.c_movilidad
                "Alimentacion" -> lista.c_alimentacion
                "Alojamiento" -> lista.c_alojamiento
                "Otros" -> lista.c_otros
                else -> ""
            }

            var MontoTemp = if (monto.isNotEmpty()) monto else when (tipoGastoDetalle) {
                "Movilidad" -> lista.s_movilidad
                "Alimentacion" -> lista.s_alimentacion
                "Alojamiento" -> lista.s_alojamiento
                "Otros" -> lista.s_otros
                else -> ""
            }
            if (MontoTemp.startsWith("S")) {
                binding.lblMonto.text = MontoTemp
            } else {
                MontoTemp = MontoTemp.replace(",", ".")
                binding.lblMonto.text =
                    String.format(Locale.getDefault(), "S/ %.2f", MontoTemp.toDoubleOrNull() ?: 0.0)
            }
            binding.cvComprobante.visibility =
                if (tipoSustento == "Sin sustento") View.GONE else View.VISIBLE

            itemView.setOnClickListener {
                // Deseleccionar todos los elementos
                limpiarSeleccion()
            }
            itemView.setOnLongClickListener {
                // Seleccionar el elemento
                if(selectedItemPosition!=null) limpiarSeleccion()else{
                    selectedItemPosition = adapterPosition
                    notifyDataSetChanged()
                }
                true
            }
            binding.btnEdit.setOnClickListener {
                onClickListener(lista, 1, adapterPosition)
            }
            binding.btnDelete.setOnClickListener {
                onClickListener(lista, 2, adapterPosition)
            }
        }
    }

    fun limpiarSeleccion() {
        selectedItemPosition = null
        onClickDeselect()
        notifyDataSetChanged()
    }
}
