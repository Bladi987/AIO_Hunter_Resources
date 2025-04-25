package com.kasolution.aiohunterresources.core

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.PopupWindow
import android.widget.TextView
import com.kasolution.aiohunterresources.R
import com.kasolution.aiohunterresources.UI.FichasTecnicas.view.model.menuOption

object PopupMenuHelper {
    interface PopupMenuItemClickListener {
        fun onMenuItemClicked(item: menuOption)
    }

    private var popupWindow: PopupWindow? = null
    fun configureAndShowPopupMenu(
        context: Context,
        anchorView: View,
        datosMenu: List<menuOption>,
        listener: PopupMenuItemClickListener
    ) {

        val inflater = LayoutInflater.from(context)
        val popupView = inflater.inflate(R.layout.popup_menu_layout, null)
        val menuContainer = popupView.findViewById<LinearLayout>(R.id.menuContainer)

        for (dato in datosMenu) {
            // Inflar el diseño del elemento del menú
            val menuItemView = inflater.inflate(R.layout.item_popup_menu, null)

            // Configurar el texto y el icono del elemento del menú
            menuItemView.findViewById<TextView>(R.id.textTextView).text = dato.texto
            menuItemView.findViewById<ImageView>(R.id.iconImageView)
                .setImageResource(dato.icon)

            // Listener para los items del menú
            menuItemView.setOnClickListener {
                listener.onMenuItemClicked(dato)
                popupWindow?.dismiss()
            }

            // Agregar el elemento del menú al contenedor
            menuContainer.addView(menuItemView)
        }

        // Crear y configurar el PopupWindow
        popupWindow = PopupWindow(
            popupView,
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT,
            true
        )
        popupWindow!!.animationStyle = R.style.PopupAnimation
        popupWindow!!.showAsDropDown(anchorView)
    }

    /*
    private fun getMaxPopupHeight(context: Context, numberOfItems: Int): Int {
        val displayMetrics = context.resources.displayMetrics
        val screenHeight = displayMetrics.heightPixels

        // Calcular la altura máxima del PopupWindow en función del número de elementos en la lista
        val maxItemsToShow = 5 // Puedes ajustar este valor según tus necesidades
        val maxHeight = screenHeight * 0.5 // Porcentaje máximo de la pantalla
        val itemHeight = context.resources.getDimensionPixelSize(R.dimen.menu_item_height) // Altura de cada elemento del menú

        return minOf(numberOfItems, maxItemsToShow) * itemHeight // Limitar la altura según el número de elementos
    }
*/
}
