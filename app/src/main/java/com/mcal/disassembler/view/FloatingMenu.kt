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
    var isAdded = false
    var wm = activity.applicationContext.getSystemService(Context.WINDOW_SERVICE) as WindowManager
    var params = WindowManager.LayoutParams()
    var floatView = FloatingMenuView(activity, this, path)

    @SuppressLint("ClickableViewAccessibility")
    fun show() {
        wm.addView(floatView.apply {
            isClickable = true
            setOnTouchListener(object : OnTouchListener {
                var lastX = 0
                var lastY = 0
                var paramX = 0
                var paramY = 0
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
                            this@apply.updateViewLayout(this@apply, param)
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
            format = PixelFormat.RGBA_8888
            flags =
                WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL or WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
            height = wm.defaultDisplay.height / 2
            width = App.dp(activity, 250)
            x = xPos
            y = yPos
        })
        isAdded = true
    }

    fun dismiss() {
        wm.removeView(floatView)
    }

    companion object {
        var xPos = 0
        var yPos = 0
    }
}
