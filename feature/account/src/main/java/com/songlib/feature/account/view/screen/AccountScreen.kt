package com.songlib.feature.account.view.screen

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Checklist
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Login
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.songlib.core.common.entity.AuthState
import com.songlib.core.common.utils.Routes
import com.songlib.core.ui.components.action.AppTopBar
import com.songlib.feature.account.view.components.AccountHeader
import com.songlib.feature.account.viewmodel.AccountViewModel

@Composable
fun AccountScreen(
    navController: NavHostController,
    viewModel: AccountViewModel = hiltViewModel(),
    onSignInRequested: (
        onResult: (googleId: String, email: String, name: String, photo: String) -> Unit,
        onError: (message: String) -> Unit
    ) -> Unit,
) {
    val authState by viewModel.authState.collectAsState()
    val profile by viewModel.profile.collectAsState()
    val context = LocalContext.current
    var showSignOutConfirm by remember { mutableStateOf(false) }

    LaunchedEffect(authState) {
        if (authState is AuthState.Success) {
            Toast.makeText(context, "Welcome, ${profile.name}! 👋", Toast.LENGTH_SHORT).show()
        }
        if (authState is AuthState.Error) {
            Toast.makeText(context, (authState as AuthState.Error).message, Toast.LENGTH_SHORT).show()
        }
    }

    fun requestSignIn() {
        onSignInRequested(
            { googleId, email, name, photo ->
                viewModel.loginOrRegister(
                    googleId,
                    email,
                    name,
                    photo
                )
            },
            { message -> viewModel.signInFailed(message) }
        )
    }

    if (showSignOutConfirm) {
        AlertDialog(
            onDismissRequest = { showSignOutConfirm = false },
            title = { Text("Sign out?") },
            text = { Text("You can always sign in again to sync your drafts and edits.") },
            confirmButton = {
                TextButton(onClick = {
                    showSignOutConfirm = false
                    viewModel.signOut()
                    Toast.makeText(context, "Signed out", Toast.LENGTH_SHORT).show()
                }) {
                    Text("Sign out")
                }
            },
            dismissButton = {
                TextButton(onClick = { showSignOutConfirm = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    Scaffold(
        topBar = {
            AppTopBar(
                title = "My Account",
                showGoBack = true,
                onNavIconClick = { navController.popBackStack() },
                actions = {
                    val isLoading = authState is AuthState.Loading
                    if (isLoading) {
                        CircularProgressIndicator(modifier = Modifier.size(20.dp))
                    } else if (profile.isLoggedIn) {
                        IconButton(onClick = { showSignOutConfirm = true }) {
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
            AccountHeader(profile = profile)
            HorizontalDivider()

            ListItem(
                leadingContent = { Icon(Icons.Default.History, contentDescription = "History") },
                headlineContent = { Text("My History: Views & Searches") },
                modifier = Modifier.clickable { navController.navigate(Routes.HISTORY) }
            )
            HorizontalDivider()

            ListItem(
                leadingContent = { Icon(Icons.Default.EditNote, contentDescription = "Drafts") },
                headlineContent = { Text("My Song Drafts") },
                modifier = Modifier.clickable { navController.navigate(Routes.DRAFTS) }
            )
            HorizontalDivider()

            ListItem(
                leadingContent = { Icon(Icons.Default.Checklist, contentDescription = "Edits") },
                headlineContent = { Text("My Song Edits") },
                modifier = Modifier.clickable { navController.navigate(Routes.USER_EDITS) }
            )
            HorizontalDivider()

            if (profile.isAdmin) {
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
