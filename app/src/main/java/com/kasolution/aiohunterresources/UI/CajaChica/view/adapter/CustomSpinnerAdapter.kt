package com.kasolution.aiohunterresources.UI.CajaChica.view.adapter

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.kasolution.aiohunterresources.UI.CajaChica.view.model.fileDetails

class CustomSpinnerAdapter(context: Context, private val items: List<fileDetails>) : ArrayAdapter<fileDetails>(context, android.R.layout.simple_spinner_item, items) {

    // Configuración del diseño del item del spinner
    init {
        setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
    }

    // Sobrescribimos el método getView para personalizar la vista del spinner
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = super.getView(position, convertView, parent)
        val item = getItem(position)
        val textView = view.findViewById<TextView>(android.R.id.text1)
        textView.text = item?.nombre
        return view
    }

    // Sobrescribimos el método getDropDownView para personalizar los ítems cuando se despliega el spinner
    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = super.getDropDownView(position, convertView, parent)
        val item = getItem(position)
        val textView = view.findViewById<TextView>(android.R.id.text1)
        textView.text = item?.nombre
        return view
    }

    // Método para obtener el objeto completo cuando se selecciona un ítem
    fun getSelectedItemReal(position: Int): String {
        return items[position].nombreReal
    }
}
