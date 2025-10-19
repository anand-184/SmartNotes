package com.anand.smartnotes.data.dataclasses

data class Note(
    val id: String = "",
    val title: String = "",
    val content: String = "",
    val timestamp: Long = System.currentTimeMillis()
)
