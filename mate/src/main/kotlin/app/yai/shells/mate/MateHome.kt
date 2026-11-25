/* Copyright (c) 2026 YAi. See LICENSE for the terms applicable to this copy. */
package app.yai.shells.mate

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.foundation.focusGroup
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import app.yai.shells.vizo.R as ShellR
import app.yai.shells.vizo.VizoButton
import app.yai.shells.vizo.VizoPalette
import app.yai.shells.vizo.VizoSource
import app.yai.shells.vizo.VizoText

enum class MateAction {
    CONTINUE, ACCOUNT, SEARCH, CHANNELS, ARCHIVE, HISTORY, FAVORITES, MOVIES, SERIES, DOWNLOADS,
    APPEARANCE, PLAYLISTS, GUIDE_SOURCES, PLAYER, REMOTE_SHORTCUTS, PARENTAL, SETTINGS,
}

private enum class MatePage { GUIDE, LIBRARY, SETTINGS, ADD_SOURCE }

@Composable
fun MateHome(
    palette: VizoPalette,
    hasSources: Boolean,
    canContinue: Boolean,
    homeFocus: FocusRequester,
    onEnterGuide: () -> Unit,
    onAction: (MateAction) -> Unit,
    onAddSource: (VizoSource) -> Unit,
    modifier: Modifier = Modifier,
    guide: @Composable () -> Unit,
) {
    var page by rememberSaveable { mutableStateOf(MatePage.GUIDE) }
    val panelFocus = remember { FocusRequester() }
    BackHandler(page != MatePage.GUIDE) {
        page = if (page == MatePage.ADD_SOURCE && hasSources) MatePage.SETTINGS else MatePage.GUIDE
    }
    LaunchedEffect(page) {
        withFrameNanos { }
        if (page == MatePage.GUIDE) homeFocus.requestFocus() else panelFocus.requestFocus()
    }

    val shortcuts = buildList {
        if (canContinue) add(ShellR.string.vizo_continue to MateAction.CONTINUE)
        if (hasSources) {
            add(R.string.mate_live to MateAction.CHANNELS)
            add(ShellR.string.vizo_archive to MateAction.ARCHIVE)
            add(ShellR.string.vizo_search to MateAction.SEARCH)
        }
    }
    val library = listOf(
        ShellR.string.vizo_favorites to MateAction.FAVORITES,
        ShellR.string.vizo_history to MateAction.HISTORY,
        R.string.mate_downloads to MateAction.DOWNLOADS,
        R.string.mate_movies to MateAction.MOVIES,
        R.string.mate_series to MateAction.SERIES,
    )

    Column(modifier.fillMaxSize().background(palette.background).padding(18.dp)) {
        Row(
            Modifier.fillMaxWidth().padding(bottom = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Box(Modifier.size(13.dp).clip(RoundedCornerShape(4.dp)).background(palette.accent))
            VizoText(stringResource(R.string.mate_brand), palette.text, Modifier.weight(1f), size = 25, strong = true)
        }
        Row(Modifier.fillMaxSize(), horizontalArrangement = Arrangement.spacedBy(14.dp)) {
            Column(
                Modifier.width(182.dp).fillMaxHeight().clip(RoundedCornerShape(16.dp))
                    .background(palette.surface).verticalScroll(rememberScrollState()).focusGroup().padding(8.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                VizoButton(stringResource(ShellR.string.vizo_guide), palette, {
                    if (page == MatePage.GUIDE && hasSources) onEnterGuide() else page = MatePage.GUIDE
                },
                    Modifier.fillMaxWidth().focusRequester(homeFocus), accent = page == MatePage.GUIDE)
                shortcuts.forEach { (label, action) ->
                    VizoButton(stringResource(label), palette, { onAction(action) }, Modifier.fillMaxWidth())
                }
                if (hasSources) {
                    VizoButton(stringResource(R.string.mate_library), palette, { page = MatePage.LIBRARY },
                        Modifier.fillMaxWidth(), accent = page == MatePage.LIBRARY)
                } else {
                    VizoButton(stringResource(ShellR.string.vizo_add), palette, { page = MatePage.ADD_SOURCE },
                        Modifier.fillMaxWidth(), accent = page == MatePage.ADD_SOURCE)
                }
                VizoButton(stringResource(ShellR.string.vizo_settings), palette, { page = MatePage.SETTINGS },
                    Modifier.fillMaxWidth(), accent = page == MatePage.SETTINGS)
            }
            Box(Modifier.weight(1f).fillMaxHeight().clip(RoundedCornerShape(16.dp)).background(palette.surface)) {
                if (page == MatePage.GUIDE && hasSources) {
                    guide()
                } else if (page == MatePage.GUIDE) {
                    Column(
                        Modifier.align(Alignment.Center).padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        VizoText(stringResource(ShellR.string.vizo_empty), palette.text, size = 18)
                        VizoButton(stringResource(ShellR.string.vizo_add), palette, { page = MatePage.ADD_SOURCE }, accent = true)
                    }
                } else {
                    Column(
                        Modifier.align(Alignment.TopEnd).widthIn(max = 365.dp).fillMaxSize()
                            .verticalScroll(rememberScrollState()).padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp),
                    ) {
                        val title = when (page) {
                            MatePage.LIBRARY -> R.string.mate_library
                            MatePage.ADD_SOURCE -> ShellR.string.vizo_sources
                            else -> ShellR.string.vizo_settings
                        }
                        VizoText(stringResource(title), palette.text, size = 22, strong = true)
                        if (page == MatePage.ADD_SOURCE) {
                            VizoButton(stringResource(ShellR.string.vizo_m3u), palette, { onAddSource(VizoSource.M3U) },
                                Modifier.fillMaxWidth().focusRequester(panelFocus))
                            VizoButton(stringResource(ShellR.string.vizo_xtream), palette, { onAddSource(VizoSource.XTREAM) }, Modifier.fillMaxWidth())
                            VizoButton(stringResource(ShellR.string.vizo_stalker), palette, { onAddSource(VizoSource.STALKER) }, Modifier.fillMaxWidth())
                        } else {
                            if (page == MatePage.SETTINGS) {
                                VizoButton(stringResource(ShellR.string.vizo_add), palette, { page = MatePage.ADD_SOURCE },
                                    Modifier.fillMaxWidth().focusRequester(panelFocus))
                            }
                            val options = if (page == MatePage.LIBRARY) library else listOf(
                                ShellR.string.vizo_playlists to MateAction.PLAYLISTS,
                                ShellR.string.vizo_epg to MateAction.GUIDE_SOURCES,
                                R.string.mate_player to MateAction.PLAYER,
                                R.string.mate_controls to MateAction.REMOTE_SHORTCUTS,
                                R.string.mate_parental to MateAction.PARENTAL,
                                ShellR.string.vizo_account to MateAction.ACCOUNT,
                                ShellR.string.shell_appearance to MateAction.APPEARANCE,
                                ShellR.string.vizo_full_settings to MateAction.SETTINGS,
                            )
                            options.forEachIndexed { index, (label, action) ->
                                val button = Modifier.fillMaxWidth()
                                VizoButton(stringResource(label), palette, { onAction(action) },
                                    if (page == MatePage.LIBRARY && index == 0) button.focusRequester(panelFocus) else button)
                            }
                        }
                    }
                }
            }
        }
    }
}
