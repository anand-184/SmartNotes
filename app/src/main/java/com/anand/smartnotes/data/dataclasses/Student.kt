package com.anand.smartnotes.data.dataclasses

data class Student(
    val id: String = "",
    val userId: String = "",
    val university: String = "",
    val program: String = "",
    val semester: String = "",
    val syllabusKey: String = "", // "PTU_BTech_CSE_Sem7"
    val extractedText: String = "",
    val summary: String = "",
    val questions: List<QuestionAnswer> = emptyList(),
    val topic: String = "",
    val syllabusChapter: String = "",
    val timestamp: Long = System.currentTimeMillis()
)
