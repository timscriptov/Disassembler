package com.mcal.disassembler.activities

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.mcal.disassembler.R
import com.mcal.disassembler.adapters.ListAdapter
import com.mcal.disassembler.data.Database
import com.mcal.disassembler.data.RecentsManager
import com.mcal.disassembler.data.Storage
import com.mcal.disassembler.databinding.MainActivityBinding
import com.mcal.disassembler.interfaces.MainView
import com.mcal.disassembler.nativeapi.DisassemblerDumper
import com.mcal.disassembler.nativeapi.Dumper
import com.mcal.disassembler.utils.FileHelper
import com.mcal.disassembler.utils.FilePickHelper
import com.mcal.disassembler.view.CenteredToolBar
import java.io.File
import java.io.FileOutputStream

class MainActivity : AppCompatActivity(), MainView {
    private val binding by lazy(LazyThreadSafetyMode.NONE) {
        MainActivityBinding.inflate(
            layoutInflater
        )
    }
    private val paths = ArrayList<String>()
    var dialog: ProgressDialog? = null

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
                        val projectsDir = Storage.getHomeDir(this)
                        val soFile = File(projectsDir, FilePickHelper.getFileName(this, uri))

                        contentResolver.openInputStream(uri)?.let { inputStream ->
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


        findViewById<ExtendedFloatingActionButton>(R.id.open_lib).setOnClickListener {
            pickLauncher.launch(FilePickHelper.pickFile(false))
        }
    }

    private fun setupToolbar(title: String) {
        setSupportActionBar(findViewById<CenteredToolBar>(R.id.toolbar))
        supportActionBar?.apply {
            this.title = title
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
    fun updateRecents() {
        paths.clear()
        val cursor = RecentsManager.getRecents()
        val welcomeLayout = binding.welcomeLayout
        val recentOpened = binding.items
        recentOpened.layoutManager = LinearLayoutManager(this@MainActivity)
        if (cursor.count == 0) {
            recentOpened.visibility = View.GONE
            welcomeLayout.visibility = View.VISIBLE
            recentOpened.adapter = ListAdapter(paths, this)
        } else {
            welcomeLayout.visibility = View.INVISIBLE
            recentOpened.visibility = View.VISIBLE
            recentOpened.layoutManager = LinearLayoutManager(this)
            if (cursor.count > 0) {
                while (cursor.moveToNext()) {
                    paths.add(cursor.getString(0))
                }
            }
            recentOpened.adapter = ListAdapter(paths, this)
        }
        if (cursor.count > 0) {
            while (cursor.moveToNext()) {
                paths.add(cursor.getString(0))
            }
        }
        recentOpened.adapter?.notifyDataSetChanged()
    }

    override fun loadSo(path: String) {
        showProgressDialog()
        this.path = path
        object : Thread() {
            override fun run() {
                DisassemblerDumper.load(path)
                Dumper.readData()
                toClassesActivity()
            }
        }.start()
    }

    private fun showProgressDialog() {
        dialog = ProgressDialog(this@MainActivity).also {
            it.setTitle(getString(R.string.loading))
            it.show()
        }
    }

    private fun dismissProgressDialog() {
        dialog?.dismiss().also {
            dialog = null
        }
    }

    fun toClassesActivity() {
        startActivity(Intent(this@MainActivity, SymbolsActivity::class.java).apply {
            putExtras(Bundle().apply {
                putString("filePath", path)
            })
        })
        dismissProgressDialog()
    }
}