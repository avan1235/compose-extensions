package `in`.procyk.compose.util

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

@Composable
internal expect fun rememberPlatformColors(top: Color, bottom: Color)
