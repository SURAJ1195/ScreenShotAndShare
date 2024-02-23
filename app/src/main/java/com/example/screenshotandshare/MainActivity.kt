package com.example.screenshotandshare

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material3.FilledTonalButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.example.screenshotandshare.screenshot.ImageResult
import com.example.screenshotandshare.screenshot.ScreenShotBox

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyApp()
        }
    }


    @Composable
    fun MyApp() {
        var text by remember { mutableStateOf("Hello, World!") }
        val capture = remember { mutableStateOf(false) }
        val captureBitmap = remember { mutableStateOf<Bitmap?>(null) }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        )
        {
            ScreenShotBox(capture = capture, capturedBitmap = {
                captureBitmap.value = it
            }) {
                TextField(
                    value = text,
                    onValueChange = { newText -> text = newText },
                    label = { Text("Enter text") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp)
                )

                // Clickable composable to take a screenshot and share
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .clip(MaterialTheme.shapes.medium)
                        .background(MaterialTheme.colors.primary)
                        .clickable {
                            capture.value = true
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Take Screenshot and Share",
                        color = Color.White
                    )
                }
            }
        }
        if(capture.value){
            ImageAlertDialog(bitmap = captureBitmap.value) {
                capture.value = false
            }
        }

    }

// Function to share the captured screenshot

    private fun shareScreenshot(bitmap: Bitmap, context: Context) {
        val imageUri = context.contentResolver.insert(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            ContentValues()
        ) ?: return

        context.contentResolver.openOutputStream(imageUri)?.use { outputStream ->
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
        }

        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "image/jpeg"
            putExtra(Intent.EXTRA_STREAM, imageUri)
        }

        ContextCompat.startActivity(
            context,
            Intent.createChooser(shareIntent, "Share screenshot"),
            null
        )
    }
}


@Composable
private fun ImageAlertDialog(bitmap:Bitmap?, onDismiss: () -> Unit) {
    androidx.compose.material.AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            FilledTonalButton(onClick = { onDismiss() }) {
                Text(text = "Confirm")
            }
        },
        dismissButton = {
            FilledTonalButton(onClick = { onDismiss() }) {
                Text(text = "Dismiss")
            }
        },
        text = {

               if(bitmap!= null) {
                    Image(
                        bitmap = bitmap.asImageBitmap(),
                        contentDescription = null
                    )
                }else{
                   Text(text = "Error in capturing")
               }
        })
}
