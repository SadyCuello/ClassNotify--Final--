package com.example.classnotify.ui_presentation.ui.view

import android.graphics.BitmapFactory
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import android.util.Base64


fun decodeBase64ToByteArray(base64String: String): ByteArray {
    return Base64.decode(base64String, Base64.DEFAULT)
}

fun byteArrayToImageBitmap(byteArray: ByteArray): ImageBitmap? {
    return try {
        val bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
        bitmap?.asImageBitmap()
    } catch (e: Exception) {
        println("Error convirtiendo byte array to bitmap: ${e.message}")
        null
    }
}