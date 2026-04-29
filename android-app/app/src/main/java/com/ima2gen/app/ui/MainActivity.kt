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
import androidx.compose.material3.*
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
import com.ima2gen.app.ui.project.ProjectListScreen
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

@Composable
fun Ima2GenApp(hasApiKey: Boolean) {
    val navController = rememberNavController()
    val startDestination = if (hasApiKey) Screen.ProjectList.route else Screen.Auth.route

    NavHost(
        navController = navController,
        startDestination = startDestination,
    ) {
        composable(Screen.Auth.route) {
            AuthScreen(
                onNavigateToGuide = { navController.navigate("api_key_guide") },
                onAuthComplete = {
                    navController.navigate(Screen.ProjectList.route) {
                        popUpTo(Screen.Auth.route) { inclusive = true }
                    }
                },
            )
        }
        composable("api_key_guide") {
            ApiKeyGuideScreen(onBack = { navController.popBackStack() })
        }
        composable(Screen.ProjectList.route) {
            ProjectListScreen(
                onProjectSelected = { projectId ->
                    navController.navigate("project_main/$projectId")
                }
            )
        }
        composable("project_main/{projectId}") { backStackEntry ->
            val projectId = backStackEntry.arguments?.getString("projectId") ?: ""
            MainProjectScreen(
                projectId = projectId,
                onLogout = {
                    navController.navigate(Screen.Auth.route) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }
    }
}

private data class BottomNavItem(
    val route: String,
    val label: String,
    val icon: ImageVector,
)

@Composable
fun MainProjectScreen(projectId: String, onLogout: () -> Unit) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    val bottomNavItems = listOf(
        BottomNavItem(Screen.Generate.createRoute(projectId), "생성", Icons.Filled.AutoAwesome),
        BottomNavItem(Screen.Gallery.createRoute(projectId), "갤러리", Icons.Filled.Collections),
        BottomNavItem(Screen.Settings.createRoute(projectId), "설정", Icons.Filled.Settings),
    )

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            NavigationBar {
                bottomNavItems.forEach { item ->
                    NavigationBarItem(
                        icon = { Icon(item.icon, contentDescription = item.label) },
                        label = { Text(item.label) },
                        selected = currentDestination?.hierarchy?.any { it.route == item.route } == true,
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
            startDestination = Screen.Generate.createRoute(projectId),
            modifier = Modifier.padding(innerPadding),
        ) {
            composable(Screen.Generate.route) {
                GenerateScreen(onBack = onLogout)
            }
            composable(Screen.Gallery.route) {
                GalleryScreen(onBack = onLogout)
            }
            composable(Screen.Settings.route) {
                SettingsScreen(
                    onNavigateToAuth = onLogout,
                    onBack = onLogout
                )
            }
        }
    }
}
