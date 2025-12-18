package xyz.sattar.javid.proqueue

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import org.jetbrains.compose.ui.tooling.preview.Preview
import xyz.sattar.javid.proqueue.core.state.BusinessStateHolder
import xyz.sattar.javid.proqueue.core.navigation.navHost.BusinessNavHost
import xyz.sattar.javid.proqueue.core.navigation.navHost.MainNavHost
import xyz.sattar.javid.proqueue.ui.theme.AppTheme

import xyz.sattar.javid.proqueue.core.state.ThemeStateHolder
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import xyz.sattar.javid.proqueue.core.prefs.PreferencesManager
import org.koin.compose.koinInject
import xyz.sattar.javid.proqueue.domain.BusinessRepository

@Composable
@Preview
fun App() {
    val themeMode by ThemeStateHolder.themeMode.collectAsState()
    LaunchedEffect(Unit) {
        PreferencesManager.themeMode.collect { ThemeStateHolder.setThemeMode(it) }
    }
    val scope = rememberCoroutineScope()
    val businessRepository: BusinessRepository = koinInject()

    AppTheme(themeMode = themeMode) {
        val selectedBusiness by BusinessStateHolder.selectedBusiness.collectAsState()

        LaunchedEffect(Unit) {
            PreferencesManager.defaultBusinessId.collect { id ->
                if (id != null && BusinessStateHolder.selectedBusiness.value == null) {
                    val business = businessRepository.getBusinessById(id)
                    if (business != null) {
                        BusinessStateHolder.selectBusiness(business)
                    }
                }
            }
        }

        if (selectedBusiness == null) {
            BusinessNavHost(
                onBusinessSelected = { business ->
                    BusinessStateHolder.selectBusiness(business)
                    scope.launch { PreferencesManager.setDefaultBusinessId(business.id) }
                }
            )
        } else {
            MainNavHost(
                onNavigateToCreateBusiness = {
                    // Should not be reachable or should navigate to business list?
                    // For now, let's just clear business to go back to list
                    BusinessStateHolder.clearBusiness()
                    scope.launch { PreferencesManager.setDefaultBusinessId(null) }
                },
                onNavigateToCreateVisitor = {
                    // TODO: Implement navigation to CreateVisitor
                },
                onChangeBusiness = {
                    BusinessStateHolder.clearBusiness()
                    scope.launch { PreferencesManager.setDefaultBusinessId(null) }
                }
            )
        }
    }
}
