package com.mcal.disassembler.view

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.graphics.PixelFormat
import android.os.Build
import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener
import android.view.WindowManager
import com.mcal.disassembler.App

class FloatingMenu(
    private val activity: Activity,
    path: String
) {
    private var isAdded = false
    private val windowManager =
        activity.applicationContext.getSystemService(Context.WINDOW_SERVICE) as WindowManager
    private val params = WindowManager.LayoutParams()
    private val floatView = FloatingMenuView(activity, this, path)

    fun show() {
        windowManager.addView(floatView.apply {
            isClickable = true
            setOnTouchListener(object : OnTouchListener {
                var lastX = 0
                var lastY = 0
                var paramX = 0
                var paramY = 0

                @SuppressLint("ClickableViewAccessibility")
                override fun onTouch(v: View, event: MotionEvent): Boolean {
                    val param = params
                    when (event.action) {
                        MotionEvent.ACTION_DOWN -> {
                            lastX = event.rawX.toInt()
                            lastY = event.rawY.toInt()
                            paramX = param.x
                            paramY = param.y
                        }

                        MotionEvent.ACTION_MOVE -> {
                            val dx = event.rawX.toInt() - lastX
                            val dy = event.rawY.toInt() - lastY
                            param.x = paramX + dx
                            param.y = paramY + dy
                            windowManager.updateViewLayout(this@apply, param)
                        }
                    }
                    xPos = param.x
                    yPos = param.y
                    return false
                }
            })
        }, params.apply {
            type = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
            } else {
                WindowManager.LayoutParams.TYPE_PHONE
            }
            format = PixelFormat.TRANSPARENT
            flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
            width = App.dp(activity, 250)
            height = windowManager.defaultDisplay.height / 2
            x = xPos
            y = yPos
        })
        isAdded = true
    }

    fun dismiss() {
        windowManager.removeView(floatView)
    }

    companion object {
        private var xPos = 0
        private var yPos = 0
    }
}
