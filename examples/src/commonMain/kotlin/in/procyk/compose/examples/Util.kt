package `in`.procyk.compose.examples

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Density
import `in`.procyk.compose.util.NoSystemBarsScreen
import `in`.procyk.compose.util.SystemBarsScreen

@Composable
internal fun ExampleNoSystemBarsScreen(
    visible: Boolean = true,
    onVisibleChange: (Boolean) -> Unit = {},
    isCloseAvailable: Boolean = true,
    content: @Composable BoxScope.() -> Unit,
) {
    if (!visible) return

    NoSystemBarsScreen {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background),
            contentAlignment = Alignment.Center,
        ) {
            content()
        }
        if (isCloseAvailable) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .windowInsetsPadding(object : WindowInsets by WindowInsets.systemBars {
                        override fun getBottom(density: Density): Int = 0
                    }),
            ) {
                CloseButton(onVisibleChange)
            }
        }
    }
}

@Composable
internal fun ExampleSystemBarsScreen(
    visible: Boolean = true,
    onVisibleChange: (Boolean) -> Unit = {},
    isCloseAvailable: Boolean = true,
    content: @Composable BoxScope.() -> Unit,
) {
    if (!visible) return

    SystemBarsScreen(
        top = MaterialTheme.colorScheme.background,
        bottom = MaterialTheme.colorScheme.background,
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background),
            contentAlignment = Alignment.Center,
        ) {
            content()
        }
        if (isCloseAvailable) CloseButton(onVisibleChange)
    }
}

@Composable
internal fun ExampleButton(name: String, onClick: () -> Unit) {
    Button(onClick = onClick) {
        Text(name)
    }
}

@Composable
private fun CloseButton(onVisibleChange: (Boolean) -> Unit) {
    IconButton(onClick = { onVisibleChange(false) }) {
        Icon(
            imageVector = Icons.Default.Close,
            contentDescription = null,
        )
    }
}
