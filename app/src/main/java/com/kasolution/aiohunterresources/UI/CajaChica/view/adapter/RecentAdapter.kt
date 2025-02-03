package com.kasolution.aiohunterresources.UI.CajaChica.view.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.kasolution.aiohunterresources.R
import com.kasolution.aiohunterresources.UI.CajaChica.view.model.recent
import com.kasolution.aiohunterresources.databinding.ItemListRecentBinding
import java.text.SimpleDateFormat
import java.util.*

class RecentAdapter(
    private val listaRecibida: List<recent>,
    private val onClickListener: (recent, Int, Int) -> Unit
) : RecyclerView.Adapter<RecentAdapter.ViewHolder>() {
    private var selectedItemPosition: Int? = null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecentAdapter.ViewHolder {
        val layoutInflater =
            LayoutInflater.from(parent.context).inflate(R.layout.item_list_recent, parent, false)
        return ViewHolder(layoutInflater)
    }

    override fun getItemCount(): Int {
        return listaRecibida.size
    }

    override fun onBindViewHolder(holder: RecentAdapter.ViewHolder, position: Int) {
        val item = listaRecibida[position]
        holder.render(item, onClickListener)
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val binding = ItemListRecentBinding.bind(view)

        fun render(
            lista: recent,
            onClickListener: (recent, Int, Int) -> Unit
        ) {
            binding.ivLogo.setImageResource(lista.icon)
            binding.lblTitulo.text = lista.titulo
            binding.lblDetalle.text = lista.detalle

            binding.lblFecha.text = formatDate(lista.fecha)
        }
    }

    fun formatDate(dateString: String): String {
        // Ajusta el formato de fecha al formato que tienes en el String (dd/MM/yyyy)
        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

        val date = dateFormat.parse(dateString) // Convierte el String a Date
        val today = Calendar.getInstance() // Obtiene la fecha actual
        val yesterday = Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, -1) } // Obtiene la fecha de ayer

        val dateCalendar = Calendar.getInstance().apply { time = date } // Convierte la fecha a Calendar

        return when {
            dateCalendar.get(Calendar.YEAR) == today.get(Calendar.YEAR) &&
                    dateCalendar.get(Calendar.DAY_OF_YEAR) == today.get(Calendar.DAY_OF_YEAR) -> {
                "Hoy" // Si la fecha es hoy
            }
            dateCalendar.get(Calendar.YEAR) == yesterday.get(Calendar.YEAR) &&
                    dateCalendar.get(Calendar.DAY_OF_YEAR) == yesterday.get(Calendar.DAY_OF_YEAR) -> {
                "Ayer" // Si la fecha es ayer
            }
            else -> {
                dateFormat.format(date) // Devuelve la fecha tal cual si no es hoy ni ayer
            }
        }
    }

    fun limpiarSeleccion() {
        selectedItemPosition = null
        notifyDataSetChanged()
    }
}