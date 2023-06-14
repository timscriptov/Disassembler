package com.mcal.disassembler.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.mcal.disassembler.R
import com.mcal.disassembler.adapters.ListAdapter
import com.mcal.disassembler.data.Database
import com.mcal.disassembler.data.RecentsManager
import com.mcal.disassembler.data.Storage
import com.mcal.disassembler.databinding.ActivityRecentFilesBinding
import com.mcal.disassembler.databinding.ProgressDialogBinding
import com.mcal.disassembler.interfaces.DialogProgressListener
import com.mcal.disassembler.interfaces.MainView
import com.mcal.disassembler.nativeapi.DisassemblerDumper
import com.mcal.disassembler.nativeapi.Dumper
import com.mcal.disassembler.utils.FileHelper
import com.mcal.disassembler.utils.FilePickHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream

class RecentFilesActivity : BaseActivity(), MainView, DialogProgressListener {
    private val binding by lazy(LazyThreadSafetyMode.NONE) {
        ActivityRecentFilesBinding.inflate(
            layoutInflater
        )
    }
    private var dialogBinding: ProgressDialogBinding? = null
    private val paths = ArrayList<String>()
    private var dialog: AlertDialog? = null
    private var path: String? = null
    private var pickLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            result?.takeIf { it.resultCode == RESULT_OK }?.data?.data?.let { uri ->
                contentResolver.openInputStream(uri)?.let { inputStream ->
                    val soFile = File(
                        Storage(this).getHomeDir(),
                        FilePickHelper.getFileName(this, uri)
                    )
                    val filePath = soFile.path
                    FileHelper.copyFile(inputStream, FileOutputStream(soFile))
                    if (filePath.endsWith(".so")) {
                        RecentsManager.add(filePath)
                        updateRecents()
                        loadSo(filePath)
                    } else {
                        Toast.makeText(
                            this@RecentFilesActivity,
                            R.string.noFile,
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setupToolbar(binding.toolbar, getString(R.string.app_name))
        Database(this)
        updateRecents()
        binding.openLib.setOnClickListener {
            pickLauncher.launch(FilePickHelper.pickFile(false))
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun updateRecents() {
        paths.clear()
        val cursor = RecentsManager.getRecents()
        val welcomeLayout = binding.welcomeLayout
        val recentOpened = binding.items
        val adapter = ListAdapter(paths, this)
        if (cursor.count == 0) {
            recentOpened.visibility = View.GONE
            welcomeLayout.visibility = View.VISIBLE
            recentOpened.adapter = adapter
        } else {
            welcomeLayout.visibility = View.INVISIBLE
            recentOpened.visibility = View.VISIBLE
            if (cursor.count > 0) {
                while (cursor.moveToNext()) {
                    paths.add(cursor.getString(0))
                }
            }
            recentOpened.adapter = adapter
        }
        if (cursor.count > 0) {
            while (cursor.moveToNext()) {
                paths.add(cursor.getString(0))
            }
        }
        adapter.notifyDataSetChanged()
    }

    override fun loadSo(path: String) {
        showProgressDialog()
        this.path = path
        CoroutineScope(Dispatchers.IO).launch {
            DisassemblerDumper.load(path)
            Dumper.readData(this@RecentFilesActivity)
            toClassesActivity()
        }
    }

    private fun showProgressDialog() {
        dialog = MaterialAlertDialogBuilder(this).apply {
            dialogBinding = ProgressDialogBinding.inflate(layoutInflater).also { binding ->
                setView(binding.root)
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

    private fun toClassesActivity() {
        startActivity(Intent(this@RecentFilesActivity, SymbolsActivity::class.java).apply {
            putExtras(Bundle().apply {
                putString("path", path)
            })
        })
        dismissProgressDialog()
    }

    override fun updateDialogProgress(last: Int, total: Int) {
        runOnUiThread {
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
    }
}
