package com.songlib.presentation.screens.home.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Backspace
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.Icon
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.*

@Composable
fun DialPad(
    onNumberClick: (String) -> Unit,
    onBackspaceClick: () -> Unit,
    onSearchClick: () -> Unit
) {
    val dialPadItems = listOf(
        listOf("6", "7", "8", "9"),
        listOf("2", "3", "4", "5"),
        listOf(
            "1",
            "0",
            Icons.AutoMirrored.Filled.Backspace to onBackspaceClick,
            Icons.Default.Check to onSearchClick
        )
    )
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.BottomCenter
    ) {
        Card(
            shape = RoundedCornerShape(topStart = 5.dp, topEnd = 5.dp),
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.onPrimary
            )
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.padding(16.dp)
            ) {
                dialPadItems.forEach { row ->
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        row.forEach { item ->
                            val modifier = Modifier.weight(1f)
                            when (item) {
                                is String -> DialButton(
                                    label = item, modifier = modifier
                                ) { onNumberClick(item) }

                                is Pair<*, *> -> {
                                    @Suppress("UNCHECKED_CAST")
                                    DialIconButton(
                                        icon = item.first as ImageVector,
                                        modifier = modifier,
                                        onClick = item.second as () -> Unit
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun DialButton(
    label: String, modifier: Modifier, onClick: () -> Unit
) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier.aspectRatio(1f)
    ) { Text(label, fontSize = 20.sp) }
}

@Composable
private fun DialIconButton(
    icon: ImageVector, modifier: Modifier, onClick: () -> Unit
) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier.aspectRatio(1f)
    ) { Icon(icon, contentDescription = "") }
}