/**
 * Copyright © 2020 Safened - Fourthline B.V. All rights reserved.
 */
package de.solarisbank.sdk.fourthline.feature.ui.custom

import android.content.Context
import android.graphics.*
import android.graphics.Paint.ANTI_ALIAS_FLAG
import android.util.AttributeSet
import android.view.View

class PunchholeView @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    var punchholeRect = Rect(0, 0, 0, 0)

    private val transparentPaint = run {
        val paint = Paint(ANTI_ALIAS_FLAG)
        paint.style = Paint.Style.FILL
        paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.CLEAR)
        paint
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.drawRect(punchholeRect, transparentPaint)
    }
}