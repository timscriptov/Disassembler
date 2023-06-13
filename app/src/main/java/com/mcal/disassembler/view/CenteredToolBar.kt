package com.mcal.disassembler.view

import android.content.Context
import android.graphics.Typeface
import android.text.TextUtils
import android.util.AttributeSet
import android.view.Gravity
import android.widget.TextView
import androidx.annotation.StringRes
import androidx.core.widget.TextViewCompat
import com.google.android.material.appbar.MaterialToolbar
import com.mcal.disassembler.R

class CenteredToolBar : MaterialToolbar {
    private var centeredTitleTextView: TextView? = null

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    override fun setTitleTextColor(color: Int) {
        getCenteredTitleTextView().setTextColor(color)
    }

    override fun getTitle(): CharSequence {
        return getCenteredTitleTextView().text.toString()
    }

    override fun setTitle(@StringRes resId: Int) {
        title = resources.getString(resId)
    }

    override fun setTitle(title: CharSequence) {
        getCenteredTitleTextView().text = title
    }

    fun setTypeface(font: Typeface?) {
        getCenteredTitleTextView().typeface = font
    }

    private fun getCenteredTitleTextView(): TextView {
        var textView = centeredTitleTextView
        if (textView == null) {
            textView = TextView(context).apply {
                setSingleLine()
                ellipsize = TextUtils.TruncateAt.END
                gravity = Gravity.CENTER
                layoutParams =
                    LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT).apply {
                        gravity = Gravity.CENTER
                    }
            }.also {
                TextViewCompat.setTextAppearance(
                    it,
                    R.style.TextAppearance_AppCompat_Widget_ActionBar_Title
                )
                addView(it)
            }
        }
        return textView
    }
}
