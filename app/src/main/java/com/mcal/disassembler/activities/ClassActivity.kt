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
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.mcal.disassembler.R
import com.mcal.disassembler.adapters.ClassSymbolsListAdapter
import com.mcal.disassembler.data.Preferences
import com.mcal.disassembler.data.Storage.getHomeDir
import com.mcal.disassembler.databinding.ActivityClassBinding
import com.mcal.disassembler.databinding.ProgressDialogBinding
import com.mcal.disassembler.nativeapi.DisassemblerClass
import com.mcal.disassembler.nativeapi.DisassemblerVtable
import com.mcal.disassembler.nativeapi.Dumper
import com.mcal.disassembler.utils.ClassGeter
import com.mcal.disassembler.utils.FileSaver
import com.mcal.disassembler.utils.HeaderGenerator
import com.mcal.disassembler.view.SnackBar
import com.mcal.disassembler.vtable.VtableDumper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ClassActivity : BaseActivity(), ClassSymbolsListAdapter.SymbolItemClick {
    private val binding by lazy(LazyThreadSafetyMode.NONE) {
        ActivityClassBinding.inflate(
            layoutInflater
        )
    }
    private var dialog: AlertDialog? = null
    private var dialogBinding: ProgressDialogBinding? = null
    private var mPath: String? = null
    private var mName: String? = null
    private var lastValue: String? = null

    private val data by lazy(LazyThreadSafetyMode.NONE) {
        val list: MutableList<Map<String, Any>> = ArrayList()
        var map: MutableMap<String, Any>
        findClass()?.let { classThis ->
            for (i in classThis.symbols.indices) {
                map = HashMap()
                when (classThis.symbols[i].type) {
                    1 -> {
                        map["img"] = R.drawable.ic_box_blue
                    }

                    2 -> {
                        map["img"] = R.drawable.ic_box_red
                    }

                    else -> {
                        map["img"] = R.drawable.ic_box_green
                    }
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
        list
    }

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setupToolbar(getString(R.string.app_name))
        intent.extras?.let {
            val path = it.getString("path").also { path ->
                mPath = path
            }
            val name = it.getString("name").also { name ->
                mName = name
            }

            if (path != null && name != null) {
                val adapter = ClassSymbolsListAdapter(this, data, this, path)
                val recyclerView = binding.classActivityListView
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

                title = name
                if (hasVtable()) {
                    binding.classactivityButtonFloat.visibility = View.VISIBLE
                }
            }
        }
    }

    private fun setupToolbar(title: String) {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.apply {
            setTitle(title)
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowHomeEnabled(true)
        }
    }

    fun save(view: View?) {
        mName?.let { name ->
            CoroutineScope(Dispatchers.IO).launch {
                withContext(Dispatchers.Main) {
                    showSavingProgressDialog()
                }
                findClass()?.let { clazz ->
                    FileSaver(
                        getHomeDir(this@ClassActivity).path + "/Disassembler/headers/",
                        getSaveName(name),
                        HeaderGenerator(clazz, findVtable(), mPath).generate()
                    ).save()
                }
                withContext(Dispatchers.Main) {
                    SnackBar(this@ClassActivity, this@ClassActivity.getString(R.string.done)).show()
                    dismissProgressDialog()
                }
            }
        }
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

    fun toVtableActivity(view: View?) {
        showLoadingProgressDialog()
        mName?.let { name ->
            CoroutineScope(Dispatchers.IO).launch {
                val vtable = VtableDumper.dump(mPath, getZTVName(name))
                if (vtable != null) {
                    toVtableActivity_(vtable)
                }
                dismissProgressDialog()
            }
        }
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

    private fun toVtableActivity_(vtable: DisassemblerVtable) {
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