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
import com.mcal.disassembler.adapters.VTableListAdapter
import com.mcal.disassembler.data.Preferences
import com.mcal.disassembler.data.Storage
import com.mcal.disassembler.databinding.ActivityVTableBinding
import com.mcal.disassembler.databinding.ProgressDialogBinding
import com.mcal.disassembler.nativeapi.DisassemblerDumper
import com.mcal.disassembler.nativeapi.DisassemblerVtable
import com.mcal.disassembler.nativeapi.Dumper
import com.mcal.disassembler.utils.FileSaver
import com.mcal.disassembler.view.SnackBar
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class VtableActivity : BaseActivity(), VTableListAdapter.SymbolItemClick {
    private val binding by lazy(LazyThreadSafetyMode.NONE) {
        ActivityVTableBinding.inflate(
            layoutInflater
        )
    }

    private val data by lazy(LazyThreadSafetyMode.NONE) {
        val list: MutableList<Map<String, Any>> = ArrayList()
        var map: MutableMap<String, Any>
        mVTable?.let { vtable ->
            for (i in vtable.vtables.indices) {
                map = HashMap()
                map["img"] = R.drawable.ic_box_blue
                map["title"] = vtable.vtables[i].demangledName
                map["info"] = vtable.vtables[i].name
                map["type"] = vtable.vtables[i].type
                list.add(map)
            }
        }
        list.sortBy {
            it["title"] as String
        }
        updateSymbolsSize(list)
        list
    }

    private var mPath: String? = null
    private var mName: String? = null
    private var mVTable: DisassemblerVtable? = null
    private var lastValue: String? = null

    private var dialog: AlertDialog? = null
    private var dialogBinding: ProgressDialogBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setupToolbar(binding.toolbar, getString(R.string.app_vtable))
        intent?.extras?.let {
            val path = it.getString("path").also { path ->
                mPath = path
            }
            val name = it.getString("name").also { name ->
                mName = name
            }
            if (path != null && name != null) {
                for (mvtable in Dumper.exploed) {
                    if (mvtable.name == mName) {
                        mVTable = mvtable
                    }
                }
                title = DisassemblerDumper.demangle(name)
                val adapter = VTableListAdapter(this, data, this, path)
                val recyclerView = binding.vtableActivityListView
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

    fun save(view: View?) {
        mVTable?.let { vtable ->
            CoroutineScope(Dispatchers.IO).launch {
                withContext(Dispatchers.Main) {
                    showSavingProgressDialog()
                }
                val size = vtable.vtables.size
                val listNames = arrayOfNulls<String>(size)
                for (i in vtable.vtables.indices) {
                    withContext(Dispatchers.Main) {
                        updateDialogProgress(i, size)
                    }
                    listNames[i] = vtable.vtables[i].name
                }
                val homeDir = Storage.getVTablesDir(this@VtableActivity).path
                FileSaver(homeDir, "$mName.txt", listNames).save()

                val listDemangledNames = arrayOfNulls<String>(size)
                for (i in vtable.vtables.indices) {
                    withContext(Dispatchers.Main) {
                        updateDialogProgress(i, size)
                    }
                    listDemangledNames[i] = vtable.vtables[i].demangledName
                }
                val demangledName = DisassemblerDumper.demangleOnly(mName)
                val fileName = demangledName.substring(demangledName.lastIndexOf(" ") + 1)
                FileSaver(homeDir, "$fileName.txt", listDemangledNames).save()
                withContext(Dispatchers.Main) {
                    SnackBar(this@VtableActivity, getString(R.string.done)).show()
                    dismissProgressDialog()
                }
            }
        }
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

    private fun dismissProgressDialog() {
        dialog?.let {
            it.dismiss()
            dialog = null
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
            return true
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

    private fun updateSymbolsSize(list: MutableList<Map<String, Any>>) {
        val symbolsSizeView = binding.symbolsSize
        val oldText = symbolsSizeView.text.toString()
        val dataSize = list.size.toString()
        if (oldText != dataSize) {
            symbolsSizeView.text = getString(R.string.symbols_count) + dataSize
        }
    }
}
