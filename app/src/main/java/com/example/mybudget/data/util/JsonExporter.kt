package com.example.mybudget.data.util

import android.content.ContentValues
import android.content.Context
import android.os.Build
import android.provider.MediaStore
import androidx.annotation.RequiresApi
import com.example.mybudget.data.model.Budget
import com.example.mybudget.data.util.JsonHelper.serializeToJson
import java.io.IOException

object JsonExporter {

    @RequiresApi(Build.VERSION_CODES.Q)
    fun exportBudgetToDownloads(context: Context, budget: Budget): String {
        val jsonString = serializeToJson(budget)

        val fileName = "mybudget_export_${System.currentTimeMillis()}.json"
        val mimeType = "application/json"

        val contentValues = ContentValues().apply {
            put(MediaStore.Downloads.DISPLAY_NAME, fileName)
            put(MediaStore.Downloads.MIME_TYPE, mimeType)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                put(MediaStore.Downloads.IS_PENDING, 1)
            }
        }

        val contentResolver = context.contentResolver
        val uri = contentResolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues)
            ?: throw IOException("Failed to create file in Downloads")

        contentResolver.openOutputStream(uri)?.use { outputStream ->
            outputStream.write(jsonString.toByteArray())
        } ?: throw IOException("Failed to open output stream")

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            contentValues.clear()
            contentValues.put(MediaStore.Downloads.IS_PENDING, 0)
            contentResolver.update(uri, contentValues, null, null)
        }
        return fileName
    }
}
