package com.ima2gen.app.ui

/**
 * Navigation route definitions for the app.
 */
sealed class Screen(val route: String) {
    data object Auth : Screen("auth")
    data object ProjectList : Screen("project_list")
    data object Generate : Screen("generate/{projectId}") {
        fun createRoute(projectId: String) = "generate/$projectId"
    }
    data object Gallery : Screen("gallery/{projectId}") {
        fun createRoute(projectId: String) = "gallery/$projectId"
    }
    data object Settings : Screen("settings")

}
