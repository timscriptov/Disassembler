package com.mcal.disassembler.activities

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.os.Message
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.AdapterView.OnItemClickListener
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mcal.disassembler.R
import com.mcal.disassembler.adapters.AppListAdapter
import com.mcal.disassembler.nativeapi.Dumper
import com.mcal.disassembler.utils.FileSaver
import com.mcal.disassembler.view.CenteredToolBar
import com.mcal.disassembler.view.FloatingButton
import com.mcal.disassembler.view.SnackBar

class SymbolsActivity : AppCompatActivity(), AppListAdapter.AppItemClick {
    private var data = mutableListOf<Map<String, Any>>()
    private var path: String? = null
    private var mDialog: ProgressDialog? = null
    private var mBar: SnackBar? = null

    @SuppressLint("HandlerLeak")
    private val mHandler: Handler = object : Handler() {
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            mDialog?.dismiss().also {
                mDialog = null
            }
            mBar?.show() ?: {
                SnackBar(
                    this@SymbolsActivity,
                    this@SymbolsActivity.getString(R.string.done)
                ).show()
            }
        }
    }

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.symbols_activity)
        setupToolbar(getString(R.string.app_symbols))
        getData(data)

        path = intent.extras?.getString("filePath")?.also {
            val adapter = AppListAdapter(this, data, this, it)
            val recyclerView = findViewById<RecyclerView>(R.id.symbols_activity_list_view)
            setVisibility(recyclerView, View.VISIBLE)
            recyclerView.layoutManager = LinearLayoutManager(this)
            recyclerView.adapter = adapter
        }
    }

    private fun setVisibility(view: View, mode: Int) {
        if (view.visibility != mode) {
            view.visibility = mode
        }
    }

    private fun setupToolbar(title: String) {
        val toolbar = findViewById<CenteredToolBar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar!!.title = title
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)
    }

    private fun getData(data: MutableList<Map<String, Any>>) {
        if (data.isNotEmpty()) {
            data.clear()
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
            data.add(map)
        }
    }

    fun showFloatingMenu(view: View?) {
        FloatingButton(this, path).show()
    }

    fun showSearch(view: View?) {
        val i = Intent(this, SearchActivity::class.java)
        val bundle = Bundle()
        bundle.putString("filePath", path)
        i.putExtras(bundle)
        startActivity(i)
    }

    private fun _saveSymbols() {
        val strings = arrayOfNulls<String>(Dumper.symbols.size)
        for (i in Dumper.symbols.indices) strings[i] = Dumper.symbols[i].name
        val saver = FileSaver(
            Environment.getExternalStorageDirectory().toString() + "/Disassembler/symbols/",
            "Symbols.txt",
            strings
        )
        saver.save()
        val strings_ = arrayOfNulls<String>(Dumper.symbols.size)
        for (i in Dumper.symbols.indices) strings_[i] = Dumper.symbols[i].demangledName
        val saver_ = FileSaver(
            Environment.getExternalStorageDirectory().toString() + "/Disassembler/symbols/",
            "Symbols_demangled.txt",
            strings_
        )
        saver_.save()
    }

    fun saveSymbols(view: View?) {
        mDialog = ProgressDialog(this)
        mDialog!!.setTitle(getString(R.string.saving))
        mDialog!!.show()
        mBar = SnackBar(this, getString(R.string.done))
        object : Thread() {
            override fun run() {
                _saveSymbols()
                val msg = Message()
                mHandler.sendMessage(msg)
            }
        }.start()
    }

    override fun onBackPressed() {
        val bar = SnackBar(this, getString(R.string.againToExit))
        bar.show()
        bar.dismissTimer = 2500
        bar.setOnBackPressedListener { finish() }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                finish()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    internal class ViewHolder {
        var info: AppCompatTextView? = null
        var type = 0
        var img: AppCompatImageView? = null
        var title: AppCompatTextView? = null
    }

    private inner class ItemClickListener : OnItemClickListener {
        override fun onItemClick(arg0: AdapterView<*>?, view: View, arg2: Int, arg3: Long) {

        }
    }

    override fun onClick(view: View) {

    }

    override fun onFoundApp(mode: Boolean) {
    }
}