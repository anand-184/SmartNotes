package com.anand.smartnotes.data.dataclasses

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
@Parcelize
data class QuestionAnswer(
    val question: String = "",
    val answer: String = "",
):Parcelable
