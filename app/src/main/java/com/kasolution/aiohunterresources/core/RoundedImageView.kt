package com.kasolution.aiohunterresources.core

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.graphics.drawable.toBitmap
import com.kasolution.aiohunterresources.R


class RoundedImageView(context: Context, attrs: AttributeSet) : AppCompatImageView(context, attrs) {

    private val paint = Paint()

    private var topLeftRadius = 0f
    private var topRightRadius = 0f
    private var bottomRightRadius = 0f
    private var bottomLeftRadius = 0f

    init {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.RoundedImageView)
        topLeftRadius = typedArray.getDimension(R.styleable.RoundedImageView_topLeftRadius, 0f)
        topRightRadius = typedArray.getDimension(R.styleable.RoundedImageView_topRightRadius, 0f)
        bottomRightRadius = typedArray.getDimension(R.styleable.RoundedImageView_bottomRightRadius, 0f)
        bottomLeftRadius = typedArray.getDimension(R.styleable.RoundedImageView_bottomLeftRadius, 0f)
        typedArray.recycle()
    }

    override fun onDraw(canvas: Canvas) {
        val bitmap = drawable?.toBitmap()
        bitmap?.let {
            val shader = BitmapShader(bitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP)
            paint.shader = shader

            val rect = RectF(0f, 0f, width.toFloat(), height.toFloat())
            val path = Path()

            // Agregar esquinas redondeadas al path con diferentes radios
            path.addRoundRect(rect,
                floatArrayOf(
                    topLeftRadius, topLeftRadius,
                    topRightRadius, topRightRadius,
                    bottomRightRadius, bottomRightRadius,
                    bottomLeftRadius, bottomLeftRadius
                ),
                Path.Direction.CW
            )

            canvas?.drawPath(path, paint)
        } ?: run {
            super.onDraw(canvas)
        }
    }
}
