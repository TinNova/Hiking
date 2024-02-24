package com.tinnovakovic.hiking.presentation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.tinnovakovic.hiking.shared.Destination

fun NavGraphBuilder.homeScreen() {
    composable(route = Destination.Home.name) {
        HomeScreen()
    }
}