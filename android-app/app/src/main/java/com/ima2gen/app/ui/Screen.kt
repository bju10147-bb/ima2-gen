package com.ima2gen.app.ui

/**
 * Navigation route definitions for the app.
 */
sealed class Screen(val route: String) {
    data object Auth : Screen("auth")
    data object ApiKeyGuide : Screen("api_key_guide")
    data object Main : Screen("main")
    data object Generate : Screen("generate")
    data object Gallery : Screen("gallery")
    data object ImageViewer : Screen("image_viewer/{filename}") {
        fun createRoute(filename: String) = "image_viewer/$filename"
    }
    data object Sessions : Screen("sessions")
    data object Settings : Screen("settings")
}
