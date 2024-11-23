package com.kasolution.aiohunterresources.UI.FichasTecnicas.view.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.kasolution.aiohunterresources.R
import com.kasolution.aiohunterresources.UI.FichasTecnicas.view.model.Brand
import com.kasolution.aiohunterresources.databinding.ItemBrandBinding
import com.kasolution.aiohunterresources.core.CustomPicasso

import com.squareup.picasso.Callback
import java.util.Locale


class BrandAdapter(
    private val listaRecibida: ArrayList<Brand>,
    private val OnClickListener: (Brand) -> Unit
) : RecyclerView.Adapter<BrandAdapter.ViewHolder>() {
    private var copylist: ArrayList<Brand> = ArrayList()
    var mod=0
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater =
            LayoutInflater.from(parent.context).inflate(R.layout.item_brand, parent, false)
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

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = listaRecibida[position]
        holder.render(item, OnClickListener)
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val binding = ItemBrandBinding.bind(view)

        fun render(
            lista: Brand,
            OnClickListener: (Brand) -> Unit
        ) {
            itemView.setOnClickListener { OnClickListener(lista) }
            binding.shimerEffect.startShimmer()
            val customPicasso = CustomPicasso.getInstance(itemView.context)
            customPicasso.load("https://drive.google.com/uc?export=view&id=${lista.icon}").into(binding.imgIcon,
                object : Callback {
                    override fun onSuccess() {
                        //binding.imgIcon.animate().alpha(1f).setDuration(300)
                        binding.shimerEffect.stopShimmer()
                        binding.shimmerBrandElement.isVisible=false
                        binding.viewBrand.isVisible=true
                        binding.lblBrand.isVisible=true
                        binding.imgIcon.animate().alpha(1f).setDuration(800)
                    }

                    override fun onError(e: Exception?) {

                    }

                })
            binding.lblBrand.text = lista.brand
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
                if (item.brand.toUpperCase(Locale.getDefault()).contains(filterPattern)) {
                    listaRecibida.add(item) // Agregar elementos que coinciden con el patrón de búsqueda
                }
            }
        }
        notifyDataSetChanged() // Notificar al adaptador que los datos han cambiado
    }
    fun limpiar(){
        listaRecibida.clear()
    }
}