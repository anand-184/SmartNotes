package com.anand.smartnotes.data.dataclasses

data class SyllabusMapping(
    val id: String = "",
    val university: String = "",
    val program: String = "",
    val semester: String = "",
    val batch: String = "",
    val syllabusUrl: String = "",
    val topics: List<String> = emptyList()
)
