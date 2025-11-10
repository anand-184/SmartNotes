package com.anand.smartnotes.data.dataclasses

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
@Parcelize
data class Note(
    val id: String = "",
    val userId: String = "",
    val userName: String = "",
    val university: String = "",
    val program: String = "",
    val semester: String = "",
    val batch: String = "", // "PTU_BTech_CSE_Sem7"


    // Image & Text
    val imageUrl: String = "",
    val extractedText: String = "",

    // AI Generated Content
    val summary: List<String> = emptyList(),
    val questions: List<QuestionAnswer> = emptyList(),
    val answers: List<String> = emptyList(),
    val topic: String = "",
    val syllabusChapter: String = "",

    val timestamp: Long = System.currentTimeMillis()
): Parcelable
