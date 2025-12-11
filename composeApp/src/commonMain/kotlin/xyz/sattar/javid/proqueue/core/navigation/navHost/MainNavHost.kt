package xyz.sattar.javid.proqueue.core.navigation.navHost

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import xyz.sattar.javid.proqueue.core.navigation.AppScreens
import xyz.sattar.javid.proqueue.core.navigation.MainTab
import xyz.sattar.javid.proqueue.core.ui.components.BottomNavigationBar
import xyz.sattar.javid.proqueue.feature.createAppointment.CreateAppointmentScreen
import xyz.sattar.javid.proqueue.feature.createVisitor.CreateVisitorRoute
import xyz.sattar.javid.proqueue.feature.home.HomeScreen
import xyz.sattar.javid.proqueue.feature.lastVisitors.LastVisitorsScreen
import xyz.sattar.javid.proqueue.feature.visitorSelection.VisitorSelectionScreen
import xyz.sattar.javid.proqueue.feature.settings.SettingsScreen
import androidx.navigation.toRoute

@Composable
fun MainNavHost(
    onNavigateToCreateBusiness: () -> Unit = {},
    onNavigateToCreateVisitor: () -> Unit = {},
    onChangeBusiness: () -> Unit = {}
) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val screensWithBottomBar = setOf(
        AppScreens.Home::class.qualifiedName,
        AppScreens.Visitors::class.qualifiedName,
        AppScreens.Settings::class.qualifiedName
    )

    val shouldShowBottomBar = currentRoute in screensWithBottomBar

    var selectedTab by remember { mutableStateOf<MainTab>(MainTab.Home) }

    val tabs = listOf(
        MainTab.Home,
        MainTab.LastVisitors,
        MainTab.Settings
    )

    Scaffold(
        bottomBar = {
            AnimatedVisibility(
                visible = shouldShowBottomBar,
                enter = slideInVertically(
                    initialOffsetY = { it }
                ),
                exit = slideOutVertically(
                    targetOffsetY = { it }
                )
            ) {
                BottomNavigationBar(
                    tabs = tabs,
                    selectedTab = selectedTab,
                    onTabSelected = { tab ->
                        selectedTab = tab
                        when (tab) {
                            MainTab.Home -> navController.navigate(AppScreens.Home) {
                                popUpTo(AppScreens.Home) {
                                    inclusive = false
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                            MainTab.LastVisitors -> navController.navigate(AppScreens.Visitors) {
                                popUpTo(AppScreens.Home) {
                                    inclusive = false
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                            MainTab.Settings -> navController.navigate(AppScreens.Settings) {
                                popUpTo(AppScreens.Home) {
                                    inclusive = false
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    }
                )
            }
        }
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = AppScreens.Home,
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = if (shouldShowBottomBar) paddingValues.calculateBottomPadding() else paddingValues.calculateBottomPadding())
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
                    onNavigateToCreateAppointment = {
                        navController.navigate(AppScreens.VisitorSelection)
                    },
                    onNavigateToEditAppointment = { appointmentId ->
                        navController.navigate(AppScreens.CreateAppointment(appointmentId = appointmentId))
                    }
                )
            }

            composable<AppScreens.VisitorSelection> {
                VisitorSelectionScreen(
                    onNavigateToCreateAppointment = { visitorId ->
                        navController.navigate(AppScreens.CreateAppointment(visitorId))
                    },
                    onNavigateToCreateVisitor = {
                        navController.navigate(AppScreens.CreateVisitor)
                    },
                    onNavigateBack = {
                        navController.popBackStack()
                    }
                )
            }

            composable<AppScreens.Settings> {
                selectedTab = MainTab.Settings
                SettingsScreen(
                    onNavigateToAbout = {},
                    onChangeBusiness = onChangeBusiness
                )
            }

            composable<AppScreens.CreateVisitor> {
                CreateVisitorRoute(
                    onContinue = { visitorId ->
                        navController.navigate(AppScreens.CreateAppointment(visitorId))
                    },
                    onNavigateBack = {
                        navController.popBackStack()
                    }
                )
            }

            composable<AppScreens.CreateAppointment> { backStackEntry ->
                val args = backStackEntry.toRoute<AppScreens.CreateAppointment>()
                CreateAppointmentScreen(
                    visitorId = args.visitorId,
                    appointmentId = args.appointmentId,
                    onNavigateBack = {
                        navController.popBackStack()
                    },
                    onAppointmentCreated = {
                        navController.navigate(AppScreens.Visitors) {
                            popUpTo(AppScreens.Visitors) {
                                inclusive = true
                            }
                        }
                    }
                )
            }
        }
    }
}