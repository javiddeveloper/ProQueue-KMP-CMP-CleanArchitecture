package xyz.sattar.javid.proqueue

import androidx.compose.runtime.Composable
import org.jetbrains.compose.ui.tooling.preview.Preview
import xyz.sattar.javid.proqueue.feature.main.MainScreen
import xyz.sattar.javid.proqueue.ui.theme.AppTheme

@Composable
@Preview
fun App() {
    AppTheme {
        MainScreen(
            onNavigateToCreateBusiness = {

            },
            onNavigateToCreateVisitor = {

            }
        )
    }
}