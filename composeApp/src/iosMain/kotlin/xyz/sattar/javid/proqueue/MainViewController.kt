package xyz.sattar.javid.proqueue

import androidx.compose.ui.window.ComposeUIViewController
import xyz.sattar.javid.proqueue.di.initKoin

fun MainViewController() = ComposeUIViewController {
    initKoin(null)
    App()
}