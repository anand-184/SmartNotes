package com.anand.smartnotes.data.cloud

import android.content.Context
import com.cloudinary.android.MediaManager

object CloudinaryManager {
    fun init(context: Context) {
        val config = mapOf(
            "cloud_name" to "delrsmhgl",
            "api_key" to "839334239917742",
            "api_secret" to "jVxnUwe9ZZjW6xKiiiXWvU1BtXU"
        )
        MediaManager.init(context, config)
    }
}