package com.motisoft.herd.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.motisoft.herd.ui.screens.addcow.AddCowScreen
import com.motisoft.herd.ui.screens.breeding.BreedingInfoScreen
import com.motisoft.herd.ui.screens.cowdetail.CowDetailScreen
import com.motisoft.herd.ui.screens.cowinfo.CowInformationScreen
import com.motisoft.herd.ui.screens.dailylog.DailyInformationScreen
import com.motisoft.herd.ui.screens.health.HealthUpdateScreen
import com.motisoft.herd.ui.screens.home.HomeScreen
import com.motisoft.herd.ui.screens.milk.MilkRecordScreen
import com.motisoft.herd.ui.screens.scan.ScanTagScreen
import com.motisoft.herd.ui.screens.signin.SignInScreen
import com.motisoft.herd.ui.screens.splash.SplashScreen
import com.motisoft.herd.ui.screens.dashboard.DashboardScreen

@Composable
fun HerdNavGraph(navController: NavHostController = rememberNavController()) {
    NavHost(navController = navController, startDestination = NavRoutes.Splash.route) {
        composable(NavRoutes.Splash.route) {
            SplashScreen(
                onSignedIn = {
                    navController.navigate(NavRoutes.Home.route) {
                        popUpTo(NavRoutes.Splash.route) { inclusive = true }
                    }
                },
                onSignedOut = {
                    navController.navigate(NavRoutes.SignIn.route) {
                        popUpTo(NavRoutes.Splash.route) { inclusive = true }
                    }
                },
            )
        }

        composable(NavRoutes.SignIn.route) {
            SignInScreen(
                onSignInSuccess = {
                    navController.navigate(NavRoutes.Home.route) {
                        popUpTo(NavRoutes.SignIn.route) { inclusive = true }
                    }
                },
            )
        }

        composable(NavRoutes.Home.route) {
            HomeScreen(
                onAddCow = { navController.navigate(NavRoutes.AddCow.route) },
                onScan = { navController.navigate(NavRoutes.Scan.route) },
                onCowClick = { tag -> navController.navigate(NavRoutes.CowDetail.build(tag)) },
                onOpenDashboard = { navController.navigate(NavRoutes.Dashboard.route) },
            )
        }

        composable(NavRoutes.Dashboard.route) {
            DashboardScreen(
                onBack = { navController.popBackStack() },
                onSignedOut = {
                    navController.navigate(NavRoutes.SignIn.route) {
                        popUpTo(0) { inclusive = true }
                    }
                },
            )
        }

        composable(NavRoutes.Scan.route) {
            ScanTagScreen(onClose = { navController.popBackStack() })
        }

        composable(NavRoutes.AddCow.route) {
            AddCowScreen(
                onDone = { navController.popBackStack() },
                onBack = { navController.popBackStack() },
            )
        }

        composable(NavRoutes.CowDetail.route) { backStackEntry ->
            val tag = backStackEntry.arguments?.getString(NavRoutes.ARG_TAG).orEmpty()
            CowDetailScreen(
                tag = tag,
                onBack = { navController.popBackStack() },
                onOpenCowInformation = { navController.navigate(NavRoutes.CowInformation.build(tag)) },
                onOpenBreeding = { navController.navigate(NavRoutes.Breeding.build(tag)) },
                onOpenMilk = { navController.navigate(NavRoutes.Milk.build(tag)) },
                onOpenHealth = { navController.navigate(NavRoutes.Health.build(tag)) },
                onOpenDailyLog = { navController.navigate(NavRoutes.DailyLog.build(tag)) },
            )
        }

        // The 5 section screens are only ever pushed from Cow Detail, so a plain
        // popBackStack() here always lands back on Cow Detail, never Home - this is
        // the "preserve back to Cow Detail" rule from the design spec, achieved for
        // free by not providing any shortcut that skips Cow Detail.
        composable(NavRoutes.CowInformation.route) { backStackEntry ->
            val tag = backStackEntry.arguments?.getString(NavRoutes.ARG_TAG).orEmpty()
            CowInformationScreen(tag = tag, onBack = { navController.popBackStack() })
        }

        composable(NavRoutes.Breeding.route) { backStackEntry ->
            val tag = backStackEntry.arguments?.getString(NavRoutes.ARG_TAG).orEmpty()
            BreedingInfoScreen(tag = tag, onBack = { navController.popBackStack() })
        }

        composable(NavRoutes.Milk.route) { backStackEntry ->
            val tag = backStackEntry.arguments?.getString(NavRoutes.ARG_TAG).orEmpty()
            MilkRecordScreen(tag = tag, onBack = { navController.popBackStack() })
        }

        composable(NavRoutes.Health.route) { backStackEntry ->
            val tag = backStackEntry.arguments?.getString(NavRoutes.ARG_TAG).orEmpty()
            HealthUpdateScreen(tag = tag, onBack = { navController.popBackStack() })
        }

        composable(NavRoutes.DailyLog.route) { backStackEntry ->
            val tag = backStackEntry.arguments?.getString(NavRoutes.ARG_TAG).orEmpty()
            DailyInformationScreen(tag = tag, onBack = { navController.popBackStack() })
        }
    }
}
