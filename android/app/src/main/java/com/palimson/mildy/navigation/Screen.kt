package com.palimson.mildy.navigation

sealed class Screen(val route: String, val titleRes: Int? = null) {
    object Home : Screen("home")
    object Tools : Screen("tools")
    object Projects : Screen("projects")
    object Settings : Screen("settings")

    // Creator Studios
    object Subtitles : Screen("subtitles")
    object PhotoFix : Screen("photofix")
    object VideoTools : Screen("videotools")
    object Thumbnail : Screen("thumbnail")
    object Utility : Screen("utility")
}
