package com.mcal.disassembler.adapters

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.mcal.disassembler.R
import com.mcal.disassembler.activities.SymbolActivity
import com.mcal.disassembler.utils.StringHelper
import com.mikepenz.fastadapter.items.AbstractItem

class SymbolsItem : AbstractItem<SymbolsItem.ViewHolder>() {
    var icon: Int = R.drawable.ic_box_blue
    var title: String? = null
    var subtitle: String? = null
    var symbolType: Int = 0
    var context: Context? = null
    var path: String? = null

    override val type: Int
        get() = R.id.item_container

    override val layoutRes: Int
        get() = R.layout.item_symbols

    override fun getViewHolder(v: View): ViewHolder {
        return ViewHolder(v)
    }

    fun withId(id: Long): SymbolsItem {
        this.identifier = id
        return this
    }

    fun withIcon(icon: Int): SymbolsItem {
        this.icon = icon
        return this
    }

    fun withTitle(title: String): SymbolsItem {
        this.title = title
        return this
    }

    fun withSubTitle(subtitle: String): SymbolsItem {
        this.subtitle = subtitle
        return this
    }

    fun withSymbolType(type: Int): SymbolsItem {
        this.symbolType = type
        return this
    }

    fun withContext(context: Context): SymbolsItem {
        this.context = context
        return this
    }

    fun withPath(path: String): SymbolsItem {
        this.path = path
        return this
    }


    override fun bindView(holder: ViewHolder, payloads: List<Any>) {
        super.bindView(holder, payloads)
        holder.icon.setBackgroundResource(icon)
        holder.title.text = title
        holder.info.text = subtitle
        holder.type = symbolType
        context?.let { context ->
            holder.itemView.setOnClickListener {
                context.startActivity(Intent(context, SymbolActivity::class.java).apply {
                    putExtras(Bundle().apply {
                        putString("demangledName", holder.title.text as String)
                        putString("name", holder.info.text as String)
                        putInt("type", holder.type)
                        putString("filePath", path)
                    })
                })
            }
            holder.itemView.setOnLongClickListener {
                StringHelper.copyToClipboard("$title // $subtitle", context)
                return@setOnLongClickListener true
            }
        }
    }

    override fun unbindView(holder: ViewHolder) {
        super.unbindView(holder)
        holder.icon.setImageDrawable(null)
        holder.title.text = null
        holder.info.text = null
        holder.type = 0
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val icon: ImageView = itemView.findViewById(R.id.symbolslistitemimg)
        val title: TextView = itemView.findViewById(R.id.symbolslistitemTextViewtop)
        val info: TextView = itemView.findViewById(R.id.symbolslistitemTextViewbottom)
        var type = 0
    }
}