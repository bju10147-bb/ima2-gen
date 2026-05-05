package com.ima2gen.app.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.core.os.LocaleListCompat
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.ima2gen.app.data.local.SecureKeyStore
import com.ima2gen.app.data.repository.AppLanguage
import com.ima2gen.app.data.repository.AppTheme
import com.ima2gen.app.data.repository.SettingsRepository
import com.ima2gen.app.ui.auth.*
import com.ima2gen.app.ui.gallery.GalleryScreen
import com.ima2gen.app.ui.generate.GenerateScreen
import com.ima2gen.app.ui.generate.GenerateViewModel
import com.ima2gen.app.ui.project.ProjectListScreen
import com.ima2gen.app.ui.settings.SettingsScreen
import com.ima2gen.app.ui.theme.*
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject lateinit var secureKeyStore: SecureKeyStore
    @Inject lateinit var settingsRepository: SettingsRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val theme by settingsRepository.theme.collectAsState(initial = AppTheme.SYSTEM)
            val language by settingsRepository.language.collectAsState(initial = AppLanguage.SYSTEM)

            LaunchedEffect(language) {
                val localeCode = when (language) {
                    AppLanguage.KO -> "ko"
                    AppLanguage.EN -> "en"
                    AppLanguage.JA -> "ja"
                    AppLanguage.ZH -> "zh"
                    AppLanguage.SYSTEM -> java.util.Locale.getDefault().language
                }
                val appLocale = LocaleListCompat.forLanguageTags(localeCode)
                AppCompatDelegate.setApplicationLocales(appLocale)
            }

            val darkTheme = when (theme) {
                AppTheme.DARK -> true
                AppTheme.LIGHT -> false
                AppTheme.SYSTEM -> isSystemInDarkTheme()
            }

            Ima2GenTheme(darkTheme = darkTheme) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Ima2GenApp(hasApiKey = secureKeyStore.hasApiKey())
                }
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
                onAuthComplete = { navController.navigate(Screen.ProjectList.route) },
                onNavigateToGuide = { navController.navigate("api_key_guide") }
            )
        }
        composable("api_key_guide") {
            ApiKeyGuideScreen(onBack = { navController.popBackStack() })
        }
        composable(Screen.ProjectList.route) {
            ProjectListScreen(
                onProjectSelected = { id -> navController.navigate("generate/$id") },
                onSettingsClick = { navController.navigate(Screen.Settings.route) }
            )
        }
        composable(Screen.Settings.route) {
            SettingsScreen(
                onBack = { navController.popBackStack() },
                onLogout = {
                    navController.navigate(Screen.Auth.route) {
                        popUpTo(0) { inclusive = true }
                        launchSingleTop = true
                    }
                }
            )
        }
        composable(Screen.Generate.route) { backStackEntry ->
            val projectId = backStackEntry.arguments?.getString("projectId") ?: ""

            // Check if Gallery sent a reference image back
            val savedState = backStackEntry.savedStateHandle
            val refImageFromGallery = savedState.get<String>("continueEditImageUrl")
            val viewModel: GenerateViewModel = androidx.hilt.navigation.compose.hiltViewModel()

            androidx.compose.runtime.LaunchedEffect(refImageFromGallery) {
                if (refImageFromGallery != null) {
                    viewModel.setReferenceImage(refImageFromGallery)
                    savedState.remove<String>("continueEditImageUrl")
                }
            }

            GenerateScreen(
                viewModel = viewModel,
                onBack = { navController.popBackStack() },
                onNavigateToGallery = { navController.navigate("gallery/$projectId") }
            )
        }
        composable(Screen.Gallery.route) { backStackEntry ->
            GalleryScreen(
                onBack = { navController.popBackStack() },
                onContinueEdit = { imageUrl ->
                    navController.previousBackStackEntry
                        ?.savedStateHandle
                        ?.set("continueEditImageUrl", imageUrl)
                    navController.popBackStack()
                }
            )
        }
    }
}
