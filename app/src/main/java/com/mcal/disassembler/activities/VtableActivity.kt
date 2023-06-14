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
import com.mcal.disassembler.databinding.ActivityVTableBinding
import com.mcal.disassembler.databinding.ProgressDialogBinding
import com.mcal.disassembler.nativeapi.DisassemblerDumper
import com.mcal.disassembler.nativeapi.DisassemblerVtable
import com.mcal.disassembler.nativeapi.Dumper
import com.mcal.disassembler.utils.FileHelper
import com.mcal.disassembler.view.SnackBar
import com.mikepenz.fastadapter.FastAdapter
import com.mikepenz.fastadapter.adapters.ItemAdapter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class VtableActivity : SymbolsSearchActivity() {
    private val binding by lazy(LazyThreadSafetyMode.NONE) {
        ActivityVTableBinding.inflate(
            layoutInflater
        )
    }

    private var mPath: String? = null
    private var mName: String? = null
    private var mVTable: DisassemblerVtable? = null

    private var dialog: AlertDialog? = null
    private var dialogBinding: ProgressDialogBinding? = null

    private val itemAdapter = ItemAdapter<SymbolsItem>()
    private val fastAdapter = FastAdapter.with(itemAdapter)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setupToolbar(binding.toolbar, getString(R.string.app_vtable))
        intent?.extras?.let { bundle ->
            val path = bundle.getString("path").also { path ->
                mPath = path
            }
            val name = bundle.getString("name").also { name ->
                mName = name
            }
            var vTable: DisassemblerVtable? = null
            for (item in Dumper.exploed) {
                if (item.name == mName) {
                    vTable = item.also {
                        mVTable = it
                    }
                }
            }
            if (path != null && name != null && vTable != null) {
                title = DisassemblerDumper.demangle(name)

                val recyclerView = binding.vtableActivityListView.apply {
                    adapter = fastAdapter
                }

                if (data.isEmpty()) {
                    initData(vTable)
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
                        setVisibility(
                            clearBtn, if (s.isEmpty()) {
                                View.GONE
                            } else {
                                View.VISIBLE
                            }
                        )
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
                binding.save.setOnClickListener {
                    CoroutineScope(Dispatchers.IO).launch {
                        withContext(Dispatchers.Main) {
                            showSavingProgressDialog()
                        }
                        val size = vTable.vtables.size
                        val listNames = arrayOfNulls<String>(size)
                        for (i in vTable.vtables.indices) {
                            withContext(Dispatchers.Main) {
                                updateDialogProgress(i, size)
                            }
                            listNames[i] = vTable.vtables[i].name
                        }
                        val homeDir = Storage(this@VtableActivity).getVTablesDir()
                        FileHelper.writeSymbolsToFile(homeDir, "$name.txt", listNames)

                        val listDemangledNames = arrayOfNulls<String>(size)
                        for (i in vTable.vtables.indices) {
                            withContext(Dispatchers.Main) {
                                updateDialogProgress(i, size)
                            }
                            listDemangledNames[i] = vTable.vtables[i].demangledName
                        }
                        val demangledName = DisassemblerDumper.demangleOnly(name)
                        val fileName =
                            demangledName.substring(demangledName.lastIndexOf(" ") + 1)
                        FileHelper.writeSymbolsToFile(
                            homeDir,
                            "$fileName.txt",
                            listDemangledNames
                        )
                        withContext(Dispatchers.Main) {
                            SnackBar(this@VtableActivity, getString(R.string.done)).show()
                            dismissProgressDialog()
                        }
                    }
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

    private fun initData(vtable: DisassemblerVtable) {
        setVisibility(binding.progress, View.VISIBLE)
        val list = symbolsFilteredList
        if (list.isNotEmpty()) {
            list.clear()
        }
        var map: Map<String, Any>
        for (i in vtable.vtables.indices) {
            map = HashMap()
            map["img"] = R.drawable.ic_box_blue
            map["title"] = vtable.vtables[i].demangledName
            map["info"] = vtable.vtables[i].name
            map["type"] = vtable.vtables[i].type
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

    private fun showSavingProgressDialog() {
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

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
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
            binding.vtableActivityListView, if (mode) {
                View.VISIBLE
            } else {
                View.GONE
            }
        )
        updateSymbolsSize(list)
        if (itemAdapter.adapterItemCount >= 0) {
            itemAdapter.clear()
        }
        updateAdapter(list)
    }

    private fun updateAdapter(list: ArrayList<Map<String, Any>>) {
        mPath?.let { path ->
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
}
