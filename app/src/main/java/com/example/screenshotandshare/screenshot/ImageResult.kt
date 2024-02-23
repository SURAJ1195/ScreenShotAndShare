package com.example.screenshotandshare.screenshot

import android.graphics.Bitmap

sealed class ImageResult {

    //every class and object extends the ImageResult class
    object Initial : ImageResult()
    data class Error(val exception: Exception) : ImageResult()
    data class Success(val bitmapData: Bitmap) :ImageResult()
}