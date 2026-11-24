/*
 * Copyright (c) 2026 YAi. See LICENSE for the terms applicable to this copy.
 */
package app.yai.shells.vizo

import androidx.compose.foundation.background
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp

data class VizoProgramme(
    val id: Long,
    val title: String,
    val time: String,
    val onNow: Boolean,
    val canReplay: Boolean,
)

@Composable
fun VizoSchedule(
    channel: String,
    programmes: List<VizoProgramme>,
    palette: VizoPalette,
    onFullScreen: () -> Unit,
    onReplay: (Long) -> Unit,
    modifier: Modifier = Modifier,
    firstFocus: FocusRequester? = null,
) {
    val listState = rememberLazyListState()
    val current = programmes.indexOfFirst { it.onNow }
    LaunchedEffect(programmes.getOrNull(current)?.id) {
        if (current >= 0) listState.scrollToItem(current)
    }
    Column(
        modifier.background(palette.background).padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            VizoText(channel, palette.text, Modifier.weight(1f), size = 20, strong = true)
            VizoButton(
                stringResource(R.string.vizo_fullscreen), palette, onFullScreen,
                firstFocus?.let { Modifier.focusRequester(it) } ?: Modifier,
            )
        }
        if (programmes.isEmpty()) {
            VizoText(stringResource(R.string.vizo_no_guide), palette.muted)
        }
        LazyColumn(state = listState, verticalArrangement = Arrangement.spacedBy(6.dp)) {
            items(programmes, key = { it.id }) { programme ->
                if (programme.canReplay) {
                    VizoButton(
                        "${programme.title} · ${programme.time}", palette, { onReplay(programme.id) },
                        Modifier.fillMaxWidth(),
                    )
                } else {
                    var focused by remember { mutableStateOf(false) }
                    Column(
                        Modifier.fillMaxWidth().onFocusChanged { focused = it.isFocused }.focusable()
                            .background(if (focused) palette.accent.copy(alpha = 0.20f) else palette.surface)
                            .padding(horizontal = 13.dp, vertical = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp),
                    ) {
                        VizoText(programme.title, palette.text, strong = programme.onNow)
                        val time = if (programme.onNow) {
                            "${stringResource(R.string.vizo_on_now)} · ${programme.time}"
                        } else programme.time
                        VizoText(time, if (programme.onNow || focused) palette.focus else palette.muted, size = 13)
                    }
                }
            }
        }
    }
}

@Composable
fun VizoScheduleButton(palette: VizoPalette, onClick: () -> Unit, modifier: Modifier = Modifier) {
    VizoButton(stringResource(R.string.vizo_guide), palette, onClick, modifier)
}
