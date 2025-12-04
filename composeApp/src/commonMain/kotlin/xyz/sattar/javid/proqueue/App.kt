package xyz.sattar.javid.proqueue

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import org.jetbrains.compose.ui.tooling.preview.Preview
import xyz.sattar.javid.proqueue.core.state.BusinessStateHolder
import xyz.sattar.javid.proqueue.core.navigation.navHost.BusinessNavHost
import xyz.sattar.javid.proqueue.core.navigation.navHost.MainNavHost
import xyz.sattar.javid.proqueue.ui.theme.AppTheme

@Composable
@Preview
fun App() {
    AppTheme {
        val selectedBusiness by BusinessStateHolder.selectedBusiness.collectAsState()

        if (selectedBusiness == null) {
            BusinessNavHost(
                onBusinessSelected = { business ->
                    BusinessStateHolder.selectBusiness(business)
                }
            )
        } else {
            MainNavHost(
                onNavigateToCreateBusiness = {
                    // Should not be reachable or should navigate to business list?
                    // For now, let's just clear business to go back to list
                    BusinessStateHolder.clearBusiness()
                },
                onNavigateToCreateVisitor = {
                    // TODO: Implement navigation to CreateVisitor
                },
                onChangeBusiness = {
                    BusinessStateHolder.clearBusiness()
                }
            )
        }
    }
}