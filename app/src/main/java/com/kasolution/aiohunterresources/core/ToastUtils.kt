package com.kasolution.aiohunterresources.core

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.view.Gravity

import android.view.LayoutInflater
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.DrawableRes
import androidx.cardview.widget.CardView
import com.kasolution.aiohunterresources.R

object ToastUtils {
    fun showCustomToast(context: Context, message: String, @DrawableRes iconResId: Int) {
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val layout = inflater.inflate(R.layout.toast_custom, null)

        val textMessage = layout.findViewById<TextView>(R.id.text_message)
        textMessage.text = message

        val imageIcon = layout.findViewById<ImageView>(R.id.image_icon)
        imageIcon.setImageResource(iconResId)

        val toast = Toast(context)
        toast.duration = Toast.LENGTH_SHORT
        toast.view = layout

        // Configurar animación de entrada
        val slideInAnimation = AnimationUtils.loadAnimation(context, R.anim.zoom)
        toast.view?.startAnimation(slideInAnimation)
        // Configurar la gravedad para que el Toast esté centrado
        toast.setGravity(Gravity.CENTER, 0, 0)

        toast.show()

//        // Ocultar el Toast con animación después de la duración del Toast
//        Handler(Looper.getMainLooper()).postDelayed({
//            val slideOutAnimation = AnimationUtils.loadAnimation(context, R.anim.zoom_out)
//            toast.view?.startAnimation(slideOutAnimation)
//            toast.cancel()
//        }, toast.duration.toLong())
    }
    fun MensajeToast(context: Context, mensaje: String?, tipo: Int) {
        var anim1= AnimationUtils.loadAnimation(context, R.anim.bounce3)
        val layout = LayoutInflater.from(context).inflate(R.layout.dialog_toast, null)
        val msg=layout.findViewById<TextView>(R.id.cp_title)
        val img=layout.findViewById<ImageView>(R.id.cp_imagen)
        val cardView=layout.findViewById<CardView>(R.id.cp_cardview)
        if(tipo==0){
            img.setImageResource(R.drawable.done)
        }else{
            img.setImageResource(R.drawable.error)
        }

        msg.text=mensaje
        val myToast=Toast(context)
        myToast.duration = Toast.LENGTH_SHORT
        myToast.setGravity(Gravity.CENTER_VERTICAL, 0, 0)
        myToast.view = layout//setting the view of custom toast layout
        myToast.show()
        cardView.animation=anim1
    }

}