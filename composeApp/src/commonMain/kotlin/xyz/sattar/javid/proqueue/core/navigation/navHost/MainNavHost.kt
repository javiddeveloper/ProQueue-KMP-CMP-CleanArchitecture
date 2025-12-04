package xyz.sattar.javid.proqueue.core.navigation.navHost

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import xyz.sattar.javid.proqueue.core.navigation.AppScreens
import xyz.sattar.javid.proqueue.core.navigation.MainTab
import xyz.sattar.javid.proqueue.core.ui.components.BottomNavigationBar
import xyz.sattar.javid.proqueue.feature.home.HomeScreen
import xyz.sattar.javid.proqueue.feature.lastVisitors.LastVisitorsScreen
import xyz.sattar.javid.proqueue.feature.settings.SettingsScreen

@Composable
fun MainNavHost(
    onNavigateToCreateBusiness: () -> Unit = {},
    onNavigateToCreateVisitor: () -> Unit = {},
    onChangeBusiness: () -> Unit = {}
) {
    val navController = rememberNavController()
    var selectedTab by remember { mutableStateOf<MainTab>(MainTab.Home) }

    val tabs = listOf(
        MainTab.Home,
        MainTab.LastVisitors,
        MainTab.Settings
    )

    Scaffold(
        bottomBar = {
            BottomNavigationBar(
                tabs = tabs,
                selectedTab = selectedTab,
                onTabSelected = { tab ->
                    selectedTab = tab
                    when (tab) {
                        MainTab.Home -> navController.navigate(AppScreens.Home) {
                            popUpTo(AppScreens.Home) { 
                                inclusive = true 
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                        MainTab.LastVisitors -> navController.navigate(AppScreens.Visitors) {
                            popUpTo(AppScreens.Home) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                        MainTab.Settings -> navController.navigate(AppScreens.Settings) {
                            popUpTo(AppScreens.Home) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                }
            )
        }
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = AppScreens.Home,
            modifier = Modifier.padding(paddingValues)
        ) {
            composable<AppScreens.Home> {
                selectedTab = MainTab.Home
                HomeScreen(
                    onNavigateToCreateBusiness = onNavigateToCreateBusiness,
                    onNavigateToCreateVisitor = onNavigateToCreateVisitor
                )
            }

            composable<AppScreens.Visitors> {
                selectedTab = MainTab.LastVisitors
                LastVisitorsScreen(
                    onNavigateToVisitorDetail = { visitorId ->

                    }
                )
            }

            composable<AppScreens.Settings> {
                selectedTab = MainTab.Settings
                SettingsScreen(
                    onNavigateToAbout = {

                    },
                    onChangeBusiness = onChangeBusiness
                )
            }
        }
    }
}
