/* Copyright (c) 2026 YAi. See LICENSE for the terms applicable to this copy. */
package app.yai.shells.vizo

import android.content.SharedPreferences
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties

enum class ShellMode(val title: Int) {
    CLASSIC(R.string.shell_classic),
    VIZO(R.string.vizo_home),
    MATE(R.string.shell_mate),
}

@Stable
class ShellModes internal constructor(private val preferences: SharedPreferences, val television: Boolean) {
    val available = if (television) ShellMode.entries else listOf(ShellMode.CLASSIC, ShellMode.VIZO)
    var selected by mutableStateOf(available.firstOrNull { it.name == preferences.getString("mode", null) } ?: ShellMode.CLASSIC)
        private set
    internal var page by mutableStateOf<DialogPage?>(null)
    internal var transition by mutableIntStateOf(0)
        private set

    fun openSettings() { page = DialogPage.SETTINGS }
    fun openAppearance() { page = DialogPage.APPEARANCE }

    internal fun select(mode: ShellMode) {
        if (mode !in available) return
        page = null
        if (selected == mode) return
        preferences.edit().putString("mode", mode.name).apply()
        selected = mode
        transition++
    }
}

internal enum class DialogPage { SETTINGS, APPEARANCE }
val LocalShellModes = staticCompositionLocalOf<ShellModes> { error("ShellModeHost is required") }

@Composable
fun ShellModeHost(
    television: Boolean,
    palette: VizoPalette,
    animate: Boolean = true,
    content: @Composable () -> Unit,
) {
    val app = LocalContext.current.applicationContext
    val modes = remember(app, television) { ShellModes(app.getSharedPreferences("yai_shell_appearance", 0), television) }
    val flash = remember { Animatable(0f) }
    LaunchedEffect(modes.transition, animate) {
        if (modes.transition > 0 && animate) {
            flash.snapTo(1f)
            flash.animateTo(0f, tween(230))
        } else flash.snapTo(0f)
    }
    CompositionLocalProvider(LocalShellModes provides modes) {
        Box(Modifier.fillMaxSize()) {
            content()
            if (flash.value > 0f) Box(
                Modifier.matchParentSize().graphicsLayer { alpha = flash.value }
                    .background(palette.background.copy(alpha = 0.82f)),
                contentAlignment = Alignment.Center,
            ) {
                VizoText(stringResource(modes.selected.title), palette.text, size = if (television) 28 else 22, strong = true)
            }
        }
        val page = modes.page
        if (page != null) ShellModeDialog(modes, page, palette)
    }
}

@Composable
private fun ShellModeDialog(modes: ShellModes, page: DialogPage, palette: VizoPalette) {
    val first = remember { FocusRequester() }
    fun back() {
        modes.page = if (page == DialogPage.APPEARANCE) DialogPage.SETTINGS else null
    }

    Dialog(onDismissRequest = ::back, properties = DialogProperties(usePlatformDefaultWidth = false)) {
        Column(
            Modifier.padding(24.dp).widthIn(max = if (modes.television) 420.dp else 360.dp)
                .fillMaxWidth().clip(RoundedCornerShape(20.dp)).background(palette.background).padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(9.dp),
        ) {
            val title = if (page == DialogPage.SETTINGS) R.string.vizo_settings else R.string.shell_appearance
            VizoText(stringResource(title), palette.text, size = 23, strong = true)
            if (page == DialogPage.SETTINGS) {
                VizoButton(stringResource(R.string.shell_appearance), palette, modes::openAppearance,
                    Modifier.fillMaxWidth().focusRequester(first), accent = true)
                VizoText(stringResource(modes.selected.title), palette.muted)
            } else {
                modes.available.forEach { mode ->
                    val current = mode == modes.selected
                    VizoButton(stringResource(mode.title), palette, { modes.select(mode) },
                        if (current) Modifier.fillMaxWidth().focusRequester(first) else Modifier.fillMaxWidth(), accent = current)
                }
            }
            VizoButton(stringResource(R.string.vizo_back), palette, ::back, Modifier.fillMaxWidth())
        }
        LaunchedEffect(page, modes.television) {
            if (modes.television) {
                withFrameNanos { }
                first.requestFocus()
            }
        }
    }
}
