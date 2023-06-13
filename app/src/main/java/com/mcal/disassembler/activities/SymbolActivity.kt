package com.mcal.disassembler.activities

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AlertDialog
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.mcal.disassembler.R
import com.mcal.disassembler.databinding.ActivitySymbolBinding
import com.mcal.disassembler.databinding.ProgressDialogBinding
import com.mcal.disassembler.nativeapi.DisassemblerVtable
import com.mcal.disassembler.nativeapi.Dumper
import com.mcal.disassembler.vtable.Tables
import com.mcal.disassembler.vtable.VtableDumper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SymbolActivity : BaseActivity() {
    private val binding by lazy(LazyThreadSafetyMode.NONE) {
        ActivitySymbolBinding.inflate(
            layoutInflater
        )
    }
    private var dialog: AlertDialog? = null
    private var dialogBinding: ProgressDialogBinding? = null

    private var mPath: String? = null
    private var mName: String? = null
    private var className: String? = null

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setupToolbar(binding.toolbar, getString(R.string.app_symbol))
        intent.extras?.let { bundle ->
            val name = bundle.getString("name").also {
                mName = it
            }
            val path = bundle.getString("filePath").also {
                mPath = it
            }
            if (name != null && path != null) {
                val type = bundle.getInt("type")
                binding.symbolactivityImageView.apply {
                    when (type) {
                        1 -> setImageResource(R.drawable.ic_box_blue)
                        2 -> setImageResource(R.drawable.ic_box_red)
                        else -> setImageResource(R.drawable.ic_box_green)
                    }
                }
                binding.symbolactivityTextViewName.apply {
                    text = name
                }
                val demangledName = bundle.getString("demangledName")
                binding.symbolactivityTextViewDemangledName.apply {
                    text = demangledName
                }
                binding.symbolactivityTextViewArguments.apply {
                    text = demangledName?.takeIf { it.contains("(") && it.lastIndexOf(")") != -1 }
                        ?.let {
                            it.substring(it.indexOf("(") + 1, it.lastIndexOf(")"))
                        } ?: run {
                        "NULL"
                    }
                }
                val symbolMainName = demangledName?.takeIf { it.contains("(") }?.let {
                    it.substring(0, it.indexOf("("))
                } ?: run {
                    demangledName
                }

                className = if (symbolMainName != null && symbolMainName.lastIndexOf("::") != -1) {
                    symbolMainName.substring(0, symbolMainName.lastIndexOf("::"))
                } else if (symbolMainName != null && symbolMainName.startsWith("vtable")) {
                    symbolMainName.substring(symbolMainName.lastIndexOf(" ") + 1)
                } else {
                    "NULL"
                }

                binding.symbolactivityTextClass.apply {
                    text = className
                }
                binding.symbolactivityTextViewSymbolMainName.apply {
                    text = symbolMainName?.takeIf { it.lastIndexOf("::") != -1 }?.let {
                        it.substring(it.lastIndexOf("::") + 2)
                    } ?: run {
                        symbolMainName
                    }
                }
                binding.symbolactivityTextViewType.apply {
                    text = Tables.symbol_type[type]
                }
                if (name.startsWith("_ZTV")) {
                    setVisibility(binding.symbolactivityButtonFloat, View.VISIBLE)
                }
                if (className != "NULL") {
                    setVisibility(binding.symbolactivityButtonFloatClass, View.VISIBLE)
                }
                binding.symbolactivityButtonFloat.setOnClickListener {
                    CoroutineScope(Dispatchers.IO).launch {
                        withContext(Dispatchers.Main) {
                            showProgressDialog()
                        }
                        VtableDumper.dump(path, name)?.let { vtable ->
                            toVtableActivity(path, name, vtable)
                        }
                        withContext(Dispatchers.Main) {
                            dismissProgressDialog()
                        }
                    }
                }
                binding.symbolactivityButtonFloatClass.setOnClickListener {
                    CoroutineScope(Dispatchers.IO).launch {
                        withContext(Dispatchers.Main) {
                            showProgressDialog()
                        }
                        VtableDumper.dump(path, name)?.let {
                            toClassActivity(path)
                        }
                        withContext(Dispatchers.Main) {
                            dismissProgressDialog()
                        }
                    }
                }
            }
        }
    }

    private fun toClassActivity(path: String) {
        val name = className
        if (name == null || name.trim().isEmpty() || name == "NULL") {
            return
        }
        startActivity(Intent(this, ClassActivity::class.java).apply {
            putExtras(Bundle().apply {
                putString("name", name)
                putString("path", path)
            })
        })
    }

    private fun toVtableActivity(path: String, name: String, vtable: DisassemblerVtable?) {
        Dumper.exploed.addElement(vtable)
        startActivity(Intent(this, VtableActivity::class.java).apply {
            putExtras(Bundle().apply {
                putString("name", name)
                putString("path", path)
            })
        })
    }

    private fun showProgressDialog() {
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
}
