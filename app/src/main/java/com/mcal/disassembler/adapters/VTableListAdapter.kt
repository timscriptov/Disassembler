package com.mcal.disassembler.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.mcal.disassembler.R
import com.mcal.disassembler.activities.SymbolActivity
import com.mcal.disassembler.data.Preferences
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Locale
import java.util.regex.Matcher
import java.util.regex.Pattern

class VTableListAdapter(
    private val context: Context,
    private val data: MutableList<Map<String, Any>>,
    private val listener: SymbolItemClick,
    private val path: String
) : RecyclerView.Adapter<VTableListAdapter.VTableListViewHolder>() {
    var newValue: String? = null
    var canStartFilterProcess = true
    private var symbolsFilteredList = data

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VTableListViewHolder {
        val itemView =
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_symbols, parent, false)
        return VTableListViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: VTableListViewHolder, position: Int) {
        val item = data[position]
        holder.icon.setBackgroundResource((item["img"] as Int))
        holder.title.text = item["title"] as String
        holder.info.text = item["info"] as String
        holder.type = item["type"] as Int
        holder.itemView.setOnClickListener {
            val bundle = Bundle()
            bundle.putString("demangledName", holder.title.text as String)
            bundle.putString("name", holder.info.text as String)
            bundle.putInt("type", holder.type)
            bundle.putString("filePath", path)
            val intent = Intent(context, SymbolActivity::class.java)
            intent.putExtras(bundle)
            context.startActivity(intent)
        }
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
            symbolsFilteredList = results.also {
                it.sortBy { it["title"] as String }
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

    class VTableListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val icon: ImageView = itemView.findViewById(R.id.symbolslistitemimg)
        val title: TextView = itemView.findViewById(R.id.symbolslistitemTextViewtop)
        val info: TextView = itemView.findViewById(R.id.symbolslistitemTextViewbottom)
        var type = 0
    }

    interface SymbolItemClick {
        fun onFoundApp(list: MutableList<Map<String, Any>>, mode: Boolean)
    }
}