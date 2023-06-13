package com.mcal.disassembler.activities

import android.annotation.SuppressLint
import com.mcal.disassembler.data.Preferences
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Locale
import java.util.regex.Matcher
import java.util.regex.Pattern

abstract class SymbolsSearchActivity : BaseActivity() {
    val data = mutableListOf<Map<String, Any>>()
    val symbolsFilteredList = mutableListOf<Map<String, Any>>()

    var lastValue: String? = null
    var newValue: String? = null
    var canStartFilterProcess = true

    fun filter(constraint: CharSequence) = CoroutineScope(Dispatchers.IO).launch {
        val listStart = mutableListOf<Map<String, Any>>()
        val listEnd = mutableListOf<Map<String, Any>>()
        val charSearch = constraint.toString().lowercase(Locale.ROOT)
        if (charSearch.isEmpty()) {
            listStart.addAll(data)
        } else {
            val isRegex = Preferences(this@SymbolsSearchActivity).regex
            var name: String?
            val pattern = Pattern.compile(charSearch)
            var matcher: Matcher
            for (symbol in data) {
                name = (symbol["title"] as? String)?.lowercase(Locale.ROOT)
                if (name != null) {
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
            }
        }
        val offset1 = listStart.size
        val list: MutableList<Map<String, Any>> = ArrayList(offset1 + listEnd.size)
        for (app in listStart) {
            list.add(0, app)
        }
        for (app in listEnd) {
            list.add(offset1, app)
        }
        withContext(Dispatchers.Main) {
            publishResults(list)
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun publishResults(results: MutableList<Map<String, Any>>) {
        val isNotEmpty = results.isNotEmpty()
        val filterList = symbolsFilteredList
        if (isNotEmpty) {
            if (filterList.isNotEmpty()) {
                filterList.clear()
            }
            filterList.addAll(results)
        }
        onFoundApp(filterList, isNotEmpty)
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