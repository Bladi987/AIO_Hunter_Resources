package com.kasolution.aiohunterresources.core

import android.content.Context
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.RectF
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import androidx.core.content.ContextCompat
import com.kasolution.recursoshunter.UI.view.Home.Interfaces.MyButtonClickListener


class MyButton(
    private val context: Context, // Contexto de la aplicación
    private val text: String, // Texto que se mostrará en el botón
    private val textSize: Int, // Tamaño del texto
    private val imageResId: Int, // Recurso de imagen para el botón (si se proporciona)
    private val color: Int, // Color de fondo del botón
    private val listener: MyButtonClickListener // Listener para manejar los clics en el botón
) {
    private var pos: Int = 0 // Posición del botón en la lista
    private var clickRegion: RectF? = null // Región de clic del botón (área sensible para clics)

    fun onClick(x: Float, y: Float): Boolean { // Método para manejar clics en el botón
        if (clickRegion != null && clickRegion!!.contains(x, y)) { // Comprobar si el clic está dentro del área del botón
            listener.onClick(pos) // Notificar al listener que se hizo clic en el botón
            return true // Indicar que se manejó el clic
        }
        return false // Indicar que no se manejó el clic
    }

    fun onDraw(c: Canvas, rectF: RectF, pos: Int) { // Método para dibujar el botón en el lienzo
        val p = Paint() // Objeto Paint para dibujar
        p.color = color // Establecer el color de fondo del botón
        c.drawRect(rectF, p) // Dibujar el fondo del botón

        // Texto
        p.color = Color.WHITE // Color del texto
        p.textSize = textSize.toFloat() // Tamaño del texto

        val r = Rect() // Rectángulo para medir el texto
        val cHeight = rectF.height() // Altura del botón
        val cWidth = rectF.width() // Ancho del botón
        p.textAlign = Paint.Align.LEFT // Alineación del texto
        p.getTextBounds(text, 0, text.length, r) // Medir el texto
        var x = 0f // Posición X del texto
        var y = 0f // Posición Y del texto
        if (imageResId == 0) { // Si no hay imagen en el botón
            x = cWidth / 2f - r.width() / 2f - r.left.toFloat() // Calcular la posición X del texto
            y = cHeight / 2f + r.height() / 2f - r.bottom.toFloat() // Calcular la posición Y del texto
            c.drawText(text, rectF.left + x, rectF.top + y, p) // Dibujar el texto en el lienzo
        } else { // Si hay una imagen en el botón
            val d = ContextCompat.getDrawable(context, imageResId) // Obtener la imagen del recurso
            val bitmap = drawableToBitmap(d) // Convertir la imagen a Bitmap
            // Dibujar la imagen en el centro del botón
            c.drawBitmap(bitmap, (rectF.left + rectF.right) / 2, (rectF.top + rectF.bottom) / 2, p)
        }
        clickRegion = rectF // Establecer la región de clic del botón
        this.pos = pos // Establecer la posición del botón en la lista
    }

    private fun drawableToBitmap(d: Drawable?): Bitmap { // Método para convertir un Drawable a Bitmap
        if (d is BitmapDrawable) return d.bitmap // Si el Drawable es un BitmapDrawable, devolver el Bitmap
        val bitmap = Bitmap.createBitmap(d!!.intrinsicWidth, d.intrinsicHeight, Bitmap.Config.ARGB_8888) // Crear un nuevo Bitmap
        val canvas = Canvas(bitmap) // Crear un lienzo para el Bitmap
        d.setBounds(0, 0, canvas.width, canvas.height) // Establecer los límites del Drawable
        d.draw(canvas) // Dibujar el Drawable en el lienzo
        return bitmap // Devolver el Bitmap resultante
    }
}
