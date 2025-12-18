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
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
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
import xyz.sattar.javid.proqueue.feature.notifications.NotificationsScreen
import androidx.navigation.toRoute

import xyz.sattar.javid.proqueue.feature.visitorDetails.VisitorDetailsScreen

@Composable
fun MainNavHost(
    onNavigateToCreateBusiness: () -> Unit = {},
    onNavigateToCreateVisitor: () -> Unit = {},
    onChangeBusiness: () -> Unit = {}
) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    val tabs = listOf(
        MainTab.Home,
        MainTab.LastVisitors,
        MainTab.Settings
    )

    // Determine if the bottom bar should be shown
    val shouldShowBottomBar = tabs.any { tab ->
        currentDestination?.hierarchy?.any {
            it.route == tab.route::class.qualifiedName
        } == true
    }

    val selectedTab = tabs.find { tab ->
        currentDestination?.hierarchy?.any {
            it.route == tab.route::class.qualifiedName
        } == true
    } ?: MainTab.Home

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
                        if (selectedTab != tab) {
                            navController.navigate(tab.route) {
                                // Pop up to the start destination of the graph to
                                // avoid building up a large stack of destinations
                                // on the back stack as users select items
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                // Avoid multiple copies of the same destination when
                                // reselecting the same item
                                launchSingleTop = true
                                // Restore state when reselecting a previously selected item
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
                HomeScreen()
            }

            composable<AppScreens.Visitors> {
                LastVisitorsScreen(
                    onNavigateToCreateAppointment = {
                        navController.navigate(AppScreens.VisitorSelection)
                    },
                    onNavigateToEditAppointment = { appointmentId ->
                        navController.navigate(AppScreens.CreateAppointment(appointmentId = appointmentId))
                    },
                    onNavigateToVisitorDetails = { visitorId ->
                        navController.navigate(AppScreens.VisitorDetails(visitorId))
                    }
                )
            }

            composable<AppScreens.VisitorSelection> {
                VisitorSelectionScreen(
                    onNavigateToCreateAppointment = { visitorId ->
                        navController.navigate(AppScreens.CreateAppointment(visitorId = visitorId))
                    },
                    onNavigateToEditVisitor = { visitorId ->
                        navController.navigate(AppScreens.EditVisitor(visitorId))
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
                SettingsScreen(
                    onNavigateToAbout = {},
                    onChangeBusiness = onChangeBusiness,
                    onNavigateToNotifications = {
                        navController.navigate(AppScreens.Notifications)
                    }
                )
            }

            composable<AppScreens.Notifications> {
                NotificationsScreen(
                    onNavigateBack = {
                        navController.popBackStack()
                    }
                )
            }

            composable<AppScreens.VisitorDetails> { backStackEntry ->
                val args = backStackEntry.toRoute<AppScreens.VisitorDetails>()
                VisitorDetailsScreen(
                    visitorId = args.visitorId,
                    onNavigateBack = {
                        navController.popBackStack()
                    },
                    onNavigateToCreateAppointment = { visitorId ->
                        navController.navigate(AppScreens.CreateAppointment(visitorId))
                    }
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

            composable<AppScreens.EditVisitor> { backStackEntry ->
                val args = backStackEntry.toRoute<AppScreens.EditVisitor>()
                CreateVisitorRoute(
                    visitorId = args.visitorId,
                    onContinue = { visitorId ->
                        navController.popBackStack() // Return after edit
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
