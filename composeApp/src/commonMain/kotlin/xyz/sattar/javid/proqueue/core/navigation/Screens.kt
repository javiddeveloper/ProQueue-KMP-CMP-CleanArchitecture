package xyz.sattar.javid.proqueue.core.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.People
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.ui.graphics.vector.ImageVector
import kotlinx.serialization.Serializable
import org.jetbrains.compose.resources.StringResource
import proqueue.composeapp.generated.resources.Res
import proqueue.composeapp.generated.resources.home_menu_item
import proqueue.composeapp.generated.resources.last_visitors_menu_item
import proqueue.composeapp.generated.resources.settings_menu_item

object AppNavHost {
    @Serializable
    data object MessageNavHost

    @Serializable
    data object MainNavHost

    @Serializable
    data object BusinessNavHost
}

sealed class AppScreens {
    @Serializable
    object OnBoarding : AppScreens()
    @Serializable
    object Home : AppScreens()
    @Serializable
    object Settings : AppScreens()
    @Serializable
    object Visitors : AppScreens()
    @Serializable
    object CreateBusiness : AppScreens()
    @Serializable
    object BusinessList : AppScreens()
    @Serializable
    object VisitorSelection : AppScreens()
    @Serializable
    object CreateVisitor : AppScreens()
    @Serializable
    data class EditVisitor(val visitorId: Long) : AppScreens()
    @Serializable
    data class CreateAppointment(val visitorId: Long? = null, val appointmentId: Long? = null) : AppScreens()
}


sealed interface MainTab {
    val title: StringResource
    val iconSelected: ImageVector
    val iconUnSelected: ImageVector

    @Serializable
    data object Home : MainTab {
        override val title = Res.string.home_menu_item
        override val iconSelected = Icons.Filled.Home
        override val iconUnSelected = Icons.Outlined.Home
    }

    @Serializable
    data object LastVisitors : MainTab {
        override val title = Res.string.last_visitors_menu_item
        override val iconSelected = Icons.Filled.People
        override val iconUnSelected = Icons.Outlined.People
    }

    @Serializable
    data object Settings : MainTab {
        override val title = Res.string.settings_menu_item
        override val iconSelected = Icons.Filled.Settings
        override val iconUnSelected = Icons.Outlined.Settings
    }
}
