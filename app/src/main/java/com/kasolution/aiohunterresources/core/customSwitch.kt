package com.kasolution.aiohunterresources.core

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import com.kasolution.aiohunterresources.R
import com.kasolution.aiohunterresources.databinding.CustomSwitchBinding

class customSwitch @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
): ConstraintLayout(context, attrs, defStyleAttr) {
    private var binding: CustomSwitchBinding
    private var isItemSelected = true

    // Atributos personalizados
    private var textOption1: String?
    private var textOption2: String?
    private var backgroundDrawable: Drawable?  // Fondo del switch
    private var indicatorDrawable: Drawable?   // Fondo del selector
    private var textColor: Int

    // Interfaz para escuchar cambios
    interface OnSwitchChangedListener {
        fun onSwitchChanged(isItemSelected: Boolean)
    }
    private var switchChangedListener: OnSwitchChangedListener? = null

    init {
        // Inflar el layout usando ViewBinding
        binding = CustomSwitchBinding.inflate(LayoutInflater.from(context), this, true)

        // Obtener los atributos personalizados del XML
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.CustomSwitch)

        // Asignar valores a las variables
        textOption1 = typedArray.getString(R.styleable.CustomSwitch_textOption1)
        textOption2 = typedArray.getString(R.styleable.CustomSwitch_textOption2)
        backgroundDrawable = typedArray.getDrawable(R.styleable.CustomSwitch_backgroundDrawable) // Fondo del switch
        indicatorDrawable = typedArray.getDrawable(R.styleable.CustomSwitch_indicatorDrawable) // Fondo del selector
        textColor = typedArray.getColor(R.styleable.CustomSwitch_textColor, Color.BLACK)

        // Establecer los valores a las vistas
        binding.tvTexto1.text = textOption1 ?: "Option 1"
        binding.tvTexto2.text = textOption2 ?: "Option 2"
        binding.tvTexto1.setTextColor(textColor)
        binding.tvTexto2.setTextColor(textColor)

        // Aplicar el fondo del CustomSwitch con un drawable
        binding.customSwitch.background= backgroundDrawable ?: context.getDrawable(R.drawable.selector_background) // Usamos un drawable predeterminado si no se pasa

        // Aplicar el fondo del selector con un drawable
        binding.selector.background = indicatorDrawable ?: context.getDrawable(R.drawable.selector_switch_background) // Usamos un drawable predeterminado si no se pasa
//        binding.selector.elevation = 5f
        // Liberar los recursos del TypedArray
        typedArray.recycle()




        // Inicializar las vistas usando el binding
        binding.tvTexto1.setOnClickListener {
            isItemSelected = true
            updateSelectorPosition()
            switchChangedListener?.onSwitchChanged(isItemSelected)
        }

        binding.tvTexto2.setOnClickListener {
            isItemSelected = false
            updateSelectorPosition()
            switchChangedListener?.onSwitchChanged(isItemSelected)
        }
    }

    // Método para actualizar la posición del selector
    private fun updateSelectorPosition() {
        val layoutParams = binding.selector.layoutParams as ConstraintLayout.LayoutParams
        layoutParams.width =
            if (isItemSelected) binding.tvTexto1.width else binding.tvTexto2.width
        layoutParams.startToStart = if (isItemSelected) R.id.tvTexto1 else R.id.tvTexto2
        binding.selector.layoutParams = layoutParams
        binding.selector.animate()
            .x(if (isItemSelected) 0f else binding.tvTexto1.width.toFloat())
            .setDuration(200)
            .start()
    }

    // Método para configurar el listener
    fun setOnSwitchChangedListener(listener: OnSwitchChangedListener) {
        switchChangedListener = listener
    }
}