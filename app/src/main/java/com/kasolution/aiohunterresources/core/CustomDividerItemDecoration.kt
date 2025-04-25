package com.kasolution.aiohunterresources.core

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import androidx.recyclerview.widget.RecyclerView

class CustomDividerItemDecoration(
    private val context: Context,
    private val leftMargin: Int,
    private val rightMargin: Int,
    private val dividerHeight: Int = 1,  // Grosor del divisor
    private val dividerColor: Int = Color.GRAY // Color del divisor
) : RecyclerView.ItemDecoration() {

    private val paint = Paint()

    init {
        paint.color = dividerColor
        paint.strokeWidth = dividerHeight.toFloat()
        paint.style = Paint.Style.FILL
    }

    override fun onDrawOver(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        val left = parent.paddingLeft + leftMargin
        val right = parent.width - parent.paddingRight - rightMargin

        for (i in 0 until parent.childCount - 1) {
            val child = parent.getChildAt(i)
            val params = child.layoutParams as RecyclerView.LayoutParams
            val top = child.bottom + params.bottomMargin
            val bottom = top + dividerHeight

            c.drawRect(left.toFloat(), top.toFloat(), right.toFloat(), bottom.toFloat(), paint)
        }
    }
}
