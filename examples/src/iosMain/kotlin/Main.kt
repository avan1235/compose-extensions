import androidx.compose.ui.window.ComposeUIViewController
import `in`.procyk.compose.examples.ExamplesApp
import platform.UIKit.UIViewController

fun Main(): UIViewController = ComposeUIViewController {
    ExamplesApp()
}
