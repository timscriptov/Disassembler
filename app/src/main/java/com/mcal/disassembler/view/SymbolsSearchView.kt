package com.mcal.disassembler.view

import android.annotation.SuppressLint
import android.app.Activity
import android.widget.RelativeLayout
import com.mcal.disassembler.data.Preferences
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Locale
import java.util.regex.Matcher
import java.util.regex.Pattern

abstract class SymbolsSearchView(private val activity: Activity) : RelativeLayout(activity) {
    var data = mutableListOf<Map<String, Any>>()
    var symbolsFilteredList = mutableListOf<Map<String, Any>>()

    var lastValue: String? = null
    var newValue: String? = null
    var canStartFilterProcess = true

    fun filter(constraint: CharSequence?) = CoroutineScope(Dispatchers.IO).launch {
        val list = mutableListOf<Map<String, Any>>()
        val charSearch = constraint.toString().lowercase(Locale.ROOT)
        if (charSearch.isEmpty()) {
            list.addAll(data)
        } else {
            val isRegex = Preferences(activity).regex
            var name: String?
            val pattern = Pattern.compile(charSearch)
            var matcher: Matcher
            for (symbol in data) {
                name = (symbol["title"] as? String)?.lowercase(Locale.ROOT)
                if (name != null) {
                    if (isRegex) {
                        matcher = pattern.matcher(name)
                        if (matcher.find()) {
                            list.add(symbol)
                        }
                    } else {
                        if (name.contains(charSearch)) {
                            list.add(symbol)
                        }
                    }
                }
            }
        }
        symbolsFilteredList.apply {
            clear()
            addAll(list)
        }
        CoroutineScope(Dispatchers.Main).launch {
            publishResults(list)
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun publishResults(results: MutableList<Map<String, Any>>) {
        val isNotEmpty = results.isNotEmpty()
        if (isNotEmpty) {
            symbolsFilteredList = results.also { list ->
                list.sortBy { it["title"] as String }
            }
        }
        onFoundApp(symbolsFilteredList, isNotEmpty)
        val text = newValue
        if (text.isNullOrEmpty()) {
            canStartFilterProcess = true
            return
        }
        newValue = null
        filter(text)
    }

    abstract fun onFoundApp(list: MutableList<Map<String, Any>>, mode: Boolean)
}