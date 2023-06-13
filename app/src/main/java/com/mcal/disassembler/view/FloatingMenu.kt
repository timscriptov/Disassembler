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
import com.mcal.disassembler.App.Companion.dp

class FloatingMenu(private val activity: Activity, private val path: String) {
    var isAdded = false
    var wm: WindowManager? = null
    var params: WindowManager.LayoutParams? = null
    var floatView: FloatingMenuView? = null

    @SuppressLint("ClickableViewAccessibility")
    fun show() {
        val LAYOUT_FLAG: Int
        LAYOUT_FLAG = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
        } else {
            WindowManager.LayoutParams.TYPE_PHONE
        }
        wm = activity.applicationContext.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        params = WindowManager.LayoutParams()
        params!!.type = LAYOUT_FLAG // WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
        params!!.format = PixelFormat.RGBA_8888
        params!!.flags =
            WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL or WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
        params!!.height = wm!!.defaultDisplay.height / 2
        params!!.width = dp(activity, 250)
        params!!.x = xPos
        params!!.y = yPos
        floatView = FloatingMenuView(activity, this, path)
        floatView!!.isClickable = true
        floatView!!.setOnTouchListener(object : OnTouchListener {
            var lastX = 0
            var lastY = 0
            var paramX = 0
            var paramY = 0
            override fun onTouch(v: View, event: MotionEvent): Boolean {
                when (event.action) {
                    MotionEvent.ACTION_DOWN -> {
                        lastX = event.rawX.toInt()
                        lastY = event.rawY.toInt()
                        paramX = params!!.x
                        paramY = params!!.y
                    }

                    MotionEvent.ACTION_MOVE -> {
                        val dx = event.rawX.toInt() - lastX
                        val dy = event.rawY.toInt() - lastY
                        params!!.x = paramX + dx
                        params!!.y = paramY + dy
                        wm!!.updateViewLayout(floatView, params)
                    }
                }
                xPos = params!!.x
                yPos = params!!.y
                return false
            }
        })
        wm!!.addView(floatView, params)
        isAdded = true
    }

    fun dismiss() {
        wm!!.removeView(floatView)
    }

    companion object {
        var xPos = 0
        var yPos = 0
    }
}