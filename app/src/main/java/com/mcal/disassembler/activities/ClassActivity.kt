package com.mcal.disassembler.activities

import android.content.Intent
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
import com.mcal.disassembler.databinding.ActivityClassBinding
import com.mcal.disassembler.databinding.ProgressDialogBinding
import com.mcal.disassembler.nativeapi.DisassemblerClass
import com.mcal.disassembler.nativeapi.DisassemblerVtable
import com.mcal.disassembler.nativeapi.Dumper
import com.mcal.disassembler.utils.ClassGeter
import com.mcal.disassembler.utils.FileHelper
import com.mcal.disassembler.utils.HeaderGenerator
import com.mcal.disassembler.view.SnackBar
import com.mcal.disassembler.vtable.VtableDumper
import com.mikepenz.fastadapter.FastAdapter
import com.mikepenz.fastadapter.adapters.ItemAdapter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ClassActivity : SymbolsSearchActivity() {
    private val binding by lazy(LazyThreadSafetyMode.NONE) {
        ActivityClassBinding.inflate(
            layoutInflater
        )
    }
    private var dialog: AlertDialog? = null
    private var dialogBinding: ProgressDialogBinding? = null
    private var mPath: String? = null
    private var mName: String? = null

    private val itemAdapter = ItemAdapter<SymbolsItem>()
    private val fastAdapter = FastAdapter.with(itemAdapter)

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setupToolbar(binding.toolbar, getString(R.string.app_name))
        intent.extras?.let {
            val path = it.getString("path").also { path ->
                mPath = path
            }
            val name = it.getString("name").also { name ->
                mName = name
            }
            if (path != null && name != null) {
                title = name

                val recyclerView = binding.classActivityListView.apply {
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
                        findClass()?.let { clazz ->
                            FileHelper.writeSymbolsToFile(
                                Storage(this@ClassActivity).getHeadersDir(),
                                getSaveName(name),
                                HeaderGenerator(clazz, findVtable()).generate()
                            )
                        }
                        withContext(Dispatchers.Main) {
                            SnackBar(
                                this@ClassActivity,
                                this@ClassActivity.getString(R.string.done)
                            ).show()
                            dismissProgressDialog()
                        }
                    }
                }
                binding.classactivityButtonFloat.setOnClickListener {
                    showLoadingProgressDialog()
                    CoroutineScope(Dispatchers.IO).launch {
                        VtableDumper.dump(path, getZTVName(name))?.let { vtable ->
                            toVtableActivity(vtable)
                        }
                        withContext(Dispatchers.Main) {
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
                if (hasVtable()) {
                    setVisibility(binding.classactivityButtonFloat, View.VISIBLE)
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
        findClass()?.let { classThis ->
            var map: Map<String, Any>
            for (i in classThis.symbols.indices) {
                map = HashMap()
                when (classThis.symbols[i].type) {
                    1 -> map["img"] = R.drawable.ic_box_blue
                    2 -> map["img"] = R.drawable.ic_box_red
                    else -> map["img"] = R.drawable.ic_box_green
                }
                map["title"] = classThis.symbols[i].demangledName
                map["info"] = classThis.symbols[i].name
                map["type"] = classThis.symbols[i].type
                list.add(map)
            }
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

    private fun findClass(): DisassemblerClass? {
        val name = mName ?: return null
        for (clazz in Dumper.classes) {
            if (clazz.name == name) {
                return clazz
            }
        }
        return ClassGeter.getClass(name)
    }

    private fun findVtable(): DisassemblerVtable? {
        val name = mName ?: return null
        for (clazz in Dumper.exploed) {
            if (clazz.name == getZTVName(name)) {
                return clazz
            }
        }
        return VtableDumper.dump(mPath, getZTVName(name))
    }

    private fun getZTVName(mangledName: String): String {
        val ret = StringBuilder("_ZTV")
        val names = mangledName.split("::".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        for (str in names) {
            ret.append(str.length).append(str)
        }
        return ret.toString()
    }

    private fun getSaveName(mangledName: String): String {
        val ret = StringBuilder()
        val names = mangledName.split("::".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        var isFirstName = true
        for (str in names) {
            if (isFirstName) {
                ret.append(str)
                isFirstName = false
            } else {
                ret.append("$").append(str)
            }
        }
        return "$ret.h"
    }

    private fun hasVtable(): Boolean {
        mName?.let { name ->
            val vtableThis = getZTVName(name)
            for (symbol in Dumper.symbols) {
                if (symbol.name == vtableThis) {
                    return true
                }
            }
        }
        return false
    }

    private fun toVtableActivity(vtable: DisassemblerVtable) {
        mName?.let { name ->
            Dumper.exploed.addElement(vtable)
            startActivity(Intent(this, VtableActivity::class.java).apply {
                putExtras(Bundle().apply {
                    putString("name", getZTVName(name))
                    putString("path", mPath)
                })
            })
        }
    }

    private fun showLoadingProgressDialog() {
        dialog = MaterialAlertDialogBuilder(this).apply {
            dialogBinding = ProgressDialogBinding.inflate(layoutInflater).also { binding ->
                setView(binding.root)
                binding.progress.isIndeterminate = true
            }
            setCancelable(false)
            setTitle(R.string.loading)
        }.create().also {
            it.show()
        }
    }

    private fun showSavingProgressDialog() {
        dialog = MaterialAlertDialogBuilder(this).apply {
            dialogBinding = ProgressDialogBinding.inflate(layoutInflater).also { binding ->
                binding.progress.isIndeterminate = true
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
            binding.classActivityListView, if (mode) {
                View.VISIBLE
            } else {
                View.GONE
            }
        )
        updateSymbolsSize(list)
        updateAdapter(symbolsFilteredList)
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
