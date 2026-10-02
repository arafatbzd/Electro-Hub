package com.example.ui.dialogs

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.repository.SyncStatusState
import com.example.ui.theme.*

enum class CloudSourceTab {
    SUPABASE,
    GOOGLE_SHEETS
}

@Composable
fun GoogleSheetSyncModal(
    currentSheetId: String,
    currentSupabaseUrl: String = "https://your-project.supabase.co",
    currentSupabaseKey: String = "",
    bulkSqlScript: String = "",
    syncStatus: SyncStatusState,
    onDismiss: () -> Unit,
    onSheetIdChanged: (String) -> Unit,
    onSupabaseUrlChanged: (String) -> Unit = {},
    onSupabaseKeyChanged: (String) -> Unit = {},
    onSyncSheetTrigger: () -> Unit,
    onSyncSupabaseTrigger: () -> Unit = {},
    onBulkPushSupabase: () -> Unit = {},
    onResetDefaults: () -> Unit
) {
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current
    val industrialColors = LocalIndustrialColors.current

    var selectedSource by remember { mutableStateOf(CloudSourceTab.SUPABASE) }
    var inputSheetId by remember { mutableStateOf(currentSheetId) }
    var inputSupaUrl by remember { mutableStateOf(currentSupabaseUrl) }
    var inputSupaKey by remember { mutableStateOf(currentSupabaseKey) }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.92f)
                .padding(4.dp)
                .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(16.dp)),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(18.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Header
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Surface(
                        color = industrialColors.tagPrimaryBg,
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.size(40.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = if (selectedSource == CloudSourceTab.SUPABASE) Icons.Default.CloudSync else Icons.Default.TableChart,
                                contentDescription = null,
                                tint = industrialColors.accentPrimary,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }
                    Column {
                        Text(
                            text = "Cloud Database Sync",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        )
                        Text(
                            text = "Supabase (Electro Hub) & Google Sheets Sync",
                            style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                        )
                    }
                }

                // Cloud Provider Selector Tabs
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FilterChip(
                        selected = selectedSource == CloudSourceTab.SUPABASE,
                        onClick = { selectedSource = CloudSourceTab.SUPABASE },
                        leadingIcon = {
                            Icon(Icons.Default.Bolt, contentDescription = null, modifier = Modifier.size(16.dp))
                        },
                        label = { Text("⚡ Supabase", fontWeight = FontWeight.Bold, fontSize = 12.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.primary,
                            selectedLabelColor = MaterialTheme.colorScheme.onPrimary,
                            containerColor = MaterialTheme.colorScheme.surfaceVariant,
                            labelColor = MaterialTheme.colorScheme.onSurfaceVariant
                        ),
                        modifier = Modifier.weight(1f)
                    )

                    FilterChip(
                        selected = selectedSource == CloudSourceTab.GOOGLE_SHEETS,
                        onClick = { selectedSource = CloudSourceTab.GOOGLE_SHEETS },
                        leadingIcon = {
                            Icon(Icons.Default.TableChart, contentDescription = null, modifier = Modifier.size(16.dp))
                        },
                        label = { Text("📊 Google Sheets", fontWeight = FontWeight.Bold, fontSize = 12.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.primary,
                            selectedLabelColor = MaterialTheme.colorScheme.onPrimary,
                            containerColor = MaterialTheme.colorScheme.surfaceVariant,
                            labelColor = MaterialTheme.colorScheme.onSurfaceVariant
                        ),
                        modifier = Modifier.weight(1f)
                    )
                }

                // Sync Status Box
                Surface(
                    color = when (syncStatus) {
                        is SyncStatusState.Syncing -> industrialColors.tagPrimaryBg
                        is SyncStatusState.Success -> industrialColors.tagSuccessBg
                        is SyncStatusState.Error -> Color(0xFFFFEBEE)
                        else -> MaterialTheme.colorScheme.surfaceVariant
                    },
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        if (syncStatus is SyncStatusState.Syncing) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                color = industrialColors.accentPrimary,
                                strokeWidth = 2.dp
                            )
                        } else {
                            Icon(
                                imageVector = when (syncStatus) {
                                    is SyncStatusState.Success -> Icons.Default.CheckCircle
                                    is SyncStatusState.Error -> Icons.Default.Error
                                    else -> Icons.Default.CloudQueue
                                },
                                contentDescription = null,
                                tint = when (syncStatus) {
                                    is SyncStatusState.Success -> industrialColors.accentSuccess
                                    is SyncStatusState.Error -> industrialColors.accentError
                                    else -> industrialColors.accentPrimary
                                }
                            )
                        }

                        Column {
                            Text(
                                text = when (syncStatus) {
                                    is SyncStatusState.Syncing -> "Synchronizing..."
                                    is SyncStatusState.Success -> "Database in Sync"
                                    is SyncStatusState.Error -> "Sync Error"
                                    else -> "Ready to Sync"
                                },
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            )
                            Text(
                                text = when (syncStatus) {
                                    is SyncStatusState.Syncing -> "Fetching cloud records from remote database..."
                                    is SyncStatusState.Success -> "Last synced: ${(syncStatus as SyncStatusState.Success).message}"
                                    is SyncStatusState.Error -> (syncStatus as SyncStatusState.Error).errorMessage
                                    else -> "Local SQLite database active and ready."
                                },
                                style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                            )
                        }
                    }
                }

                // TAB 1: SUPABASE CONFIGURATION & ACTIONS
                if (selectedSource == CloudSourceTab.SUPABASE) {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Text(
                            text = "Supabase Project URL (Electro Hub):",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        )
                        OutlinedTextField(
                            value = inputSupaUrl,
                            onValueChange = {
                                inputSupaUrl = it
                                onSupabaseUrlChanged(it)
                            },
                            placeholder = { Text("https://your-project-ref.supabase.co", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 12.sp) },
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = MaterialTheme.colorScheme.primary,
                                unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                                focusedTextColor = MaterialTheme.colorScheme.onSurface,
                                unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                                focusedContainerColor = MaterialTheme.colorScheme.surface,
                                unfocusedContainerColor = MaterialTheme.colorScheme.surface
                            ),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        )

                        Text(
                            text = "Supabase Anon Public API Key:",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        )
                        OutlinedTextField(
                            value = inputSupaKey,
                            onValueChange = {
                                inputSupaKey = it
                                onSupabaseKeyChanged(it)
                            },
                            placeholder = { Text("eyJhbGciOiJIUzI1NiIsInR5cCI6...", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 12.sp) },
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = MaterialTheme.colorScheme.primary,
                                unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                                focusedTextColor = MaterialTheme.colorScheme.onSurface,
                                unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                                focusedContainerColor = MaterialTheme.colorScheme.surface,
                                unfocusedContainerColor = MaterialTheme.colorScheme.surface
                            ),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        )

                        // Action Buttons: Pull vs 1-Click Push Bulk Seed
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Button(
                                onClick = onSyncSupabaseTrigger,
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.primary,
                                    contentColor = MaterialTheme.colorScheme.onPrimary
                                ),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.weight(1f).height(48.dp)
                            ) {
                                Icon(Icons.Default.CloudDownload, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Pull / Read", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            }

                            Button(
                                onClick = onBulkPushSupabase,
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = industrialColors.accentSuccess,
                                    contentColor = Color.White
                                ),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.weight(1.3f).height(48.dp)
                            ) {
                                Icon(Icons.Default.CloudUpload, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("🚀 Push All Machines", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            }
                        }

                        // SQL Seed & Guide
                        Card(
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                            shape = RoundedCornerShape(10.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(14.dp),
                                verticalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        Icon(Icons.Default.Terminal, contentDescription = null, tint = industrialColors.accentSuccess, modifier = Modifier.size(18.dp))
                                        Text(
                                            text = "Supabase SQL Editor Method",
                                            style = MaterialTheme.typography.titleSmall.copy(
                                                fontWeight = FontWeight.Bold,
                                                color = MaterialTheme.colorScheme.onSurface
                                            )
                                        )
                                    }

                                    FilledTonalButton(
                                        onClick = {
                                            clipboardManager.setText(AnnotatedString(bulkSqlScript))
                                            Toast.makeText(context, "Full SQL script copied to clipboard!", Toast.LENGTH_SHORT).show()
                                        },
                                        colors = ButtonDefaults.filledTonalButtonColors(
                                            containerColor = industrialColors.tagSuccessBg,
                                            contentColor = industrialColors.accentSuccess
                                        ),
                                        shape = RoundedCornerShape(6.dp),
                                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                                        modifier = Modifier.height(30.dp)
                                    ) {
                                        Icon(Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(14.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("Copy SQL", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                    }
                                }

                                Text(
                                    text = "If you prefer running SQL directly in Supabase:\n" +
                                           "1. In Supabase 'Electro Hub', open 'SQL Editor' ➔ 'New Query'.\n" +
                                           "2. Click 'Copy SQL' above and paste it, then click 'Run'.\n" +
                                           "3. All 28+ machines with specifications will be instantly added into your Supabase database!",
                                    style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant, lineHeight = 18.sp)
                                )

                                Surface(
                                    color = Slate950,
                                    shape = RoundedCornerShape(6.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text(
                                        text = bulkSqlScript.take(450) + "\n... [Full 28 machine records included - Click 'Copy SQL' to get all]",
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            fontFamily = FontFamily.Monospace,
                                            fontSize = 11.sp,
                                            color = Color(0xFF80CBC4),
                                            lineHeight = 15.sp
                                        ),
                                        modifier = Modifier.padding(10.dp)
                                    )
                                }
                            }
                        }
                    }
                }

                // TAB 2: GOOGLE SHEETS CONFIGURATION
                if (selectedSource == CloudSourceTab.GOOGLE_SHEETS) {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text(
                            text = "Google Sheet ID or Public URL:",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        )
                        OutlinedTextField(
                            value = inputSheetId,
                            onValueChange = {
                                inputSheetId = it
                                onSheetIdChanged(it)
                            },
                            placeholder = { Text("e.g. 1BxiMVs0XRA5nFMdKvBdBZjgmUUqptlbs74OgvE2upms", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 12.sp) },
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = MaterialTheme.colorScheme.primary,
                                unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                                focusedTextColor = MaterialTheme.colorScheme.onSurface,
                                unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                                focusedContainerColor = MaterialTheme.colorScheme.surface,
                                unfocusedContainerColor = MaterialTheme.colorScheme.surface
                            ),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        )

                        Button(
                            onClick = onSyncSheetTrigger,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primary,
                                contentColor = MaterialTheme.colorScheme.onPrimary
                            ),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                        ) {
                            Icon(Icons.Default.Sync, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Sync Google Sheet Now", fontWeight = FontWeight.Bold)
                        }
                    }
                }

                // Reset Factory Defaults
                OutlinedButton(
                    onClick = onResetDefaults,
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.onSurfaceVariant),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth().height(44.dp)
                ) {
                    Text("Restore Factory Default Machinery Catalog (28 Machines)")
                }

                TextButton(
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Close", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }
    }
}
