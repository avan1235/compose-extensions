package `in`.procyk.compose.examples

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application

fun main() = application {
    Window(onCloseRequest = ::exitApplication) {
        ExamplesApp()
    }
}
