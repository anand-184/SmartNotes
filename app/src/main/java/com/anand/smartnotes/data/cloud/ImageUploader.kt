package com.anand.smartnotes.data.cloud

import android.net.Uri
import com.cloudinary.android.MediaManager
import com.cloudinary.android.callback.ErrorInfo
import com.cloudinary.android.callback.UploadCallback

object ImageUploader {

    fun uploadImage(
        imageUri: Uri,
        onSuccess: (url: String) -> Unit,
        onError: (throwable: Throwable) -> Unit
    ) {
        MediaManager.get().upload(imageUri)
            .option("resource_type", "image")
            .callback(object : UploadCallback {
                override fun onStart(requestId: String?) {}
                override fun onProgress(requestId: String?, bytes: Long, totalBytes: Long) {}
                override fun onSuccess(requestId: String?, resultData: Map<*, *>) {
                    val url = resultData["secure_url"] as? String
                    url?.let { onSuccess(it) }

                }
                override fun onError(requestId: String?, error: ErrorInfo?) {
                    onError(Throwable(error?.description ?: "Unknown Upload Error"))
                }
                override fun onReschedule(requestId: String?, error: ErrorInfo?) {}
            })
            .dispatch()
    }
}