package com.mcal.disassembler.activities

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.os.Message
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.mcal.disassembler.R
import com.mcal.disassembler.adapters.AppListAdapter
import com.mcal.disassembler.databinding.SymbolsActivityBinding
import com.mcal.disassembler.nativeapi.Dumper
import com.mcal.disassembler.utils.FileSaver
import com.mcal.disassembler.view.FloatingButton
import com.mcal.disassembler.view.SnackBar

class SymbolsActivity : AppCompatActivity(), AppListAdapter.AppItemClick {
    private val binding by lazy(LazyThreadSafetyMode.NONE) {
        SymbolsActivityBinding.inflate(layoutInflater)
    }
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
        list.also {
            it.sortBy { it["title"] as String }
            updateSymbolsSize(it)
        }
    }
    private var path: String? = null
    private var mDialog: ProgressDialog? = null
    private var mBar: SnackBar? = null
    private var lastValue: String? = null

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
        setContentView(binding.root)
        setupToolbar(getString(R.string.app_symbols))
        path = intent.extras?.getString("filePath")?.also { filePath ->
            val adapter = AppListAdapter(this, data, this, filePath)
            val recyclerView = binding.symbolsActivityListView
            setVisibility(recyclerView, View.VISIBLE)
            recyclerView.layoutManager = LinearLayoutManager(this)
            recyclerView.adapter = adapter

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
                    setVisibility(clearBtn, if (s.isEmpty()) View.GONE else View.VISIBLE)
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
        }
    }

    private fun setVisibility(view: View, mode: Int) {
        if (view.visibility != mode) {
            view.visibility = mode
        }
    }

    private fun updateSymbolsSize(list: MutableList<Map<String, Any>>) {
        val symbolsSizeView = binding.symbolsSize
        val oldText = symbolsSizeView.text.toString()
        val dataSize = list.size.toString()
        if (oldText != dataSize) {
            symbolsSizeView.text = "Symbols size: $dataSize"
        }
    }

    private fun setupToolbar(title: String) {
        val toolbar = binding.toolbarSymbols.toolbar
        setSupportActionBar(toolbar)
        supportActionBar?.apply {
            this.title = title
            this.setDisplayHomeAsUpEnabled(true)
            this.setDisplayShowHomeEnabled(true)
        }
    }

    fun showFloatingMenu(view: View?) {
        FloatingButton(this, path).show()
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

    @Deprecated("Deprecated in Java")
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

    override fun onFoundApp(list: MutableList<Map<String, Any>>, mode: Boolean) {
        setVisibility(
            findViewById<TextView>(R.id.symbols_not_found),
            if (mode) View.GONE else View.VISIBLE
        )
        updateSymbolsSize(list)
    }
}