package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.repository.SyncStatusState
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IndustrialTopBar(
    syncStatus: SyncStatusState,
    onSyncClick: () -> Unit,
    onMenuClick: () -> Unit,
    onBackClick: (() -> Unit)? = null,
    title: String = "Electro Hub",
    isEditModeEnabled: Boolean = true,
    isDarkTheme: Boolean = true,
    onToggleTheme: (() -> Unit)? = null
) {
    val industrialColors = LocalIndustrialColors.current

    TopAppBar(
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Column {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = title,
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            ),
                            maxLines = 1
                        )

                        // Mode Badge (Editor vs Protected View)
                        Surface(
                            color = if (isEditModeEnabled) industrialColors.tagPrimaryBg else MaterialTheme.colorScheme.surfaceVariant,
                            shape = RoundedCornerShape(4.dp)
                        ) {
                            Text(
                                text = if (isEditModeEnabled) "EDIT" else "VIEW ONLY",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isEditModeEnabled) industrialColors.accentPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                                ),
                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                            )
                        }
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        // Cloud sync status dot
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .background(
                                    when (syncStatus) {
                                        is SyncStatusState.Syncing -> industrialColors.accentPrimary
                                        is SyncStatusState.Success -> industrialColors.accentSuccess
                                        is SyncStatusState.Error -> industrialColors.accentError
                                        else -> industrialColors.textMuted
                                    },
                                    RoundedCornerShape(50)
                                )
                        )
                        Text(
                            text = when (syncStatus) {
                                is SyncStatusState.Syncing -> "Syncing Cloud..."
                                is SyncStatusState.Success -> "Cloud Connected"
                                is SyncStatusState.Error -> "Offline (Cached)"
                                else -> "Database Active"
                            },
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontSize = 10.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        )
                    }
                }
            }
        },
        navigationIcon = {
            if (onBackClick != null) {
                IconButton(onClick = onBackClick) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back",
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }
            } else {
                IconButton(onClick = onMenuClick) {
                    Icon(
                        imageVector = Icons.Default.Menu,
                        contentDescription = "Open Premium Navigation Drawer",
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        },
        actions = {
            // Quick Theme Switch Icon in Top Bar
            if (onToggleTheme != null) {
                IconButton(onClick = onToggleTheme) {
                    Icon(
                        imageVector = if (isDarkTheme) Icons.Default.LightMode else Icons.Default.DarkMode,
                        contentDescription = if (isDarkTheme) "Switch to Light Mode" else "Switch to Dark Mode",
                        tint = if (isDarkTheme) SafetyAmberLight else BlueprintBlue
                    )
                }
            }

            // Cloud Database Sync Action
            IconButton(onClick = onSyncClick) {
                Icon(
                    imageVector = Icons.Default.Sync,
                    contentDescription = "Cloud Database Sync",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.surface,
            titleContentColor = MaterialTheme.colorScheme.onSurface
        )
    )
}
