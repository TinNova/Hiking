package com.tinnovakovic.hiking.shared

import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

data class Spacing(
    val extraSmall: Dp = 4.dp,
    val small: Dp = 8.dp,
    val medium: Dp = 16.dp,
    val large: Dp = 24.dp,
    val extraLarge: Dp = 32.dp,
    val xExtraLarge: Dp = 40.dp,

    val topAppBarHeight: Dp = 56.dp,
)

val MaterialTheme.spacing: Spacing
    get() = Spacing()
