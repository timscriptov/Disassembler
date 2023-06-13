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
        intent.extras?.let {
            val type = it.getInt("type")
            val name = it.getString("name")
            val demangledName = it.getString("demangledName")
            val path = it.getString("filePath")
            if (name != null && path != null) {
                mName = name
                mPath = path
                binding.symbolactivityImageView.apply {
                    when (type) {
                        1 -> {
                            setImageResource(R.drawable.ic_box_blue)
                        }

                        2 -> {
                            setImageResource(R.drawable.ic_box_red)
                        }

                        else -> {
                            setImageResource(R.drawable.ic_box_green)
                        }
                    }
                }
                binding.symbolactivityTextViewName.apply {
                    text = name
                }
                binding.symbolactivityTextViewDemangledName.apply {
                    text = demangledName
                }
                binding.symbolactivityTextViewArguments.apply {
                    text =
                        if (demangledName != null && demangledName.contains("(") && demangledName.lastIndexOf(
                                ")"
                            ) != -1
                        ) {
                            demangledName.substring(
                                demangledName.indexOf("(") + 1,
                                demangledName.lastIndexOf(")")
                            )
                        } else {
                            "NULL"
                        }
                }
                val symbolMainName = if (demangledName != null && demangledName.contains("(")) {
                    demangledName.substring(0, demangledName.indexOf("("))
                } else {
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
                    text = if (symbolMainName != null && symbolMainName.lastIndexOf("::") != -1) {
                        symbolMainName.substring(symbolMainName.lastIndexOf("::") + 2)
                    } else {
                        symbolMainName
                    }
                }
                binding.symbolactivityTextViewType.apply {
                    text = Tables.symbol_type[type]
                }
                if (name.startsWith("_ZTV")) {
                    binding.symbolactivityButtonFloat.visibility = View.VISIBLE
                }
                if (className != "NULL") {
                    binding.symbolactivityButtonFloatClass.visibility = View.VISIBLE
                }
            }
        }
    }

    fun toVtableActivity(view: View?) {
        CoroutineScope(Dispatchers.IO).launch {
            withContext(Dispatchers.Main) {
                showProgressDialog()
            }
            val vtable = VtableDumper.dump(mPath, mName)
            if (vtable != null) {
                toVtableActivity(vtable)
            }
            withContext(Dispatchers.Main) {
                dismissProgressDialog()
            }
        }
    }

    fun toClassActivity(view: View?) {
        CoroutineScope(Dispatchers.IO).launch {
            withContext(Dispatchers.Main) {
                showProgressDialog()
            }
            val vtable = VtableDumper.dump(mPath, mName)
            if (vtable != null) toClassActivity()
            withContext(Dispatchers.Main) {
                dismissProgressDialog()
            }
        }
    }

    private fun toClassActivity() {
        val name = className
        if (name == null || name == "" || name == " " || name.isEmpty() || name == "NULL") {
            return
        }
        val bundle = Bundle()
        bundle.putString("name", name)
        bundle.putString("path", mPath)
        val intent = Intent(this, ClassActivity::class.java)
        intent.putExtras(bundle)
        startActivity(intent)
    }

    private fun toVtableActivity(vtable: DisassemblerVtable?) {
        val bundle = Bundle()
        bundle.putString("name", mName)
        bundle.putString("path", mPath)
        Dumper.exploed.addElement(vtable)
        val intent = Intent(this, VtableActivity::class.java)
        intent.putExtras(bundle)
        startActivity(intent)
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
        when (item.itemId) {
            android.R.id.home -> {
                finish()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }
}