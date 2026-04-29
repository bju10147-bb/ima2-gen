package com.ima2gen.app.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Collections
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.ima2gen.app.data.local.SecureKeyStore
import com.ima2gen.app.ui.auth.AuthScreen
import com.ima2gen.app.ui.auth.ApiKeyGuideScreen
import com.ima2gen.app.ui.gallery.GalleryScreen
import com.ima2gen.app.ui.generate.GenerateScreen
import com.ima2gen.app.ui.settings.SettingsScreen
import com.ima2gen.app.ui.theme.Ima2GenTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject lateinit var secureKeyStore: SecureKeyStore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Ima2GenTheme {
                Ima2GenApp(hasApiKey = secureKeyStore.hasApiKey())
            }
        }
    }
}

private data class BottomNavItem(
    val route: String,
    val label: String,
    val icon: ImageVector,
)

private val bottomNavItems = listOf(
    BottomNavItem(Screen.Generate.route, "생성", Icons.Filled.AutoAwesome),
    BottomNavItem(Screen.Gallery.route, "갤러리", Icons.Filled.Collections),
    BottomNavItem(Screen.Settings.route, "설정", Icons.Filled.Settings),
)

@Composable
fun Ima2GenApp(hasApiKey: Boolean) {
    val navController = rememberNavController()
    val startDestination = if (hasApiKey) Screen.Main.route else Screen.Auth.route

    NavHost(
        navController = navController,
        startDestination = startDestination,
    ) {
        // ── Auth Flow ──
        composable(Screen.Auth.route) {
            AuthScreen(
                onNavigateToGuide = { navController.navigate(Screen.ApiKeyGuide.route) },
                onAuthComplete = {
                    navController.navigate(Screen.Main.route) {
                        popUpTo(Screen.Auth.route) { inclusive = true }
                    }
                },
            )
        }
        composable(Screen.ApiKeyGuide.route) {
            ApiKeyGuideScreen(onBack = { navController.popBackStack() })
        }

        // ── Main App (with bottom nav) ──
        composable(Screen.Main.route) {
            MainScreen()
        }
    }
}

@Composable
fun MainScreen() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            NavigationBar {
                bottomNavItems.forEach { item ->
                    NavigationBarItem(
                        icon = { Icon(item.icon, contentDescription = item.label) },
                        label = { Text(item.label) },
                        selected = currentDestination?.hierarchy?.any {
                            it.route == item.route
                        } == true,
                        onClick = {
                            navController.navigate(item.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                    )
                }
            }
        },
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Generate.route,
            modifier = Modifier.padding(innerPadding),
        ) {
            composable(Screen.Generate.route) {
                GenerateScreen()
            }
            composable(Screen.Gallery.route) {
                GalleryScreen()
            }
            composable(Screen.Settings.route) {
                SettingsScreen(
                    onNavigateToAuth = {
                        navController.navigate(Screen.Auth.route) {
                            popUpTo(navController.graph.id) { inclusive = true }
                        }
                    }
                )
            }
        }
    }
}
