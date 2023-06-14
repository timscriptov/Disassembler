package com.mcal.disassembler.view

import android.annotation.SuppressLint
import android.app.Activity
import android.widget.RelativeLayout
import com.mcal.disassembler.data.Preferences
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Locale
import java.util.regex.Matcher
import java.util.regex.Pattern

abstract class SymbolsSearchView(
    private val activity: Activity
) : RelativeLayout(activity) {
    val data = arrayListOf<Map<String, Any>>()
    val symbolsFilteredList = arrayListOf<Map<String, Any>>()

    var lastValue: String? = null
    var newValue: String? = null
    var canStartFilterProcess = true

    fun filter(constraint: CharSequence) = CoroutineScope(Dispatchers.IO).launch {
        withContext(Dispatchers.Main) {
            onStartSearch()
        }
        val list: ArrayList<Map<String, Any>>
        val charSearch = constraint.toString().lowercase(Locale.ROOT)
        if (charSearch.isEmpty()) {
            list = arrayListOf()
            list.addAll(data)
            list.sortBy { it["title"] as String }
        } else {
            val listStart = arrayListOf<Map<String, Any>>()
            val listEnd = arrayListOf<Map<String, Any>>()
            val isRegex = Preferences(activity).regex
            var name: String
            val pattern = Pattern.compile(charSearch)
            var matcher: Matcher
            for (symbol in data) {
                name = (symbol["title"] as String).lowercase(Locale.ROOT)
                if (isRegex) {
                    matcher = pattern.matcher(name)
                    if (matcher.find()) {
                        listStart.add(symbol)
                    }
                } else {
                    if (name.startsWith(charSearch)) {
                        listStart.add(symbol)
                    } else if (name.contains(charSearch)) {
                        listEnd.add(symbol)
                    }
                }
            }
            val offset1 = listStart.size
            list = ArrayList(offset1 + listEnd.size)
            for (app in listStart) {
                list.add(0, app)
            }
            for (app in listEnd) {
                list.add(offset1, app)
            }
        }
        publishResults(list)
    }

    @SuppressLint("NotifyDataSetChanged")
    private suspend fun publishResults(results: ArrayList<Map<String, Any>>) {
        val isNotEmpty = results.isNotEmpty()
        val filterList = symbolsFilteredList
        if (isNotEmpty) {
            if (filterList.isNotEmpty()) {
                filterList.clear()
            }
            filterList.addAll(results)
        }
        withContext(Dispatchers.Main) {
            onFoundApp(filterList, isNotEmpty)
        }
        val text = newValue
        if (text.isNullOrEmpty()) {
            canStartFilterProcess = true
            return
        }
        newValue = null
        filter(text)
    }

    abstract fun onStartSearch()
    abstract fun onFoundApp(list: ArrayList<Map<String, Any>>, mode: Boolean)
}