package dev.procyk.compose.camera

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import kotlinx.coroutines.CoroutineScope

@Composable
internal fun OnceLaunchedEffect(block: suspend CoroutineScope.() -> Unit) {
    LaunchedEffect(Unit, block)
}