package com.mcal.disassembler.interfaces

interface SearchResultListener {
    fun onFoundApp(list: MutableList<Map<String, Any>>, mode: Boolean)
}