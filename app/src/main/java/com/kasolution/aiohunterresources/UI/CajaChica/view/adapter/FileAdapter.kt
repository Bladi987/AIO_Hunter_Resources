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
import com.kasolution.aiohunterresources.databinding.ItemArchivosBinding

class FileAdapter(private val listaRecibida: ArrayList<file>,
                  private val OnClickListener: (file) -> Unit,
                  private val OnClickUpdate: (file,Int) -> Unit,
                  private val OnClickDelete: (Int,Int) -> Unit): RecyclerView.Adapter<FileAdapter.ViewHolder>() {
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
        holder.render(item, OnClickListener,OnClickUpdate,OnClickDelete)
    }
    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val binding = ItemArchivosBinding.bind(view)

        fun render(
            lista: file,
            OnClickListener: (file) -> Unit,
            OnClickUpdate: (file,Int) -> Unit,
            OnClickDelete: (Int,Int) -> Unit
        ) {
            itemView.setOnClickListener{OnClickListener(lista)}
//            if (lista.nombre != "LIQUIDACIONES")
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

                            OnClickUpdate(lista,position)
                            true
                        }
                        R.id.menu_item_eliminar -> {
                            OnClickDelete(lista.id!!.toInt(),position)
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