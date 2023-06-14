package com.mcal.disassembler.activities

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.MenuItem
import android.view.View
import com.mcal.disassembler.R
import com.mcal.disassembler.databinding.ActivityDemanglerBinding
import com.mcal.disassembler.nativeapi.DisassemblerDumper

class DemanglerActivity : BaseActivity() {
    private val binding by lazy(LazyThreadSafetyMode.NONE) {
        ActivityDemanglerBinding.inflate(
            layoutInflater
        )
    }

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setupToolbar(binding.toolbar, getString(R.string.app_symbols))
        val searchText = binding.namedemangleractivityEditText1
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
            }
        })
        binding.demangle.setOnClickListener {
            val editText1 = binding.namedemangleractivityEditText1
            if (editText1.text.toString().isNotEmpty()) {
                binding.namedemangleractivityEditText2.setText(DisassemblerDumper.demangle(editText1.text.toString()))
            }
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