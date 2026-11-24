/*
 * Copyright (c) 2026 YAi. See LICENSE for the terms applicable to this copy.
 */
package app.yai.shells.vizo

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.runtime.withFrameNanos

data class VizoPalette(
    val background: Color,
    val surface: Color,
    val text: Color,
    val muted: Color,
    val accent: Color,
    val focus: Color,
)

enum class VizoSource { M3U, XTREAM, STALKER }

enum class VizoAction { CONTINUE, ACCOUNT, REMOTE, SEARCH, GUIDE, PLAYLISTS, GUIDE_SOURCES, SETTINGS }

data class VizoCategory(val id: String, val title: String, val count: Int? = null)

@Composable
fun VizoEntry(palette: VizoPalette, onClick: () -> Unit, modifier: Modifier = Modifier) {
    VizoButton(stringResource(R.string.vizo_short), palette, onClick, modifier, accent = true)
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun VizoHome(
    palette: VizoPalette,
    television: Boolean,
    categories: List<VizoCategory>,
    hasSources: Boolean,
    canContinue: Boolean,
    onMain: () -> Unit,
    onCategory: (String) -> Unit,
    onAction: (VizoAction) -> Unit,
    onAddSource: (VizoSource) -> Unit,
    modifier: Modifier = Modifier,
    firstFocus: FocusRequester? = null,
) {
    var page by rememberSaveable { mutableStateOf(VizoPage.HOME) }
    val internalFocus = remember { FocusRequester() }
    val focus = firstFocus ?: internalFocus
    BackHandler(enabled = page != VizoPage.HOME) { page = VizoPage.HOME }
    LaunchedEffect(page, television) {
        if (television) {
            withFrameNanos { }
            focus.requestFocus()
        }
    }

    val menu = when (page) {
        VizoPage.SOURCES -> listOf(
            MenuItem(R.string.vizo_m3u) { onAddSource(VizoSource.M3U) },
            MenuItem(R.string.vizo_xtream) { onAddSource(VizoSource.XTREAM) },
            MenuItem(R.string.vizo_stalker) { onAddSource(VizoSource.STALKER) },
        )
        VizoPage.SETTINGS -> listOf(
            MenuItem(R.string.vizo_account) { onAction(VizoAction.ACCOUNT) },
            MenuItem(R.string.vizo_playlists) { onAction(VizoAction.PLAYLISTS) },
            MenuItem(R.string.vizo_add) { page = VizoPage.SOURCES },
            MenuItem(R.string.vizo_epg) { onAction(VizoAction.GUIDE_SOURCES) },
            MenuItem(R.string.vizo_full_settings) { onAction(VizoAction.SETTINGS) },
        )
        VizoPage.HOME -> emptyList()
    }
    val actions = buildList {
        if (canContinue) add(MenuItem(R.string.vizo_continue) { onAction(VizoAction.CONTINUE) })
        add(MenuItem(R.string.vizo_account) { onAction(VizoAction.ACCOUNT) })
        if (!television) add(MenuItem(R.string.vizo_remote) { onAction(VizoAction.REMOTE) })
        add(MenuItem(R.string.vizo_search) { onAction(VizoAction.SEARCH) })
        add(MenuItem(R.string.vizo_guide) { onAction(VizoAction.GUIDE) })
        add(MenuItem(R.string.vizo_settings) { page = VizoPage.SETTINGS })
    }

    Column(
        modifier.fillMaxSize().background(palette.background)
            .padding(horizontal = if (television) 24.dp else 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Row(
            Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            VizoMark(palette.accent, Modifier.size(if (television) 40.dp else 34.dp))
            val title = when (page) {
                VizoPage.HOME -> R.string.vizo_home
                VizoPage.SOURCES -> R.string.vizo_sources
                VizoPage.SETTINGS -> R.string.vizo_settings
            }
            VizoText(stringResource(title), palette.text, Modifier.weight(1f), size = 20, strong = true)
            val target = if (television) Modifier.focusRequester(focus) else Modifier
            if (page == VizoPage.HOME) {
                VizoButton(stringResource(R.string.vizo_main), palette, onMain, target)
            } else {
                VizoButton(stringResource(R.string.vizo_back), palette, { page = VizoPage.HOME }, target)
            }
        }

        if (page != VizoPage.HOME) {
            menu.forEach { row ->
                VizoButton(stringResource(row.title), palette, row.onClick, Modifier.fillMaxWidth())
            }
        } else if (television) {
            LazyVerticalGrid(
                columns = GridCells.Adaptive(190.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                item(span = { GridItemSpan(maxLineSpan) }) {
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        VizoButton(stringResource(R.string.vizo_add), palette, { page = VizoPage.SOURCES }, accent = true)
                        actions.forEach { row ->
                            VizoButton(stringResource(row.title), palette, row.onClick)
                        }
                    }
                }
                if (!hasSources) {
                    item(span = { GridItemSpan(maxLineSpan) }) {
                        VizoText(stringResource(R.string.vizo_empty), palette.muted)
                    }
                }
                items(categories, key = { it.id }) { category ->
                    VizoButton(category.title, palette, { onCategory(category.id) }, Modifier.height(82.dp), count = category.count)
                }
            }
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                item {
                    VizoButton(
                        stringResource(R.string.vizo_add), palette, { page = VizoPage.SOURCES },
                        Modifier.fillMaxWidth(), accent = true,
                    )
                }
                if (!hasSources) {
                    item { VizoText(stringResource(R.string.vizo_empty), palette.muted, Modifier.padding(vertical = 8.dp)) }
                }
                item {
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        actions.forEach { row -> VizoButton(stringResource(row.title), palette, row.onClick) }
                    }
                }
                items(categories, key = { it.id }) { category ->
                    VizoButton(category.title, palette, { onCategory(category.id) }, Modifier.fillMaxWidth(), count = category.count)
                }
            }
        }
    }
}

