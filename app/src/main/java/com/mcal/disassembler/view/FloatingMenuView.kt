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
import androidx.core.app.ActivityCompat
import com.mcal.disassembler.R
import com.mcal.disassembler.activities.BaseActivity
import com.mcal.disassembler.adapters.SymbolsItem
import com.mcal.disassembler.data.Preferences
import com.mcal.disassembler.databinding.FloatingMenuBinding
import com.mcal.disassembler.interfaces.SearchResultListener
import com.mcal.disassembler.nativeapi.Dumper
import com.mikepenz.fastadapter.FastAdapter
import com.mikepenz.fastadapter.adapters.ItemAdapter

@SuppressLint("ViewConstructor")
class FloatingMenuView internal constructor(
    private val activity: Activity,
    menu: FloatingMenu,
    private val path: String,
) : SymbolsSearchView(
    activity
), SearchResultListener {
    private val binding by lazy { FloatingMenuBinding.inflate(activity.layoutInflater) }
    private val itemAdapter = ItemAdapter<SymbolsItem>()
    private val adapter = FastAdapter.with(itemAdapter)

    init {
        val recyclerView = binding.recyclerView
        recyclerView.adapter = adapter

        if (data.isEmpty()) {
            initData()
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
                if (canStartFilterProcess) {
                    if (!TextUtils.equals(s, lastValue)) {
                        val constraint = s.toString()
                        lastValue = constraint
                        recyclerView.smoothScrollToPosition(0)
                        BaseActivity.setVisibility(binding.progress, VISIBLE)
                        canStartFilterProcess = false
                        filter(constraint)
                        return
                    }
                    return
                }
                newValue = s.toString()
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
        val preferences = Preferences(context)
        binding.regex.setBackgroundColor(
            if (preferences.regex) ActivityCompat.getColor(
                context,
                R.color.colorAccent
            ) else Color.TRANSPARENT
        )
        binding.regex.setOnClickListener {
            if (preferences.regex) {
                preferences.regex = false
                binding.regex.setBackgroundColor(Color.TRANSPARENT)
            } else {
                preferences.regex = true
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

    private fun updateAdapter(list: MutableList<Map<String, Any>>) {
        var item: Map<String, Any>
        for (i in list.indices) {
            item = list[i]
            itemAdapter.add(
                SymbolsItem()
                    .withContext(activity)
                    .withId(i.toLong())
                    .withIcon(item["img"] as Int)
                    .withTitle(item["title"] as String)
                    .withSubTitle(item["info"] as String)
                    .withSymbolType(item["type"] as Int)
                    .withPath(path)
            )
        }
        BaseActivity.setVisibility(binding.progress, GONE)
    }

    private fun initData() {
        BaseActivity.setVisibility(binding.progress, VISIBLE)
        val list = symbolsFilteredList
        if (list.isNotEmpty()) {
            list.clear()
        }
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
        updateSymbolsSize(list)
        updateAdapter(list)
        val dataList = data
        if (dataList.isNotEmpty()) {
            dataList.clear()
        }
        dataList.addAll(list)
    }

    private fun readFromClipboard(): String {
        val clipboardManager =
            activity.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        return clipboardManager.primaryClip?.getItemAt(0)?.text.toString()
    }

    private fun updateSymbolsSize(list: MutableList<Map<String, Any>>) {
        val symbolsSizeView = binding.symbolsSize
        val dataSize = list.size.toString()
        if (symbolsSizeView.text.toString() != dataSize) {
            symbolsSizeView.text = buildString {
                append(activity.getString(R.string.symbols_count))
                append(dataSize)
            }
        }
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
        if (itemAdapter.adapterItemCount >= 0) {
            itemAdapter.clear()
        }
        updateAdapter(list)
    }
}