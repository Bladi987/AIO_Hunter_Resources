package com.kasolution.aiohunterresources.UI.CajaChica.view.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import com.kasolution.aiohunterresources.R
import com.kasolution.aiohunterresources.UI.CajaChica.view.model.fileDetails

class CustomSpinnerAdapter(context: Context, private val items: List<fileDetails>) :
    ArrayAdapter<fileDetails>(context, R.layout.spinner_item, items) {

    init {
        setDropDownViewResource(R.layout.spinner_item)
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = convertView ?: LayoutInflater.from(context)
            .inflate(R.layout.spinner_item, parent, false)
        val item = getItem(position)
        val textView = view.findViewById<TextView>(R.id.spinner_text)
//        val imageView = view.findViewById<ImageView>(R.id.spinner_icon)

        // Configurar el texto sin la fecha
        textView.text = item?.nombre

        // Si necesitas un ícono, lo puedes asignar aquí (por ejemplo, una flecha o un ícono específico)
//        imageView.visibility = View.VISIBLE  // Puedes usar un ícono que indica que es desplegable
        //imageView.setImageResource(R.drawable.ic_dropdown_arrow)  // Coloca un ícono de flecha si quieres

        return view
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = convertView ?: LayoutInflater.from(context)
            .inflate(R.layout.spinner_item, parent, false)
        val item = getItem(position)
        val textView = view.findViewById<TextView>(R.id.spinner_text)

        textView.text = item?.nombre

        return view
    }
}
