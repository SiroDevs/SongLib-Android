package com.songlib.feature.user.view.screen

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Checklist
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Login
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.songlib.core.common.entity.AuthState
import com.songlib.core.common.utils.Routes
import com.songlib.core.ui.components.action.AppTopBar
import com.songlib.feature.user.viewmodel.UserViewModel

@Composable
fun UserScreen(
    navController: NavHostController,
    viewModel: UserViewModel = hiltViewModel(),
    onSignInRequested: (
        onResult: (googleId: String, email: String, name: String, photo: String) -> Unit,
        onError: (message: String) -> Unit
    ) -> Unit,
) {
    val authState by viewModel.authState.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(authState) {
        if (authState is AuthState.Success) {
            Toast.makeText(context, "Welcome, ${viewModel.userName}! 👋", Toast.LENGTH_SHORT).show()
        }
        if (authState is AuthState.Error) {
            Toast.makeText(context, (authState as AuthState.Error).message, Toast.LENGTH_SHORT).show()
        }
    }

    fun requestSignIn() {
        onSignInRequested(
            { googleId, email, name, photo -> viewModel.loginOrRegister(googleId, email, name, photo) },
            { message -> viewModel.signInFailed(message) }
        )
    }

    Scaffold(
        topBar = {
            AppTopBar(
                title = "Account",
                showGoBack = true,
                onNavIconClick = { navController.popBackStack() },
                actions = {
                    val isLoading = authState is AuthState.Loading
                    if (isLoading) {
                        CircularProgressIndicator(modifier = Modifier.size(20.dp))
                    } else if (viewModel.isLoggedIn) {
                        IconButton(onClick = { viewModel.signOut() }) {
                            Icon(Icons.Default.Logout, contentDescription = "Sign out")
                        }
                    } else {
                        IconButton(onClick = { requestSignIn() }) {
                            Icon(Icons.Default.Login, contentDescription = "Sign in")
                        }
                    }
                }
            )
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding)) {
            UserHeader(viewModel = viewModel)
            HorizontalDivider()

            if (hasHistory) {
                ListItem(
                    leadingContent = { Icon(Icons.Default.History, contentDescription = "My History") },
                    headlineContent = { Text("My History") },
                    modifier = Modifier.clickable { navController.navigate(Routes.HISTORY) }
                )
                HorizontalDivider()
            }

            ListItem(
                leadingContent = { Icon(Icons.Default.EditNote, contentDescription = "My Drafts") },
                headlineContent = { Text("My Drafts") },
                modifier = Modifier.clickable { navController.navigate(Routes.DRAFTS) }
            )
            HorizontalDivider()

            ListItem(
                leadingContent = { Icon(Icons.Default.Checklist, contentDescription = "My Edits") },
                headlineContent = { Text("My Edits") },
                modifier = Modifier.clickable { navController.navigate(Routes.USER_EDITS) }
            )
            HorizontalDivider()

            if (viewModel.isAdmin) {
                ListItem(
                    leadingContent = {
                        Icon(
                            Icons.Default.AdminPanelSettings,
                            contentDescription = "Admin Edits",
                            tint = MaterialTheme.colorScheme.primary,
                        )
                    },
                    headlineContent = {
                        Text("Admin: Pending Edits", color = MaterialTheme.colorScheme.primary)
                    },
                    modifier = Modifier.clickable { navController.navigate(Routes.ADMIN_EDITS) }
                )
                HorizontalDivider()
            }
        }
    }
}

@Composable
private fun UserHeader(viewModel: UserViewModel) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        if (viewModel.isLoggedIn && viewModel.userPhotoUrl.isNotEmpty()) {
            AsyncImage(
                model = viewModel.userPhotoUrl,
                contentDescription = "Profile photo",
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
            )
        } else {
            Icon(
                imageVector = Icons.Default.AccountCircle,
                contentDescription = null,
                modifier = Modifier.size(56.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        if (viewModel.isLoggedIn) {
            Column {
                Text(viewModel.userName, style = MaterialTheme.typography.titleMedium)
                Text(
                    text = viewModel.userEmail,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            Text(
                text = "Sign in with the icon above to sync your drafts, edits, and more.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Start,
            )
        }
    }
    Spacer(Modifier.height(4.dp))
}
