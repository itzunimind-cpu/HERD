package com.motisoft.herd.ui.navigation

sealed class NavRoutes(val route: String) {
    data object Splash : NavRoutes("splash")
    data object SignIn : NavRoutes("sign_in")
    data object Home : NavRoutes("home")
    data object Dashboard : NavRoutes("dashboard")
    data object Scan : NavRoutes("scan")
    data object AddCow : NavRoutes("add_cow")

    data object CowDetail : NavRoutes("cow_detail/{tag}") {
        fun build(tag: String) = "cow_detail/$tag"
    }
    data object CowInformation : NavRoutes("cow_information/{tag}") {
        fun build(tag: String) = "cow_information/$tag"
    }
    data object Breeding : NavRoutes("breeding/{tag}") {
        fun build(tag: String) = "breeding/$tag"
    }
    data object Milk : NavRoutes("milk/{tag}") {
        fun build(tag: String) = "milk/$tag"
    }
    data object Health : NavRoutes("health/{tag}") {
        fun build(tag: String) = "health/$tag"
    }
    data object DailyLog : NavRoutes("daily_log/{tag}") {
        fun build(tag: String) = "daily_log/$tag"
    }

    companion object {
        const val ARG_TAG = "tag"
    }
}
