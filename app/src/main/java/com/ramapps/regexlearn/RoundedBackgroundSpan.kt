package com.ramapps.regexlearn

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.text.style.ReplacementSpan

class RoundedBackgroundSpan(
    private var backgroundColor: Int,
    private var cornerRadius: Int = 12,
    private var paddingHorizontal: Int = 4,
    private var backgroundAlpha: Int = 255,
) : ReplacementSpan() {

    override fun getSize(paint: Paint, text: CharSequence?, start: Int, end: Int, fm: Paint.FontMetricsInt?): Int {
        // Should return the size of span.
        // The span size contain text with and paddings.
        return Math.round(paint.measureText(text, start, end) + paddingHorizontal * 2)
    }

    override fun draw(canvas: Canvas, text: CharSequence?, start: Int, end: Int, x: Float, top: Int, y: Int, bottom: Int, paint: Paint) {
        // Save original style and color to restore after drawing rounded background.
        val originalStyle = paint.style
        val originalColor = paint.color
        val originalAlpha = paint.alpha

        val textWidth = paint.measureText(text, start, end)

        val rectF = RectF(x, top.toFloat(), x + textWidth, bottom.toFloat())

        paint.style = Paint.Style.FILL
        paint.setColor(backgroundColor)
        paint.alpha = backgroundAlpha
        canvas.drawRoundRect(rectF, cornerRadius.toFloat(), cornerRadius.toFloat(), paint)

        paint.style = originalStyle
        paint.color = originalColor
        paint.alpha = originalAlpha

        if (text != null) {
            canvas.drawText(text, start, end, x, y.toFloat(), paint)
        }
    }

}