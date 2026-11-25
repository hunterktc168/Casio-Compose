package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Description
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.ui.components.CodePill
import com.example.ui.components.MarkdownBlock
import com.example.ui.components.MarkdownBlockView
import com.example.ui.components.parseMarkdownBlocks
import com.example.ui.theme.StudioBorder
import com.example.ui.theme.StudioBorderSubtle
import com.example.ui.theme.StudioError
import com.example.ui.theme.StudioInk
import com.example.ui.theme.StudioPanel
import com.example.ui.theme.StudioPanelElevated
import com.example.ui.theme.StudioTeal
import com.example.ui.theme.StudioTealContainer
import com.example.ui.theme.StudioTextPrimary
import com.example.ui.theme.StudioTextSecondary
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun DocumentationModal(
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    var blocks by remember { mutableStateOf<List<MarkdownBlock>>(emptyList()) }
    var loadError by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        isLoading = true
        val result = withContext(Dispatchers.IO) {
            runCatching {
                context.assets.open("README.md").bufferedReader().use { it.readText() }
            }
        }
        result
            .onSuccess { raw ->
                blocks = parseMarkdownBlocks(raw)
                loadError = null
            }
            .onFailure { err ->
                loadError = err.message ?: "Unable to load README.md"
            }
        isLoading = false
    }

    val sectionAnchors = remember(blocks) {
        blocks.mapIndexedNotNull { index, block ->
            if (block is MarkdownBlock.Heading && block.level <= 2) {
                block.text to index
            } else {
                null
            }
        }
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            decorFitsSystemWindows = false
        )
    ) {
        BackHandler(onBack = onDismiss)

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(StudioInk)
                .windowInsetsPadding(WindowInsets.statusBars)
                .windowInsetsPadding(WindowInsets.navigationBars)
        ) {
            // Sticky documentation header
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(StudioPanel)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Description,
                            contentDescription = null,
                            tint = StudioTeal,
                            modifier = Modifier.size(22.dp)
                        )
                        Column {
                            Text(
                                text = "App Documentation",
                                style = MaterialTheme.typography.titleLarge,
                                color = StudioTextPrimary
                            )
                            Text(
                                text = "Capabilities • Tech Stack • User Guide",
                                style = MaterialTheme.typography.bodySmall,
                                color = StudioTextSecondary
                            )
                        }
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        CodePill(text = "README.md")
                        IconButton(
                            onClick = onDismiss,
                            modifier = Modifier
                                .size(48.dp)
                                .testTag("close_docs_button")
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.Close,
                                contentDescription = "Close documentation",
                                tint = StudioTextPrimary
                            )
                        }
                    }
                }

                if (sectionAnchors.isNotEmpty()) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState())
                            .padding(start = 16.dp, end = 16.dp, bottom = 10.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        sectionAnchors.forEach { (label, index) ->
                            FilterChip(
                                selected = false,
                                onClick = {
                                    coroutineScope.launch {
                                        listState.animateScrollToItem(index)
                                    }
                                },
                                label = {
                                    Text(
                                        text = if (label == "CDP-S100 Guide") "Overview" else label
                                    )
                                },
                                modifier = Modifier
                                    .heightIn(min = 48.dp)
                                    .testTag("docs_jump_$index"),
                                colors = FilterChipDefaults.filterChipColors(
                                    containerColor = StudioPanelElevated,
                                    labelColor = StudioTeal
                                ),
                                border = FilterChipDefaults.filterChipBorder(
                                    enabled = true,
                                    selected = false,
                                    borderColor = StudioBorder
                                )
                            )
                        }
                    }
                }

                HorizontalDivider(color = StudioBorderSubtle, thickness = 1.dp)
            }

            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentAlignment = Alignment.TopCenter
            ) {
                when {
                    isLoading -> {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(color = StudioTeal)
                        }
                    }

                    loadError != null -> {
                        Box(
                            modifier = Modifier.fillMaxSize().padding(24.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = loadError ?: "Unable to load documentation",
                                style = MaterialTheme.typography.bodyLarge,
                                color = StudioError
                            )
                        }
                    }

                    else -> {
                        LazyColumn(
                            state = listState,
                            modifier = Modifier
                                .widthIn(max = 860.dp)
                                .fillMaxSize()
                                .testTag("docs_markdown_list"),
                            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 18.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            itemsIndexed(
                                items = blocks,
                                key = { index, _ -> "md_block_$index" }
                            ) { _, block ->
                                MarkdownBlockView(block = block)
                            }
                            item(key = "md_bottom_spacer") {
                                Spacer(modifier = Modifier.padding(bottom = 16.dp))
                            }
                        }
                    }
                }
            }
        }
    }
}
