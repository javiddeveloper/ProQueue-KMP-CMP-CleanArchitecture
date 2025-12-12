package xyz.sattar.javid.proqueue.core.utils

import androidx.compose.runtime.Composable

expect class ImagePickerLauncher {
    fun launch()
}

@Composable
expect fun rememberImagePicker(onResult: (ByteArray?) -> Unit): ImagePickerLauncher
