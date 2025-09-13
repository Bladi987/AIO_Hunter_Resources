package com.kasolution.aiohunterresources.UI.CajaChica.view.adapter

import android.content.res.ColorStateList
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
    private var estadoRegistro: String? = null
    private var selectedItemPosition: Int? = null

    fun updateMyState(newState: String) {
        this.estadoRegistro = newState
        // Si el cambio en la variable afecta a la vista de los ítems, puedes notificar
        // al adaptador para que se actualice.
        notifyDataSetChanged()
    }

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
            binding.llActions.visibility = if (isSelected && estadoRegistro=="Editable") View.VISIBLE else View.GONE
            binding.lblfecha.visibility = if (isSelected) View.GONE else View.VISIBLE

            binding.lblid.text = lista.id
            binding.lblfecha.text = lista.fecha
            binding.lbltipoDoc.text = lista.tipoDoc
            binding.lblNroDoc.text = lista.nroDoc
            binding.lblProveedor.text = lista.proveedor
            binding.lblDescripcion.text = lista.detalle
            binding.lblRuc.text = lista.ruc


            val tipoGastos =
                if (lista.tipoGasto == "0") {
                    when (lista.motivo) {
                        "MOVILIDAD" -> Pair("Con sustento", "Movilidad")
                        "ALIMENTACION" -> Pair("Con sustento", "Alimentacion")
                        "ALOJAMIENTO" -> Pair("Con sustento", "Alojamiento")
                        else -> Pair("Con sustento", "Otros")
                    }
                } else {
                    when (lista.motivo) {
                        "MOVILIDAD" -> Pair("Sin sustento", "Movilidad")
                        "ALIMENTACION" -> Pair("Sin sustento", "Alimentacion")
                        "ALOJAMIENTO" -> Pair("Sin sustento", "Alojamiento")
                        else -> Pair("Sin sustento", "Otros")
                    }
                }


            val (tipoSustento, tipoGastoDetalle) = tipoGastos
            binding.lbltipoGasto.text = tipoSustento
            binding.lbltipoGastoDetalle.text = tipoGastoDetalle
            when (tipoGastoDetalle) {
                "Movilidad" -> binding.imgIcon.setImageResource(R.drawable.car_icon)
                "Alimentacion" -> binding.imgIcon.setImageResource(R.drawable.comida_icon)
                "Alojamiento" -> binding.imgIcon.setImageResource(R.drawable.hotel_icon)
                else -> binding.imgIcon.setImageResource(R.drawable.otros_icon)
            }
            // Manejo de monto
            var monto = lista.monto

//            var MontoTemp = if (monto.isNotEmpty()) monto else when (tipoGastoDetalle) {
//                "Movilidad" -> lista.s_movilidad
//                "Alimentacion" -> lista.s_alimentacion
//                "Alojamiento" -> lista.s_alojamiento
//                "Otros" -> lista.s_otros
//                else -> ""
//            }
            if (monto.startsWith("S")) {
                binding.lblMonto.text = monto
            } else {
                monto = monto.replace(",", ".")
                binding.lblMonto.text =
                    String.format(Locale.getDefault(), "S/ %.2f", monto.toDoubleOrNull() ?: 0.0)
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
