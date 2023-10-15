package dev.procyk.compose.camera

internal inline fun <T, U> runIfNonNull(t: T?, crossinline action: (T) -> U): U? =
    if (t != null) action(t) else null