package com.kasolution.aiohunterresources.UI.CajaChica.view.adapter


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.kasolution.aiohunterresources.R
import com.kasolution.aiohunterresources.UI.CajaChica.view.model.fileDetails
import com.kasolution.aiohunterresources.databinding.ItemSheetBinding
import android.animation.ObjectAnimator
import android.content.res.ColorStateList
import android.graphics.Color
import android.view.animation.AccelerateDecelerateInterpolator

class FileDetailsAdapter(
    private val listaRecibida: ArrayList<fileDetails>,
    private val onClickListener: (fileDetails, Int, Int) -> Unit,
    private val onClickDeselect: () -> Unit
) : RecyclerView.Adapter<FileDetailsAdapter.ViewHolder>() {
    private var selectedItemPosition: Int? = null
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): FileDetailsAdapter.ViewHolder {
        val layoutInflater =
            LayoutInflater.from(parent.context).inflate(R.layout.item_sheet, parent, false)
        return ViewHolder(layoutInflater)
    }

    override fun getItemCount(): Int {
        return listaRecibida.size
    }

    override fun onBindViewHolder(holder: FileDetailsAdapter.ViewHolder, position: Int) {
        val item = listaRecibida[position]
        holder.render(item, position == selectedItemPosition, onClickListener, onClickDeselect)
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val binding = ItemSheetBinding.bind(view)

        fun render(
            lista: fileDetails,
            isSelected: Boolean,  // Estado de selección
            onClickListener: (fileDetails, Int, Int) -> Unit,
            onClickDeselect: () -> Unit
        ) {
            val estado = identificarItem(lista.nombreReal)
            if (estado != "Editable") {
                binding.lblEstado.isVisible = true
                binding.lblEstado.text = estado
            } else binding.lblEstado.isVisible = false
            binding.lblNombre.text = lista.nombre

            // Cambiar la imagen del "check" dependiendo de la selección
            val newImageRes = if (isSelected) {
                R.drawable.checkbox // Imagen de círculo con check
            } else {
                R.drawable.sheets_icon // Imagen de círculo vacío
            }

            // Animar el giro horizontal de la imagen
            val rotateAnimator = ObjectAnimator.ofFloat(binding.imgIcon, "rotationY", 180f, 360f)
            rotateAnimator.duration = 300 // Duración de la animación
            rotateAnimator.interpolator =
                AccelerateDecelerateInterpolator() // Interpolador para una animación suave

            // Solo realiza la animación si la imagen cambió
            if (binding.imgIcon.drawable.constantState != binding.imgIcon.context.getDrawable(
                    newImageRes
                )?.constantState
            ) {
                rotateAnimator.start() // Iniciar la animación de rotación
            }

            // Cambiar la imagen después de la animación (para no interferir con la animación)
            binding.imgIcon.setImageResource(newImageRes)

            // Actualizar el fondo del ítem usando backgroundTintList según si está seleccionado o no
            val tintColor = if (isSelected) {
                Color.parseColor("#34495E")  // Color de selección
            } else {
                Color.parseColor("#FFFFFF") // Color o predeterminado
            }
            binding.cardView1.backgroundTintList = ColorStateList.valueOf(tintColor)

            itemView.setOnClickListener {
                // Si el ítem está seleccionado, lo deseleccionamos
                if (selectedItemPosition == adapterPosition) {
                    selectedItemPosition = null
                    onClickDeselect()
                } else {
                    // Si seleccionamos otro ítem, deseleccionamos el anterior y seleccionamos este
                    val previousSelectedPosition = selectedItemPosition
                    selectedItemPosition = adapterPosition
                    // Notificar que todos los elementos deben actualizarse
                    notifyItemChanged(previousSelectedPosition ?: -1) // Actualizar el ítem anterior
                    notifyItemChanged(adapterPosition) // Actualizar el ítem seleccionado
                    onClickListener(lista, 1, adapterPosition)
                }
                notifyDataSetChanged() // Actualizar la vista
            }
//
            // Manejar la acción de mantener presionado (long click)
            itemView.setOnLongClickListener {
                // Seleccionamos el ítem al mantener presionado
                binding.cardView1.backgroundTintList =
                    itemView.context.getColorStateList(R.color.fab_color)
                selectedItemPosition = adapterPosition
                notifyDataSetChanged() // Actualizamos la vista
                onClickListener(lista, 2, position)
                true
            }
        }
    }

    fun limpiar() {
        listaRecibida.clear()
        notifyDataSetChanged()
    }

    // Método para limpiar la selección
    fun limpiarSeleccion() {
        selectedItemPosition = null
        notifyDataSetChanged()
    }

    // Método para obtener la posición seleccionada
    fun getSelectedItem(): Int? = selectedItemPosition

    private fun identificarItem(texto: String): String {
        return when {
            texto.contains("->Enviado") -> "Enviado"
            texto.contains("->Reembolsado") -> "Reembolsado"
            else -> "Editable"
        }
    }
}