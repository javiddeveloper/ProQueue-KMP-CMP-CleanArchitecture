package xyz.sattar.javid.proqueue.core.navigation.navHost

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import xyz.sattar.javid.proqueue.core.navigation.AppScreens
import xyz.sattar.javid.proqueue.domain.model.Business
import xyz.sattar.javid.proqueue.feature.businessList.BusinessListScreen
import xyz.sattar.javid.proqueue.feature.createBusiness.CreateBusinessRoute

@Composable
fun BusinessNavHost(
    onBusinessSelected: (Business) -> Unit
) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = AppScreens.BusinessList
    ) {
        composable<AppScreens.BusinessList> {
            BusinessListScreen(
                onNavigateToMain = { business ->
                    onBusinessSelected(business)
                },
                onNavigateToCreateBusiness = {
                    navController.navigate(AppScreens.CreateBusiness)
                }
            )
        }

        composable<AppScreens.CreateBusiness> {
            CreateBusinessRoute(
                onNavigateBack = {
                    navController.popBackStack()
                },
                onContinue = {
                    navController.popBackStack()
                }
            )
        }
    }
}
