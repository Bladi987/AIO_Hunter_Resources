package com.kasolution.aiohunterresources.UI.CajaChica.view.adapter

import android.content.Context
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.recyclerview.widget.RecyclerView
import com.kasolution.aiohunterresources.R
import com.kasolution.aiohunterresources.UI.CajaChica.view.model.file
import com.kasolution.aiohunterresources.UI.CajaChica.view.model.fileDetails
import com.kasolution.aiohunterresources.databinding.ItemArchivosBinding
import com.kasolution.aiohunterresources.databinding.ItemSheetBinding

class FileDetailsAdapter(private val listaRecibida: ArrayList<fileDetails>,
                         private val OnClickListener: (fileDetails) -> Unit,
                         private val OnClickUpdate: (Int) -> Unit,
                         private val OnClickDelete: (Int) -> Unit): RecyclerView.Adapter<FileDetailsAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FileDetailsAdapter.ViewHolder {
        val layoutInflater =
            LayoutInflater.from(parent.context).inflate(R.layout.item_sheet, parent, false)
        return ViewHolder(layoutInflater)
    }

    override fun getItemCount(): Int {
        return listaRecibida.size
    }

    override fun onBindViewHolder(holder: FileDetailsAdapter.ViewHolder, position: Int) {
        val item = listaRecibida[position]
        holder.render(item, OnClickListener,OnClickUpdate,OnClickDelete)
    }
    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val binding = ItemSheetBinding.bind(view)

        fun render(
            lista: fileDetails,
            OnClickListener: (fileDetails) -> Unit,
            OnClickUpdate: (Int) -> Unit,
            OnClickDelete: (Int) -> Unit
        ) {
            itemView.setOnClickListener{OnClickListener(lista)}
            binding.lblNombre.text = lista.nombre


            itemView.setOnLongClickListener() {
                val popupMenu = PopupMenu(binding.imgIcon.context, itemView)
                popupMenu.menuInflater.inflate(R.menu.pop_menu, popupMenu.menu)
                popupMenu.setOnMenuItemClickListener { menuItem ->
                    when (menuItem.itemId) {
                        R.id.menu_item_predeterminado -> {
                            // Acción para la opción 1
                            val preferencesValueConexion =
                                binding.imgIcon.context.getSharedPreferences(
                                    "valuesConexion",
                                    Context.MODE_PRIVATE
                                )
                            val editor = preferencesValueConexion.edit()
                            editor.apply() {
                                putString("ARCHIVO_SELECTED", lista.nombre)
                            }.apply()
                            notifyDataSetChanged()
                            true
                        }
                        R.id.menu_item_renonbrar -> {
                            OnClickUpdate(position)
                            true
                        }
                        R.id.menu_item_eliminar -> {
                            OnClickDelete(position)
                            true
                        }
                        // Agregar más opciones del menú si es necesario
                        else -> false
                    }
                }

                // Agrega los listener para las opciones del menú si es necesario
                popupMenu.gravity = Gravity.END
                popupMenu.show()
                true
            }
        }
    }
    fun limpiar(){
        listaRecibida.clear()
    }
}