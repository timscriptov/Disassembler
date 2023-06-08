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
import com.mcal.disassembler.activities.SymbolsActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Locale
import java.util.regex.Matcher

class AppListAdapter(
    private val context: Context,
    private val data: MutableList<Map<String, Any>>?,
    private val listener: AppItemClick,
    private val path: String
) :
    RecyclerView.Adapter<AppListAdapter.AppListViewHolder>() {
    var newValue: String? = null
    var canStartFilterProcess = true
    private var appFilterList = data

    interface AppItemClick {
        fun onClick(view: View)
        fun onFoundApp(mode: Boolean)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AppListViewHolder {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.item_applist, parent, false)
        return AppListViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: AppListViewHolder, position: Int) {
        data?.get(position)?.let { item ->
            holder.icon.setBackgroundResource((item["img"] as Int))
            holder.title.text = item["title"] as String
            holder.info.text = item["info"] as String
            holder.type = item["type"] as Int
            holder.itemView.setOnClickListener {
                it?.let {it1->
                    listener.onClick(it1)

                    val bundle = Bundle()
                    bundle.putString("demangledName", item["title"] as String)
                    bundle.putString("name", item["info"] as String)
                    bundle.putInt("type", item["type"] as Int)
                    bundle.putString("filePath", path)
                    val intent = Intent(context, SymbolActivity::class.java)
                    intent.putExtras(bundle)
                    context.startActivity(intent)
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return appFilterList?.size ?: 0
    }

    class AppListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val icon: ImageView = itemView.findViewById(R.id.symbolslistitemimg)
        val title: TextView = itemView.findViewById(R.id.symbolslistitemTextViewtop)
        val info: TextView = itemView.findViewById(R.id.symbolslistitemTextViewbottom)
        var type = 0
    }

    fun filter(constraint: CharSequence?) = CoroutineScope(Dispatchers.IO).launch {
        val list = mutableListOf<Map<String, Any>>()
        val charSearch = constraint.toString().lowercase(Locale.ROOT)
        appFilterList = if (charSearch.isEmpty()) {
            data
        } else {
            data?.forEach { row ->
                val name = row["title"] as String
                val index = name.indexOf(charSearch)
                if (index == 0) {
                    list.add(row)
                } else if (name.contains(charSearch)) {
                    list.add(row)
                }
            }
            list
        }
        CoroutineScope(Dispatchers.Main).launch {
            publishResults(appFilterList)
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun publishResults(results: MutableList<Map<String, Any>>?) {
        results?.let {
            val isNotEmpty = results.isNotEmpty()
            listener.onFoundApp(isNotEmpty)
            if (isNotEmpty) {
                appFilterList = results
                notifyDataSetChanged()
            }
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