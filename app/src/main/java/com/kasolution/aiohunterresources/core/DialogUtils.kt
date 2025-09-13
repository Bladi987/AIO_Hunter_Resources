package com.kasolution.aiohunterresources.core

import android.app.AlertDialog
import android.content.ClipboardManager
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.core.view.isVisible
import com.airbnb.lottie.LottieAnimationView
import com.kasolution.aiohunterresources.R
import com.kasolution.aiohunterresources.databinding.DialogCustomMessageBinding
import com.kasolution.aiohunterresources.databinding.DialogCustomMessageResposeBinding
import com.kasolution.aiohunterresources.databinding.DialogCustomMessageResposeErrorBinding
import com.kasolution.aiohunterresources.databinding.DialogInputDataBinding
import com.kasolution.aiohunterresources.databinding.DialogMessageQuestionBinding

object DialogUtils {
    fun dialogMessage(
        context: Context,
        imagen: Int,
        message: String,
        countOption: Int,
        onPositiveClick: (() -> Unit)? = null,
        onNegativeClick: (() -> Unit)? = null
    ) {

        val inflater = LayoutInflater.from(context)
        val binding = DialogCustomMessageBinding.inflate(inflater)
        val lottieAnimationView: LottieAnimationView = binding.ivimagen

        if (countOption == 1) {
            binding.flcontnedorExit.visibility = View.GONE
            lottieAnimationView.setAnimation(R.raw.no_connection)
        } else lottieAnimationView.setAnimation(R.raw.new_version)

        lottieAnimationView.playAnimation()
        binding.tvTexto.text = message

        val builder = AlertDialog.Builder(context)
        builder.setView(binding.root)
        val dialog = builder.create()
        dialog.setCancelable(false)
        // Configurar la ventana del diálogo como transparente
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        //Configuramos la animacion del dialogo
        val anim1 = AnimationUtils.loadAnimation(context, R.anim.bounce)
        // Configuración del botón positivo
        binding.root.animation = anim1

        binding.btnAceptar.text = context.getString(android.R.string.ok)
        binding.btnAceptar.setOnClickListener {
            onPositiveClick?.invoke()
            dialog.dismiss()
        }
        binding.ivExit.setOnClickListener() {
            onNegativeClick?.invoke()
            dialog.dismiss()
        }

        // Configuración del botón negativo
        binding.ivExit.isVisible = true // Mostrar el botón negativo
//        binding.ivExit.text = context.getString(android.R.string.cancel) // Cambiar el texto según sea necesario
        binding.ivExit.setOnClickListener {
            onNegativeClick?.invoke()
            dialog.dismiss()
        }

        dialog.show()
    }

    fun dialogMessageResponse(
        context: Context,
        message: String,
        onPositiveClick: (() -> Unit)? = null
    ) {
        val inflater = LayoutInflater.from(context)
        val binding = DialogCustomMessageResposeBinding.inflate(inflater)

        binding.tvTexto.text = message

        val builder = AlertDialog.Builder(context)
        builder.setView(binding.root)
        val dialog = builder.create()
        dialog.setCancelable(true)
        // Configurar la ventana del diálogo como transparente
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        //Configuramos la animacion del dialogo
        val anim1 = AnimationUtils.loadAnimation(context, R.anim.bounce3)
        // Configuración del botón positivo
        binding.root.animation = anim1

        binding.btnAceptar.text = context.getString(android.R.string.ok)
        binding.btnAceptar.setOnClickListener {
            onPositiveClick?.invoke()
            dialog.dismiss()
        }
        dialog.show()
    }

    fun dialogMessageResponseError(
        context: Context,
        icon: Int,
        message: String,
        codigo: String,
        onPositiveClick: (() -> Unit)? = null
    ) {
        val inflater = LayoutInflater.from(context)
        val binding = DialogCustomMessageResposeErrorBinding.inflate(inflater)

        binding.tvTexto.text = message
        binding.ivIcon.setImageResource(icon)
        binding.tvCodError.text = codigo
        val builder = AlertDialog.Builder(context)
        builder.setView(binding.root)
        val dialog = builder.create()
        dialog.setCancelable(true)
        // Configurar la ventana del diálogo como transparente
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        //Configuramos la animacion del dialogo
        val anim1 = AnimationUtils.loadAnimation(context, R.anim.bounce)
        // Configuración del botón positivo
        binding.root.animation = anim1

        binding.btnAceptar.text = context.getString(android.R.string.ok)
        binding.btnAceptar.setOnClickListener {
            onPositiveClick?.invoke()
            dialog.dismiss()
        }
        dialog.show()
    }

    fun dialogQuestion(
        context: Context,
        titulo: String,
        mensage: String,
        positiveButtontext: String? = null,
        negativeButtontext: String? = null,
        onPositiveClick: (() -> Unit)? = null,
        onNegativeClick: (() -> Unit)? = null
    ) {
        val inflater = LayoutInflater.from(context)
        val binding = DialogMessageQuestionBinding.inflate(inflater)
        binding.lbldialogTittle.text = titulo
        binding.lblDialogMessage.text = mensage
        if (positiveButtontext != null)
            binding.btndialogacept.text = positiveButtontext
        if (negativeButtontext != null)
            binding.btndialogCancel.text = negativeButtontext

        val builder = AlertDialog.Builder(context)
        builder.setView(binding.root)
        val dialog = builder.create()
        dialog.setCancelable(false)
        // Configurar la ventana del diálogo como transparente
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        // Configuración del botón positivo
        dialog.show()
        aparecer(context, binding.root)
        binding.btndialogacept.setOnClickListener() {
            onPositiveClick?.invoke()
            dialog.dismiss()
        }
        binding.btndialogCancel.setOnClickListener() {
            onNegativeClick?.invoke()
            dialog.dismiss()
        }
    }

    fun dialogInput(
        context: Context,
        title: String,
        onInput: (String) -> Unit
    ) {
        // Inflar el diseño del diálogo
        val inflater = LayoutInflater.from(context)
        val binding = DialogInputDataBinding.inflate(inflater)
        //val editText: EditText = binding.tvInputData // Cambia según el ID que uses en tu layout

        // Configurar el diálogo
        val builder = AlertDialog.Builder(context)
        builder.setView(binding.root)
        // Mostrar el diálogo
        val dialog = builder.create()
        dialog.setCancelable(true)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.show()
        binding.lbldialogTittle.text = title
        // Configurar el botón positivo
        binding.btndialogacept.setOnClickListener {
            val inputText = binding.tvInputData.text.toString()
            onInput(inputText) // Llama a la función con el dato ingresado
            dialog.dismiss()
        }
        binding.imgPaste.setOnClickListener {
            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clip = clipboard.primaryClip

            if (clip != null && clip.itemCount > 0) {
                val item = clip.getItemAt(0)
                binding.tvInputData.setText(item.text) // Establecer el texto del portapapeles en el EditText
            } else {
                Toast.makeText(context, "No hay texto en el portapapeles", Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }


    //funciones generales de uso dentro de la clase
    fun aparecer(context: Context, view: View) {
        val anim1 = AnimationUtils.loadAnimation(context, R.anim.bounce)
        view.animation = anim1
    }

}