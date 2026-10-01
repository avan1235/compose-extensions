package `in`.procyk.compose.examples

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import `in`.procyk.compose.calendar.SelectableCalendar
import `in`.procyk.compose.calendar.SelectableWeekCalendar
import `in`.procyk.compose.calendar.StaticCalendar
import `in`.procyk.compose.calendar.StaticWeekCalendar

@Composable
internal fun Calendars(
    onClose: () -> Unit,
) = ExampleSystemBarsScreen(onClose) {
    val scrollState = rememberScrollState()
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 12.dp)
            .verticalScroll(scrollState),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        Spacer(Modifier.height(24.dp))
        Titled("Static Calendar") { StaticCalendar() }
        Titled("Selectable Calendar") { SelectableCalendar() }
        Titled("Static Week Calendar") { StaticWeekCalendar() }
        Titled("Selectable Week Calendar") { SelectableWeekCalendar() }
    }
}

@Composable
private inline fun Titled(
    title: String,
    content: @Composable ColumnScope.() -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(title, maxLines = 1, style = MaterialTheme.typography.headlineLarge)
        content()
    }
}
