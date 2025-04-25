package com.kasolution.aiohunterresources.UI.FichasTecnicas.view.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.kasolution.aiohunterresources.R
import com.kasolution.aiohunterresources.UI.FichasTecnicas.view.model.VehicleModel
import com.kasolution.aiohunterresources.UI.FichasTecnicas.view.model.menuOption
import com.kasolution.aiohunterresources.core.PopupMenuHelper
import com.kasolution.aiohunterresources.databinding.ItemModelBinding
import java.util.Locale


class ModelAdapter(
    private val listaRecibida: ArrayList<VehicleModel>,
    private val onClickListener: (VehicleModel, Int,Int) -> Unit,
    private val tipoUsuario: String
) : RecyclerView.Adapter<ModelAdapter.ViewHolder>() {
    private var copylist: ArrayList<VehicleModel> = ArrayList()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater =
            LayoutInflater.from(parent.context).inflate(R.layout.item_model, parent, false)
        return ViewHolder(layoutInflater)
    }
    init {
        copylist.addAll(listaRecibida)
    }
    override fun getItemCount(): Int {
        if (listaRecibida.size>copylist.size){
            copylist.clear()
            copylist.addAll(listaRecibida)
        }
        return listaRecibida.size
    }
    fun limpiar(){
        listaRecibida.clear()
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = listaRecibida[position]
        holder.render(item, onClickListener)
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val binding = ItemModelBinding.bind(view)
        val color = ContextCompat.getColor(view.context, R.color.green)
        fun render(
            lista: VehicleModel,
            onClickListener: (VehicleModel, Int,Int) -> Unit
        ) {
            binding.ivOption.isEnabled = tipoUsuario !in setOf("Invitado", "Colaborador") // Deshabilitar el botón si el usuario es invitado o colaborador
            binding.lblModel.text = lista.modelo
            val valor = lista.basica.split(",")
            if (valor[0] == "1") binding.ivPositivo.setColorFilter(color)
            if (valor[1] == "1") binding.ivGnd.setColorFilter(color)
            if (valor[2] == "1") binding.ivIgn.setColorFilter(color)
            if (valor[3] == "1") binding.ivCorte.setColorFilter(color)
            if (valor[4] == "1") binding.ivPestillos.setColorFilter(color)
            itemView.setOnClickListener { onClickListener(lista, 1,position) }
            binding.ivOption.setOnClickListener() {
                PopupMenuHelper.configureAndShowPopupMenu(
                    itemView.context,
                    it,
                    MenuList(tipoUsuario),
                    object : PopupMenuHelper.PopupMenuItemClickListener {
                        override fun onMenuItemClicked(item: menuOption) {
                            when (item.texto) {
                                "Editar" -> onClickListener(lista, 2,bindingAdapterPosition)
                                "Observar" -> onClickListener(lista, 3,bindingAdapterPosition)
                                "Eliminar" -> onClickListener(lista, 4,bindingAdapterPosition)
                            }
                        }

                    })
            }
        }

        private fun MenuList(tipo: String): ArrayList<menuOption> {
            val lista: ArrayList<menuOption> = ArrayList()
            when (tipo) {
                "Administrador" -> {
                    lista.add(menuOption(R.drawable.ic_edit, "Editar"))
                    lista.add(menuOption(R.drawable.ic_flag, "Observar"))
                    lista.add(menuOption(R.drawable.ic_delete, "Eliminar"))
                }

                "Developer" -> {
                    lista.add(menuOption(R.drawable.ic_edit, "Editar"))
                    lista.add(menuOption(R.drawable.ic_flag, "Observar"))
                    lista.add(menuOption(R.drawable.ic_delete, "Eliminar"))
                }
            }
            return lista
        }

    }
    // Función para filtrar la lista
    fun filter(text: String) {

        listaRecibida.clear() // Limpiar la lista filtrada

        if (text.isEmpty()) {
            listaRecibida.addAll(copylist) // Si el texto de búsqueda está vacío, mostrar la lista original
        } else {
            val filterPattern = text.toUpperCase(Locale.getDefault()).trim()
            for (item in copylist) {
                if (item.modelo.toUpperCase(Locale.getDefault()).contains(filterPattern)) {
                    listaRecibida.add(item) // Agregar elementos que coinciden con el patrón de búsqueda
                }
            }
        }
        notifyDataSetChanged() // Notificar al adaptador que los datos han cambiado
    }
}