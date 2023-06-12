package com.mcal.disassembler.activities

import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.mcal.disassembler.R
import com.mcal.disassembler.adapters.SymbolsListAdapter
import com.mcal.disassembler.data.Preferences
import com.mcal.disassembler.data.Storage.getHomeDir
import com.mcal.disassembler.databinding.ActivitySymbolsBinding
import com.mcal.disassembler.databinding.ProgressDialogBinding
import com.mcal.disassembler.nativeapi.Dumper
import com.mcal.disassembler.utils.FileSaver
import com.mcal.disassembler.view.FloatingButton
import com.mcal.disassembler.view.SnackBar
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SymbolsActivity : BaseActivity(), SymbolsListAdapter.SymbolItemClick {
    private val binding by lazy(LazyThreadSafetyMode.NONE) {
        ActivitySymbolsBinding.inflate(layoutInflater)
    }
    private var dialogBinding: ProgressDialogBinding? = null
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
        list.sortBy {
            it["title"] as String
        }
        updateSymbolsSize(list)
        list
    }

    private var path: String? = null
    private var dialog: AlertDialog? = null
    private var mBar: SnackBar? = null
    private var lastValue: String? = null

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setupToolbar(binding.toolbar, getString(R.string.app_symbols))
        path = intent.extras?.getString("filePath")?.also { filePath ->
            val adapter = SymbolsListAdapter(this, data, this, filePath)
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
        binding.regex.setBackgroundColor(
            if (Preferences.isRegexEnabled()) ActivityCompat.getColor(
                this,
                R.color.colorAccent
            ) else Color.TRANSPARENT
        )
        binding.regex.setOnClickListener {
            if (Preferences.isRegexEnabled()) {
                Preferences.setRegexEnabled(false)
                binding.regex.setBackgroundColor(Color.TRANSPARENT)
            } else {
                Preferences.setRegexEnabled(true)
                binding.regex.setBackgroundColor(ActivityCompat.getColor(this, R.color.colorAccent))
            }
        }
    }

    private fun updateSymbolsSize(list: MutableList<Map<String, Any>>) {
        val symbolsSizeView = binding.symbolsSize
        val oldText = symbolsSizeView.text.toString()
        val dataSize = list.size.toString()
        if (oldText != dataSize) {
            symbolsSizeView.text = getString(R.string.symbols_count) + dataSize
        }
    }

    fun showFloatingMenu(view: View?) {
        FloatingButton(this, path).show()
    }

    private fun showProgressDialog() {
        dialog = MaterialAlertDialogBuilder(this).apply {
            dialogBinding = ProgressDialogBinding.inflate(layoutInflater).also { binding ->
                setView(binding.root)
            }
            setCancelable(false)
            setTitle(R.string.saving)
        }.create().also {
            it.show()
        }
    }

    fun saveSymbols(view: View?) {
        showProgressDialog()
        mBar = SnackBar(this, getString(R.string.done))
        CoroutineScope(Dispatchers.IO).launch {
            val size = Dumper.symbols.size
            val symbols = arrayOfNulls<String>(size)
            val demangledSymbols = arrayOfNulls<String>(size)
            for (i in Dumper.symbols.indices) {
                withContext(Dispatchers.Main) {
                    updateDialogProgress(i, size)
                }
                symbols[i] = Dumper.symbols[i].name
                demangledSymbols[i] = Dumper.symbols[i].demangledName
            }
            FileSaver(
                getHomeDir(this@SymbolsActivity).path + "/Disassembler/symbols/",
                "Symbols.txt",
                symbols
            ).save()
            FileSaver(
                getHomeDir(this@SymbolsActivity).path + "/Disassembler/symbols/",
                "Symbols_demangled.txt",
                demangledSymbols
            ).save()
            withContext(Dispatchers.Main) {
                dialog?.dismiss().also {
                    dialog = null
                    dialogBinding = null
                }
                mBar?.show() ?: run {
                    SnackBar(
                        this@SymbolsActivity,
                        this@SymbolsActivity.getString(R.string.done)
                    ).show()
                }
            }
        }
    }

    private fun updateDialogProgress(last: Int, total: Int) {
        dialogBinding?.let { binding ->
            val progressView = binding.progress
            progressView.progress = last
            progressView.max = total
            binding.count.text = buildString {
                append(last)
                append(" / ")
                append(total)
            }
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        SnackBar(this, getString(R.string.againToExit)).apply {
            show()
            dismissTimer = 2500
            setOnBackPressedListener { finish() }
        }
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
            binding.symbolsNotFound, if (mode) {
                View.GONE
            } else {
                View.VISIBLE
            }
        )
        updateSymbolsSize(list)
    }
}