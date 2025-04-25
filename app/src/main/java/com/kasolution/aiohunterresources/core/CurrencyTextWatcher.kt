package com.kasolution.aiohunterresources.core

import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText

class CurrencyTextWatcher(private val editText: EditText) : TextWatcher {
    private var isEditing = false
    fun applyInitialFormatting(value: String?) {
        if (value.isNullOrEmpty()) return

        val cleanString = value.replace(Regex("[^\\d]"), "") // Elimina caracteres no numéricos
        val parsed = cleanString.toDoubleOrNull()?.div(100) ?: 0.0  // Divide entre 100 para formato decimal
        val formatted = String.format("S/ %.2f", parsed)            // Formatea como "S/ 0.00"

        editText.setText(formatted)
        editText.setSelection(formatted.length)  // Mantiene el cursor al final
    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

    override fun afterTextChanged(s: Editable?) {
        if (isEditing || s.isNullOrEmpty()) return

        isEditing = true

        val cleanString = s.toString().replace(Regex("[^\\d]"), "")
        val parsed = cleanString.toDoubleOrNull()?.div(100) ?: 0.0  // Divide entre 100 para formato decimal
        val formatted = String.format("S/ %.2f", parsed)            // Formatea como "S/ 0.00"

        editText.setText(formatted)
        editText.setSelection(formatted.length)  // Mantiene el cursor al final

        isEditing = false
    }
}