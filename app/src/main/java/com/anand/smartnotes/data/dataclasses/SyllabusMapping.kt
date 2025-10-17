package com.anand.smartnotes.data.dataclasses

data class SyllabusMapping(
    val id: String = "",
    val university: String = "",
    val universityName: String = "",
    val program: String = "",
    val semester: String = "",
    val syllabusUrl: String = "",
    val topics: List<String> = emptyList()
)
