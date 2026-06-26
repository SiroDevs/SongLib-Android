package com.songlib.feature.broadcast.view

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.navigation.NavHostController
import com.songlib.core.broadcast.model.BroadcastState
import com.songlib.core.broadcast.model.ServerStatus
import com.songlib.core.ui.components.action.AppTopBar
import com.songlib.feature.broadcast.BroadcastViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BroadcastScreen(
    navController: NavHostController,
    viewModel: BroadcastViewModel,
) {
    val serverStatus by viewModel.serverStatus.collectAsState()
    val slideState by viewModel.slideState.collectAsState()
    val connectedClients by viewModel.connectedClients.collectAsState()
    val context = LocalContext.current
    val clipboard = LocalClipboardManager.current

    val notificationPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { /* notification just won't show if denied — broadcasting still works */ }

    fun requestStart() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val granted = ContextCompat.checkSelfPermission(
                context, Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
            if (!granted) {
                notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
        viewModel.startBroadcasting()
    }

    Scaffold(
        topBar = {
            AppTopBar(
                title = "Broadcast to PC",
                tagline = "Beta",
                showGoBack = true,
                onNavIconClick = { navController.popBackStack() },
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            ExplainerCard()

            StatusCard(
                serverStatus = serverStatus,
                connectedClients = connectedClients,
                onStart = ::requestStart,
                onStop = viewModel::stopBroadcasting,
            )

            if (serverStatus is ServerStatus.Running) {
                ConnectCard(
                    urls = (serverStatus as ServerStatus.Running).urls,
                    onCopy = { url ->
                        clipboard.setText(AnnotatedString(url))
                        Toast.makeText(context, "Link copied", Toast.LENGTH_SHORT).show()
                    },
                    onOpenHotspotSettings = { openTetherSettings(context) },
                )

                NowShowingCard(slideState = slideState)
            }
        }
    }
}

@Composable
private fun ExplainerCard() {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text("Mirror your presenter screen", style = MaterialTheme.typography.titleMedium)
            Text(
                "Turn on this phone's Personal Hotspot (or join the same Wi-Fi as your " +
                    "PC), tap Start below, then open the link on the PC's browser. Whatever " +
                    "verse you're showing on the phone — song or draft — appears there too, " +
                    "live. When you're not on a presenter screen, it just shows a waiting page.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
private fun StatusCard(
    serverStatus: ServerStatus,
    connectedClients: Int,
    onStart: () -> Unit,
    onStop: () -> Unit,
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                StatusDot(running = serverStatus is ServerStatus.Running)
                Spacer(Modifier.size(8.dp))
                Text(
                    text = when (serverStatus) {
                        ServerStatus.Stopped -> "Not broadcasting"
                        ServerStatus.Starting -> "Starting…"
                        is ServerStatus.Running -> "Broadcasting" +
                            if (connectedClients > 0) " • $connectedClients connected" else ""
                        is ServerStatus.Error -> "Couldn't start: ${serverStatus.message}"
                    },
                    style = MaterialTheme.typography.titleSmall,
                )
            }

            if (serverStatus is ServerStatus.Running || serverStatus is ServerStatus.Starting) {
                Button(onClick = onStop, modifier = Modifier.fillMaxWidth()) {
                    Text("Stop Broadcasting")
                }
            } else {
                Button(onClick = onStart, modifier = Modifier.fillMaxWidth()) {
                    Text("Start Broadcasting")
                }
            }
        }
    }
}

@Composable
private fun StatusDot(running: Boolean) {
    val color = if (running) Color(0xFF22C55E) else MaterialTheme.colorScheme.outline
    Box(
        modifier = Modifier
            .size(10.dp)
            .clip(CircleShape)
            .background(color)
    )
}

@Composable
private fun ConnectCard(
    urls: List<String>,
    onCopy: (String) -> Unit,
    onOpenHotspotSettings: () -> Unit,
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Text("Open on your PC", style = MaterialTheme.typography.titleSmall)

            if (urls.isEmpty()) {
                Text(
                    "No network link detected yet. Turn on Personal Hotspot, or join the " +
                        "same Wi-Fi network as your PC, then reopen this screen.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            } else {
                urls.forEach { url ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .clickable { onCopy(url) },
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            text = url,
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier
                                .weight(1f)
                                .padding(vertical = 8.dp),
                        )
                        IconButton(onClick = { onCopy(url) }) {
                            Icon(Icons.Default.ContentCopy, contentDescription = "Copy link")
                        }
                    }
                }
            }

            OutlinedButton(onClick = onOpenHotspotSettings, modifier = Modifier.fillMaxWidth()) {
                Icon(Icons.Default.Wifi, contentDescription = null)
                Spacer(Modifier.size(8.dp))
                Text("Open Hotspot Settings")
            }
        }
    }
}

@Composable
private fun NowShowingCard(slideState: BroadcastState) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text("Now showing", style = MaterialTheme.typography.labelMedium)
            when (slideState) {
                BroadcastState.Idle -> Text(
                    "Waiting page (no presenter open)",
                    style = MaterialTheme.typography.bodyMedium,
                )

                is BroadcastState.Slide -> Text(
                    "${slideState.title} — slide ${slideState.currentIndex + 1} of ${slideState.verses.size}",
                    style = MaterialTheme.typography.bodyMedium,
                )
            }
        }
    }
}

private fun openTetherSettings(context: Context) {
    val candidates = listOf(
        "android.settings.TETHER_SETTINGS",
        android.provider.Settings.ACTION_WIRELESS_SETTINGS,
    )
    for (action in candidates) {
        try {
            context.startActivity(Intent(action).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
            return
        } catch (_: Exception) {
            // try the next one
        }
    }
}
