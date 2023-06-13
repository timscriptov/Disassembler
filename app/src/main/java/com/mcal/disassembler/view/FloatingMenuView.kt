package com.mcal.disassembler.view

import android.annotation.SuppressLint
import android.app.Activity
import android.content.ClipboardManager
import android.content.Context
import android.graphics.Color
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.View
import android.widget.RelativeLayout
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.mcal.disassembler.R
import com.mcal.disassembler.activities.BaseActivity
import com.mcal.disassembler.adapters.FloatingListAdapter
import com.mcal.disassembler.data.Preferences
import com.mcal.disassembler.databinding.FloatingMenuBinding
import com.mcal.disassembler.interfaces.SearchResultListener
import com.mcal.disassembler.nativeapi.Dumper

@SuppressLint("ViewConstructor")
class FloatingMenuView internal constructor(
    private val activity: Activity,
    menu: FloatingMenu,
    private val path: String,
) : RelativeLayout(
    activity
), SearchResultListener {
    private val binding by lazy { FloatingMenuBinding.inflate(activity.layoutInflater) }
    private val data by lazy(LazyThreadSafetyMode.NONE) {
        val list = mutableListOf<Map<String, Any>>()
        var map: MutableMap<String, Any>
        for (i in Dumper.symbols.indices) {
            map = HashMap()
            when (Dumper.symbols[i].type) {
                1 -> map["img"] = R.drawable.ic_box_blue
                2 -> map["img"] = R.drawable.ic_box_red
                else -> map["img"] = R.drawable.ic_box_green
            }
            map["title"] = Dumper.symbols[i].demangledName
            map["info"] = Dumper.symbols[i].name
            map["type"] = Dumper.symbols[i].type
            list.add(map)
        }
        list.sortBy {
            it["title"] as String
        }
        list
    }
    private var lastValue: String? = null

    init {
        val adapter = FloatingListAdapter(context, this, data)
        val recyclerView = binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            setAdapter(adapter)
        }

        val searchText = binding.search
        val clearBtn = binding.clearText
        clearBtn.setOnClickListener {
            searchText.setText("")
        }
        searchText.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(
                s: CharSequence,
                start: Int,
                before: Int,
                count: Int
            ) = Unit

            override fun beforeTextChanged(
                s: CharSequence,
                start: Int,
                count: Int,
                after: Int
            ) = Unit

            override fun afterTextChanged(s: Editable) {
                clearBtn.visibility = if (s.isEmpty()) View.GONE else View.VISIBLE
                if (adapter.canStartFilterProcess) {
                    if (!TextUtils.equals(s, lastValue)) {
                        val constraint = s.toString()
                        lastValue = constraint
                        recyclerView.smoothScrollToPosition(0)
                        adapter.canStartFilterProcess = false
                        adapter.filter(constraint)
                        return
                    }
                    return
                }
                adapter.newValue = s.toString()
            }
        })

        binding.floatingmenuButtonClose.apply {
            setOnClickListener {
                menu.dismiss()
                FloatingButton(activity, path).show()
            }
        }
        binding.floatingmenuButtonHide.apply {
            setOnClickListener {
                menu.dismiss()
            }
        }
        binding.floatingmenuButtonPaste.setOnClickListener {
            binding.search.setText(readFromClipboard())
        }
        binding.regex.setBackgroundColor(
            if (Preferences.isRegexEnabled()) ActivityCompat.getColor(
                context,
                R.color.colorAccent
            ) else Color.TRANSPARENT
        )
        binding.regex.setOnClickListener {
            if (Preferences.isRegexEnabled()) {
                Preferences.setRegexEnabled(false)
                binding.regex.setBackgroundColor(Color.TRANSPARENT)
            } else {
                Preferences.setRegexEnabled(true)
                binding.regex.setBackgroundColor(
                    ActivityCompat.getColor(
                        context,
                        R.color.colorAccent
                    )
                )
            }
        }
        addView(binding.root)
    }

    private fun readFromClipboard(): String {
        val clipboardManager =
            activity.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        return clipboardManager.primaryClip?.getItemAt(0)?.text.toString()
    }

    override fun onFoundApp(list: MutableList<Map<String, Any>>, mode: Boolean) {
        BaseActivity.setVisibility(
            binding.symbolsNotFound, if (mode) {
                View.GONE
            } else {
                View.VISIBLE
            }
        )
        BaseActivity.setVisibility(
            binding.recyclerView, if (mode) {
                View.VISIBLE
            } else {
                View.GONE
            }
        )
    }
}