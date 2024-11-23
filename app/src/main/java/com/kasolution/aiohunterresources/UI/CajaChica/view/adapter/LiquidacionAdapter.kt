package com.kasolution.aiohunterresources.UI.CajaChica.view.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.kasolution.aiohunterresources.R
import com.kasolution.aiohunterresources.UI.CajaChica.view.model.liquidacion
import com.kasolution.aiohunterresources.UI.FichasTecnicas.view.model.menuOption
import com.kasolution.aiohunterresources.core.PopupMenuHelper
import com.kasolution.aiohunterresources.databinding.ItemLiquidacionBinding

class LiquidacionAdapter(
    private val listaRecibida: ArrayList<liquidacion>,
    private val onClickListener: (liquidacion, Int, Int) -> Unit
) : RecyclerView.Adapter<LiquidacionAdapter.viewHolder>() {

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
        holder.render(item, onClickListener)
    }

    fun limpiar() {
        listaRecibida.clear()
    }

    inner class viewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val binding = ItemLiquidacionBinding.bind(view)
        fun render(
            lista: liquidacion,
            onclickListener: (liquidacion, Int, Int) -> Unit
        ) {

            binding.lblFecha.text = lista.fecha
            binding.lblConcepto.text = lista.concepto
            binding.lblMonto.text = lista.monto
            binding.lblEstado.text = lista.estado
            binding.btnconfirmPay.setOnClickListener(){
                onclickListener(lista, 1, adapterPosition)
            }
            binding.btnAnular.setOnClickListener(){
                onclickListener(lista, 2, adapterPosition)
            }


        }
    }

}
