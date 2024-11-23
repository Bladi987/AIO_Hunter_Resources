package com.kasolution.aiohunterresources.UI.CajaChica.view.adapter

import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.recyclerview.widget.RecyclerView
import com.kasolution.aiohunterresources.R
import com.kasolution.aiohunterresources.UI.CajaChica.view.model.register
import com.kasolution.aiohunterresources.databinding.ItemRegisterBinding
import java.util.Locale

class RegisterAdapter(
    private val listaRecibida: ArrayList<register>,
    private val onclickListener: (register) -> Unit,
    private val OnClickUpdate: (register, Int) -> Unit,
    private val OnClickDelete: (register, Int) -> Unit
) : RecyclerView.Adapter<RegisterAdapter.viewHolder>() {

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
        holder.render(item, onclickListener, OnClickUpdate, OnClickDelete)
    }

    fun limpiar(){
        listaRecibida.clear()
    }
    inner class viewHolder(view:View) : RecyclerView.ViewHolder(view) {
        val binding = ItemRegisterBinding.bind(view)
        fun render(
            lista: register,
            onclickListener: (register) -> Unit,
            OnClickUpdate: (register, Int) -> Unit,
            OnClickDelete: (register, Int) -> Unit
        ) {
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
                "Movilidad" ->binding.imgIcon.setImageResource(R.drawable.car_icon)
                "Alimentacion" ->binding.imgIcon.setImageResource(R.drawable.comida_icon)
                "Alojamiento" ->binding.imgIcon.setImageResource(R.drawable.hotel_icon)
                else ->binding.imgIcon.setImageResource(R.drawable.otros_icon)
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
                binding.lblMonto.text = String.format(Locale.getDefault() ,"S/ %.2f", MontoTemp.toDoubleOrNull() ?: 0.0)
            }
            binding.cvComprobante.visibility = if (tipoSustento=="Sin sustento") View.GONE else View.VISIBLE

            itemView.setOnClickListener {
                onclickListener(lista)
            }
            itemView.setOnLongClickListener {
                val popupMenu = PopupMenu(binding.lblMonto.context, itemView)
                popupMenu.menuInflater.inflate(R.menu.pop_menu_register, popupMenu.menu)

                popupMenu.setOnMenuItemClickListener { menuItem ->
                    when (menuItem.itemId) {
                        R.id.itVer -> {
                            onclickListener(lista)
                            true
                        }
                        R.id.itEditar -> {
                            OnClickUpdate(lista, adapterPosition)
                            true
                        }
                        R.id.itEliminar -> {
                            OnClickDelete(lista, adapterPosition)
                            true
                        }
                        else -> false
                    }
                }

                popupMenu.gravity = Gravity.END
                popupMenu.show()
                true
            }
        }
    }
}
