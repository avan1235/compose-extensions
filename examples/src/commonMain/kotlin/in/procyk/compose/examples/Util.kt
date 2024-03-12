package `in`.procyk.compose.examples

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import `in`.procyk.compose.util.NoBottomBarScreen
import `in`.procyk.compose.util.NoSystemBarsScreen
import `in`.procyk.compose.util.SystemBarsScreen

@Composable
internal fun ExampleNoSystemBarsScreen(
    onClose: () -> Unit = {},
    isCloseAvailable: Boolean = true,
    content: @Composable BoxScope.() -> Unit,
) {
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
            NoBottomBarScreen {
                CloseButton(onClose)
            }
        }
    }
}

@Composable
internal fun ExampleSystemBarsScreen(
    onClose: () -> Unit = {},
    isCloseAvailable: Boolean = true,
    content: @Composable BoxScope.() -> Unit,
) {
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
        if (isCloseAvailable) CloseButton(onClose)
    }
}

@Composable
internal fun ExampleButton(name: String, onClick: () -> Unit) {
    Button(onClick = onClick) {
        Text(name)
    }
}

@Composable
private fun CloseButton(onClose: () -> Unit) {
    IconButton(onClick = { onClose() }) {
        Icon(
            imageVector = Icons.Default.Close,
            contentDescription = null,
        )
    }
}
