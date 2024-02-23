package com.example.screenshotandshare.screenshot

import android.graphics.Bitmap
import android.os.Build
import android.view.View
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.layout.boundsInRoot
import androidx.compose.ui.layout.boundsInWindow
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalView


@Composable
fun ScreenShotBox(
    modifier: Modifier = Modifier,
    capture:MutableState<Boolean>,
    capturedBitmap:(Bitmap?)->Unit,
    content: @Composable () -> Unit
    ){
    val view: View = LocalView.current
    var composableBounds = remember {
        mutableStateOf<Rect?>(null)
    }
    DisposableEffect(key1 = capture.value ){

        if(capture.value){
            composableBounds.value?.let{ bounds ->
                if(bounds.width == 0f || bounds.height == 0f)return@let
                view.screenshot(bounds){ imageResult: ImageResult ->
                    if (imageResult is ImageResult.Success) {
                        capturedBitmap(imageResult.bitmapData)
                    }
                }
            }
        }

        onDispose {
            composableBounds.value?.let{ bounds ->
                if(bounds.width == 0f || bounds.height == 0f)return@let
                view.screenshot(bounds){ imageResult: ImageResult ->
                    if (imageResult is ImageResult.Success) {
                        if(!imageResult.bitmapData.isRecycled){
                          imageResult.bitmapData.recycle()
                        }
                    }
                }
            }
        }
    }
    Box(modifier = modifier.fillMaxSize()
        .onGloballyPositioned {
            composableBounds.value = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                it.boundsInWindow()
            } else {
                it.boundsInRoot()
            }
        }
    ) {
        content()
    }

}