private enum class VizoPage { HOME, SOURCES, SETTINGS }

private data class MenuItem(val title: Int, val onClick: () -> Unit)

@Composable
internal fun VizoButton(
    label: String,
    palette: VizoPalette,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    accent: Boolean = false,
    count: Int? = null,
) {
    var focused by remember { mutableStateOf(false) }
    val shape = RoundedCornerShape(13.dp)
    val line = when {
        focused -> palette.focus
        accent -> palette.accent.copy(alpha = 0.65f)
        else -> Color.Transparent
    }
    Row(
        modifier.heightIn(min = 48.dp)
            .onFocusChanged { focused = it.isFocused }
            .clip(shape)
            .background(if (focused || accent) palette.accent.copy(alpha = 0.14f) else palette.surface)
            .border(if (focused) 2.dp else 1.dp, line, shape)
            .clickable(role = Role.Button, onClick = onClick)
            .padding(horizontal = 13.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        if (count == null) {
            VizoText(label, palette.text, strong = focused || accent)
        } else {
            VizoText(label, palette.text, Modifier.weight(1f), strong = focused)
            VizoText(count.toString(), palette.muted, size = 13)
        }
    }
}

@Composable
internal fun VizoText(text: String, color: Color, modifier: Modifier = Modifier, size: Int = 15, strong: Boolean = false) {
    BasicText(
        text = text,
        modifier = modifier,
        style = TextStyle(color = color, fontSize = size.sp, fontWeight = if (strong) FontWeight.SemiBold else FontWeight.Normal),
        maxLines = 2,
        overflow = TextOverflow.Ellipsis,
    )
}

@Composable
private fun VizoMark(accent: Color, modifier: Modifier) {
    Box(modifier.clip(RoundedCornerShape(10.dp)).background(accent.copy(alpha = 0.17f)), contentAlignment = Alignment.Center) {
        Canvas(Modifier.size(23.dp)) {
            val width = size.width
            val height = size.height
            val mark = Path().apply {
                moveTo(width * 0.06f, height * 0.24f)
                lineTo(width * 0.35f, height * 0.24f)
                lineTo(width * 0.51f, height * 0.56f)
                lineTo(width * 0.74f, height * 0.15f)
                lineTo(width * 0.98f, height * 0.15f)
                lineTo(width * 0.51f, height * 0.87f)
                close()
            }
            drawPath(mark, accent)
        }
    }
}
