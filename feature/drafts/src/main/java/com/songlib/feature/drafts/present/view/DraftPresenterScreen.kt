package com.songlib.feature.drafts.present.view

import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.songlib.core.common.entity.UiState
import com.songlib.core.common.utils.AppFonts
import com.songlib.core.common.utils.Routes
import com.songlib.core.database.model.DraftEntity
import com.songlib.core.ui.components.action.AppTopBar
import com.songlib.core.ui.components.indicators.EmptyState
import com.songlib.feature.drafts.present.viewmodel.DraftPresenterViewModel
import com.songlib.feature.song.presentor.view.PresenterContent

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DraftPresenterScreen(
    navController: NavHostController,
    draft: DraftEntity,
    horizontalSlides: Boolean = false,
    viewModel: DraftPresenterViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()
    val currentDraft by viewModel.currentDraft.collectAsState()
    val verses by viewModel.verses.collectAsState()
    val indicators by viewModel.indicators.collectAsState()
    val hasPrevious by viewModel.hasPrevious.collectAsState()
    val hasNext by viewModel.hasNext.collectAsState()
    val context = LocalContext.current

    var fontSize by remember { mutableFloatStateOf(AppFonts.DEFAULT_FONT_SP) }

    LaunchedEffect(draft) { viewModel.load(draft) }

    LaunchedEffect(Unit) {
        viewModel.toastEvent.collect { msg ->
            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
        }
    }

    val displayTitle = currentDraft?.title ?: draft.title

    Scaffold(
        topBar = {
            AppTopBar(
                title = displayTitle,
                tagline = "Draft",
                showGoBack = true,
                onNavIconClick = { navController.popBackStack() },
                actions = {
                    IconButton(onClick = {
                        val d = currentDraft ?: draft
                        navController.currentBackStackEntry
                            ?.savedStateHandle
                            ?.set("draft_to_edit", d)
                        navController.navigate(Routes.DRAFT_EDITOR)
                    }) {
                        Icon(Icons.Default.Edit, contentDescription = "Edit draft")
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
        ) {
            when (uiState) {
                UiState.Loaded -> PresenterContent(
                    verses = verses,
                    indicators = indicators,
                    horizontalSlides = horizontalSlides,
                    hasPrevious = hasPrevious,
                    hasNext = hasNext,
                    fontSize = fontSize,
                    onFontSizeChange = {
                        fontSize = it.coerceIn(
                            AppFonts.MIN_FONT_SP,
                            AppFonts.MAX_FONT_SP
                        )
                    },
                    onNavigatePrevious = { viewModel.navigatePrevious() },
                    onNavigateNext = { viewModel.navigateNext() },
                    onVerseIndexChanged = { viewModel.onVerseIndexChanged(it) },
                )

                UiState.Loading -> { }

                else -> EmptyState()
            }
        }
    }
}
