package com.mcal.disassembler.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.mcal.disassembler.R
import com.mcal.disassembler.adapters.ListAdapter
import com.mcal.disassembler.data.Database
import com.mcal.disassembler.data.RecentsManager
import com.mcal.disassembler.data.Storage
import com.mcal.disassembler.databinding.ActivityMainBinding
import com.mcal.disassembler.databinding.ProgressDialogBinding
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

class MainActivity : AppCompatActivity(), MainView, Dumper.DumperListener {
    private val binding by lazy(LazyThreadSafetyMode.NONE) {
        ActivityMainBinding.inflate(
            layoutInflater
        )
    }
    private var dialogBinding: ProgressDialogBinding? = null
    private val paths = ArrayList<String>()
    private var dialog: AlertDialog? = null
    private var path: String? = null
    private lateinit var pickLauncher: ActivityResultLauncher<Intent>

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setupToolbar(getString(R.string.app_name))
        Database(this)
        updateRecents()
        pickLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == RESULT_OK) {
                    result.data?.data?.let { uri ->
                        contentResolver.openInputStream(uri)?.let { inputStream ->
                            val soFile = File(
                                Storage.getHomeDir(this),
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
                                    this@MainActivity,
                                    R.string.noFile,
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    }
                }
            }

        binding.openLib.setOnClickListener {
            pickLauncher.launch(FilePickHelper.pickFile(false))
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
        recentOpened.layoutManager = LinearLayoutManager(this@MainActivity)
        if (cursor.count == 0) {
            recentOpened.visibility = View.GONE
            welcomeLayout.visibility = View.VISIBLE
            recentOpened.adapter = adapter
        } else {
            welcomeLayout.visibility = View.INVISIBLE
            recentOpened.visibility = View.VISIBLE
            recentOpened.layoutManager = LinearLayoutManager(this)
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
            Dumper.readData(this@MainActivity)
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

    private fun dismissProgressDialog() {
        dialog?.dismiss().also {
            dialog = null
        }
        dialogBinding = null
    }

    private fun toClassesActivity() {
        startActivity(Intent(this@MainActivity, SymbolsActivity::class.java).apply {
            putExtras(Bundle().apply {
                putString("filePath", path)
            })
        })
        dismissProgressDialog()
    }

    override fun updateDialogProgress(last: Int, total: Int) {
        runOnUiThread {
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
    }
}
