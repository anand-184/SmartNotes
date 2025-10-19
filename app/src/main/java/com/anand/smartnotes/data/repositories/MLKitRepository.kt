package com.anand.smartnotes.data.repositories

import android.content.Context
import android.net.Uri
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import kotlinx.coroutines.tasks.await
import kotlin.coroutines.CoroutineContext

class MLKitRepository(var context: Context){
    var textRecognizer=TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)

    suspend fun extractTextfromImage(imageUri:Uri):Result<String>{
        return try {
            val imageInput=InputImage.fromFilePath(context,imageUri)
            val result=textRecognizer.process(imageInput).await()
            val extractedText=result.text

            if (extractedText.isEmpty()) {
                Result.failure(Exception("No text found in image"))
            } else {
                Result.success(extractedText)
            }

        }catch (e:Exception){
            Result.failure(e)
        }
    }
    suspend fun ImageUploaded(imageUri: Uri){


    }
}