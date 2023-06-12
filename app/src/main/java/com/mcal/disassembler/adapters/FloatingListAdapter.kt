package com.mcal.disassembler.adapters

import android.annotation.SuppressLint
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.mcal.disassembler.R
import com.mcal.disassembler.data.Preferences
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Locale
import java.util.regex.Matcher
import java.util.regex.Pattern

class FloatingListAdapter(
    private val context: Context,
    private val listener: FloatingListItemClick,
    private val data: MutableList<Map<String, Any>>,
) :
    RecyclerView.Adapter<FloatingListAdapter.SymbolsListViewHolder>() {
    var newValue: String? = null
    var canStartFilterProcess = true
    private var symbolsFilteredList = data

    interface FloatingListItemClick {
        fun onFoundApp(list: MutableList<Map<String, Any>>, mode: Boolean)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SymbolsListViewHolder {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.item_symbols, parent, false)
        return SymbolsListViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: SymbolsListViewHolder, position: Int) {
        val item = symbolsFilteredList[position]
        holder.icon.setBackgroundResource((item["img"] as Int))
        val title = item["title"] as String
        holder.title.text = title
        holder.info.text = item["info"] as String
        holder.type = item["type"] as Int
        holder.itemView.setOnClickListener {
            (context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager).setPrimaryClip(
                ClipData.newPlainText("text", title)
            )
            Toast.makeText(context, title, Toast.LENGTH_LONG).show()
        }
    }

    override fun getItemCount(): Int {
        return symbolsFilteredList.size
    }

    class SymbolsListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val icon: ImageView = itemView.findViewById(R.id.symbolslistitemimg)
        val title: TextView = itemView.findViewById(R.id.symbolslistitemTextViewtop)
        val info: TextView = itemView.findViewById(R.id.symbolslistitemTextViewbottom)
        var type = 0
    }

    fun filter(constraint: CharSequence?) = CoroutineScope(Dispatchers.IO).launch {
        val list = mutableListOf<Map<String, Any>>()
        val charSearch = constraint.toString().lowercase(Locale.ROOT)
        if (charSearch.isEmpty()) {
            list.addAll(data)
        } else {
            val isRegex = Preferences.isRegexEnabled()
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
        listener.onFoundApp(symbolsFilteredList, isNotEmpty)
        if (isNotEmpty) {
            symbolsFilteredList = results.also { list ->
                list.sortBy { it["title"] as String }
            }
            notifyDataSetChanged()
        }
        val text = newValue
        if (text.isNullOrEmpty()) {
            canStartFilterProcess = true
            return
        }
        newValue = null
        filter(text)
    }
}