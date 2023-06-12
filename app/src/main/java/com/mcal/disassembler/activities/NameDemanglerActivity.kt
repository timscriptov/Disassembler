package com.mcal.disassembler.activities

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import com.mcal.disassembler.R
import com.mcal.disassembler.databinding.ActivityDemanglerBinding
import com.mcal.disassembler.nativeapi.DisassemblerDumper

class NameDemanglerActivity : BaseActivity() {
    private val binding by lazy(LazyThreadSafetyMode.NONE) {
        ActivityDemanglerBinding.inflate(
            layoutInflater
        )
    }

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setupToolbar(binding.toolbar, getString(R.string.app_symbols))
    }

    fun demangle(view: View?) {
        val editText1 = binding.namedemangleractivityEditText1
        if (editText1.text.toString().isNotEmpty()) {
            binding.namedemangleractivityEditText2.setText(DisassemblerDumper.demangle(editText1.text.toString()))
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