package com.mcal.disassembler.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.util.AttributeSet
import android.widget.RelativeLayout

class CustomView : RelativeLayout {
    val disabledBackgroundColor = Color.parseColor("#E2E2E2")

    // Indicate if user touched this view the last time
    var isLastTouch = false
    var beforeBackground = 0
    var animation = false

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?) : super(context)

    override fun setEnabled(enabled: Boolean) {
        super.setEnabled(enabled)
        if (enabled) {
            setBackgroundColor(beforeBackground)
        } else {
            setBackgroundColor(
                disabledBackgroundColor
            )
        }
        invalidate()
    }

    override fun onAnimationStart() {
        super.onAnimationStart()
        animation = true
    }

    override fun onAnimationEnd() {
        super.onAnimationEnd()
        animation = false
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (animation) {
            invalidate()
        }
    }
}