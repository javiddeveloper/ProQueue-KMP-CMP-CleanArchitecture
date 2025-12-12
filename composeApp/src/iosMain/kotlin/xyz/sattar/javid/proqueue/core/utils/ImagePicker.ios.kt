package xyz.sattar.javid.proqueue.core.utils

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember

actual class ImagePickerLauncher(
    private val onLaunch: () -> Unit
) {
    actual fun launch() {
        // No-op for iOS
    }
}

@Composable
actual fun rememberImagePicker(onResult: (ByteArray?) -> Unit): ImagePickerLauncher {
    return remember {
        ImagePickerLauncher {
            // No-op for iOS
        }
    }
}
