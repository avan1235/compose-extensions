package `in`.procyk.compose.util

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import kotlinx.coroutines.CoroutineScope

@Composable
fun OnceLaunchedEffect(block: suspend CoroutineScope.() -> Unit) {
    LaunchedEffect(Unit, block)
}
