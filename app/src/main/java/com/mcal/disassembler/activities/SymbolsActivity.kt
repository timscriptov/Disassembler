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
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.mcal.disassembler.R
import com.mcal.disassembler.adapters.SymbolsItem
import com.mcal.disassembler.data.Preferences
import com.mcal.disassembler.data.Storage
import com.mcal.disassembler.databinding.ActivitySymbolsBinding
import com.mcal.disassembler.databinding.ProgressDialogBinding
import com.mcal.disassembler.nativeapi.Dumper
import com.mcal.disassembler.utils.FileHelper
import com.mcal.disassembler.view.FloatingButton
import com.mcal.disassembler.view.SnackBar
import com.mikepenz.fastadapter.FastAdapter
import com.mikepenz.fastadapter.adapters.ItemAdapter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SymbolsActivity : SymbolsSearchActivity() {
    private val binding by lazy(LazyThreadSafetyMode.NONE) {
        ActivitySymbolsBinding.inflate(layoutInflater)
    }
    private var dialogBinding: ProgressDialogBinding? = null

    private var mPath: String? = null
    private var dialog: AlertDialog? = null
    private var mBar: SnackBar? = null

    private val itemAdapter = ItemAdapter<SymbolsItem>()
    private val fastAdapter = FastAdapter.with(itemAdapter)

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setupToolbar(binding.toolbar, getString(R.string.app_symbols))
        intent.extras?.let { bundle ->
            val path = bundle.getString("path").also { path ->
                mPath = path
            }
            if (path != null) {
                val recyclerView = binding.symbolsActivityListView.apply {
                    adapter = fastAdapter
                }

                if (data.isEmpty()) {
                    initData()
                }

                val searchText = binding.search
                val clearBtn = binding.clearText.apply {
                    setOnClickListener {
                        searchText.setText("")
                    }
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
                        if (canStartFilterProcess) {
                            if (!TextUtils.equals(s, lastValue)) {
                                val constraint = s.toString()
                                lastValue = constraint
                                recyclerView.scrollToPosition(0)
                                canStartFilterProcess = false
                                filter(constraint)
                                return
                            }
                            return
                        }
                        newValue = s.toString()
                    }
                })
                binding.saveSymbols.setOnClickListener {
                    showProgressDialog()
                    mBar = SnackBar(this, getString(R.string.done)).also { snackBar ->
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
                            val storage = Storage(this@SymbolsActivity)
                            FileHelper.writeSymbolsToFile(
                                storage.getSymbolsDir(),
                                "Symbols.txt",
                                symbols
                            )
                            FileHelper.writeSymbolsToFile(
                                storage.getSymbolsDir(),
                                "Symbols_demangled.txt",
                                demangledSymbols
                            )
                            withContext(Dispatchers.Main) {
                                dismissProgressDialog()
                                snackBar.show()
                            }
                        }
                    }
                }
                binding.showFloatingMenu.setOnClickListener {
                    FloatingButton(this, path).show()
                }
                val preferences = Preferences(this)
                binding.regex.setBackgroundColor(
                    if (preferences.regex) ActivityCompat.getColor(
                        this,
                        R.color.colorAccent
                    ) else {
                        Color.TRANSPARENT
                    }
                )
                binding.regex.setOnClickListener {
                    if (preferences.regex) {
                        preferences.regex = false
                        binding.regex.setBackgroundColor(Color.TRANSPARENT)
                    } else {
                        preferences.regex = true
                        binding.regex.setBackgroundColor(
                            ActivityCompat.getColor(
                                this,
                                R.color.colorAccent
                            )
                        )
                    }
                }
            }
        }
    }

    private fun initData() {
        setVisibility(binding.progress, View.VISIBLE)
        val list = symbolsFilteredList
        if (list.isNotEmpty()) {
            list.clear()
        }
        var map: Map<String, Any>
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
        updateAdapter(list)
        val dataList = data
        if (dataList.isNotEmpty()) {
            dataList.clear()
        }
        dataList.addAll(list)
    }

    private fun updateSymbolsSize(list: ArrayList<Map<String, Any>>) {
        val symbolsSizeView = binding.symbolsSize
        val dataSize = list.size.toString()
        if (symbolsSizeView.text.toString() != dataSize) {
            symbolsSizeView.text = buildString {
                append(getString(R.string.symbols_count))
                append(dataSize)
            }
        }
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

    override fun onDestroy() {
        super.onDestroy()
        dismissProgressDialog()
    }

    private fun dismissProgressDialog() {
        dialog?.takeIf { it.isShowing }?.let {
            it.dismiss()
            dialog = null
            dialogBinding = null
        }
    }

    private fun updateDialogProgress(last: Int, total: Int) {
        dialogBinding?.let { binding ->
            binding.progress.apply {
                progress = last
                max = total
            }
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
        if (item.itemId == android.R.id.home) {
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    private fun updateAdapter(list: ArrayList<Map<String, Any>>) {
        val path = mPath
        if (path != null) {
            val adapter = itemAdapter
            if (adapter.adapterItemCount >= 0) {
                adapter.clear()
            }
            var item: Map<String, Any>
            for (i in list.indices) {
                item = list[i]
                adapter.add(
                    SymbolsItem()
                        .withContext(this)
                        .withId(i.toLong())
                        .withIcon(item["img"] as Int)
                        .withTitle(item["title"] as String)
                        .withSubTitle(item["info"] as String)
                        .withSymbolType(item["type"] as Int)
                        .withPath(path)
                )
            }
        }
        setVisibility(binding.progress, View.GONE)
    }

    override fun onStartSearch() {
        setVisibility(binding.progress, View.VISIBLE)
    }

    override fun onFoundApp(list: ArrayList<Map<String, Any>>, mode: Boolean) {
        setVisibility(
            binding.symbolsNotFound, if (mode) {
                View.GONE
            } else {
                View.VISIBLE
            }
        )
        setVisibility(
            binding.symbolsActivityListView, if (mode) {
                View.VISIBLE
            } else {
                View.GONE
            }
        )
        updateSymbolsSize(list)
        updateAdapter(list)
    }
}