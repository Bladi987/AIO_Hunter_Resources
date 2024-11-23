package com.kasolution.aiohunterresources.UI.FichasTecnicas.view.adapter

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import android.widget.Toast
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.kasolution.aiohunterresources.R
import com.kasolution.aiohunterresources.UI.FichasTecnicas.view.model.VehicleModel
import com.kasolution.aiohunterresources.UI.FichasTecnicas.view.model.menuOption
import com.kasolution.aiohunterresources.core.CustomPicasso
import com.kasolution.aiohunterresources.core.PopupMenuHelper
import com.kasolution.aiohunterresources.databinding.ItemShowModelBinding
import com.squareup.picasso.Callback


class ApproveAdapter(
    private val listaRecibida: ArrayList<VehicleModel>,
    private val OnClickListener: (VehicleModel, Int, Int) -> Unit
) : RecyclerView.Adapter<ApproveAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater =
            LayoutInflater.from(parent.context).inflate(R.layout.item_show_model, parent, false)
        return ViewHolder(layoutInflater)
    }

    override fun getItemCount(): Int {
        return listaRecibida.size
    }
    fun limpiar(){
        listaRecibida.clear()
    }
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = listaRecibida[position]
        holder.render(item, OnClickListener)
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val binding = ItemShowModelBinding.bind(view)

        @SuppressLint("DiscouragedPrivateApi")
        fun render(
            lista: VehicleModel,
            OnClickListener: (VehicleModel, Int, Int) -> Unit
        ) {
            val customPicasso = CustomPicasso.getInstance(itemView.context)
            binding.itemViewShimmer.startShimmer()

            customPicasso.load("https://drive.google.com/uc?export=view&id=${lista.imagen}").into(binding.imgPicture,
                object : Callback {
                    override fun onSuccess() {
                        //binding.imgIcon.animate().alpha(1f).setDuration(300)
                        binding.itemViewShimmer.stopShimmer()
                        binding.itemViewModel.isVisible = true
                        binding.itemViewShimmer.isVisible = false
                        binding.imgPicture.animate().alpha(1f).duration = 800
                    }

                    override fun onError(e: Exception?) {

                    }

                })
            binding.lblComentarios.setOnClickListener() {
                if (binding.llComentarios.isVisible) {
                    binding.llComentarios.visibility = View.GONE
                    binding.lblComentarios.text = "Mostrar Comentarios >>>"
                } else {
                    binding.llComentarios.visibility = View.VISIBLE
                    binding.lblComentarios.text = "Ocultar Comentarios >>>"
                }
            }
            binding.tvBrandModel.isVisible = true
            binding.tvBrandModel.text = "${lista.marca} ${lista.modelo}"
            binding.lblAuthor.text = extraerIniciales(lista.autor)
            binding.imgPicture.setOnClickListener() {
                OnClickListener(
                    lista,
                    3,
                    bindingAdapterPosition
                )
            }
            binding.tvComent.text = lista.comentarios
            when (lista.estado) {
                "Revision" -> {
                    binding.tvEtiquetaEstado.text = lista.estado
                    binding.tvEtiquetaEstado.isVisible = true
                    binding.tvEtiquetaEstado.setBackgroundColor(Color.parseColor("#D6CD27"))
                }

                "Observado" -> {
                    binding.tvEtiquetaEstado.text = lista.estado
                    binding.tvEtiquetaEstado.isVisible = true
                    binding.tvEtiquetaEstado.setBackgroundColor(Color.parseColor("#DF1818"))
                }
            }

            binding.ivOpciones.setOnClickListener() {
                PopupMenuHelper.configureAndShowPopupMenu(
                    it.context,
                    it,
                    crearMenu(),
                    object : PopupMenuHelper.PopupMenuItemClickListener {
                        override fun onMenuItemClicked(item: menuOption) {
                            when (item.texto) {
                                "Aprobar" -> {
                                    OnClickListener(lista, 1, bindingAdapterPosition)
                                }

                                "Eliminar" -> {
                                    OnClickListener(lista, 2, bindingAdapterPosition)
                                }
                            }
                        }

                    })
            }


        }
    }

    private fun crearMenu(): ArrayList<menuOption> {
        val lista: ArrayList<menuOption> = ArrayList()
        lista.add(menuOption(R.drawable.ic_aprobar, "Aprobar"))
        lista.add(menuOption(R.drawable.ic_delete, "Eliminar"))
        return lista
    }

    private fun extraerIniciales(author: String): String {
        var nameinitial = ""
        var part = author.split(" ")
        if (part?.size!! > 1) nameinitial = part[0][0].toString() + part[1][0].toString()
        else nameinitial = part[0][0].toString() + part[0][1].toString()
        return nameinitial
    }
}