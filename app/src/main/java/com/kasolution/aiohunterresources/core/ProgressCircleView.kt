package com.kasolution.aiohunterresources.core


import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View

class ProgressCircleView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private var progress = 0f // El progreso actual, entre 0 y 100
    private var strokeWidth = 20f // El grosor del círculo
    private var circleColor = Color.parseColor("#FF4081") // Color del círculo
    private var backgroundColor = Color.parseColor("#E0E0E0") // Color del fondo del círculo

    private val paint = Paint()
    private val rectF = RectF()

    init {
        paint.isAntiAlias = true
        paint.style = Paint.Style.STROKE
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        // Calculamos el centro y el radio del círculo
        val centerX = width / 2f
        val centerY = height / 2f
        val radius = (Math.min(width, height) / 2f) - strokeWidth

        // Dibuja el círculo de fondo
        paint.color = backgroundColor
        paint.strokeWidth = strokeWidth
        canvas.drawCircle(centerX, centerY, radius, paint)

        // Dibuja el progreso
        paint.color = circleColor
        val sweepAngle = 360 * progress / 100 // Calculamos el ángulo de progreso
        canvas.drawArc(rectF, -90f, sweepAngle, false, paint) // Dibuja el arco del progreso
    }

    override fun onSizeChanged(width: Int, height: Int, oldWidth: Int, oldHeight: Int) {
        super.onSizeChanged(width, height, oldWidth, oldHeight)
        // Ajustamos el rectángulo que se usará para dibujar el arco
        rectF.set(strokeWidth, strokeWidth, width - strokeWidth, height - strokeWidth)
    }

    // Método para actualizar el progreso, con animación
    fun setProgress(progress: Float) {
        if (progress in 0f..100f) {
            this.progress = progress
            invalidate() // Redibuja la vista
        }
    }

    // Método para animar el progreso
    fun animateProgress(finalProgress: Float) {
        val animator = ValueAnimator.ofFloat(progress, finalProgress)
        animator.duration = 2000 // Duración de la animación (en milisegundos)
        animator.addUpdateListener { animation ->
            val value = animation.animatedValue as Float
            setProgress(value) // Actualizamos el progreso con el valor animado
        }
        animator.start() // Iniciamos la animación
    }
}
