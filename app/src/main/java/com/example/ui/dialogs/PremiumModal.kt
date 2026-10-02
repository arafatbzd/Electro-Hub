package com.example.ui.dialogs

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.ui.theme.*

@Composable
fun PremiumModal(
    isPremium: Boolean,
    onDismiss: () -> Unit,
    onToggleSimulator: () -> Unit,
    onActivateKey: (String) -> Boolean
) {
    var licenseInput by remember { mutableStateOf("") }
    var keyError by remember { mutableStateOf(false) }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(4.dp)
                .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(16.dp)),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Crown Header Icon
                Surface(
                    color = GoldPremium.copy(alpha = 0.2f),
                    shape = RoundedCornerShape(50),
                    modifier = Modifier.size(56.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.WorkspacePremium,
                            contentDescription = null,
                            tint = GoldPremium,
                            modifier = Modifier.size(32.dp)
                        )
                    }
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = if (isPremium) "Industrial Pro Active" else "Upgrade to Industrial Pro",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    )
                    Text(
                        text = "Full access to 30+ industrial machine drawings & interactive wire tracing",
                        style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant),
                        textAlign = TextAlign.Center
                    )
                }

                // Comparison Matrix
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(10.dp))
                        .padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    TierComparisonRow("Basic Technical Specs", free = true, pro = true)
                    TierComparisonRow("Machine Catalog (30 Units)", free = true, pro = true)
                    TierComparisonRow("Full 24+ Page Schematics", free = false, pro = true)
                    TierComparisonRow("Interactive Wire Net Tracer", free = false, pro = true)
                    TierComparisonRow("PLC I/O & Terminal Maps", free = false, pro = true)
                    TierComparisonRow("Excel / CSV Data Sheet Export", free = false, pro = true)
                    TierComparisonRow("Offline Factory Cache Pack", free = false, pro = true)
                }

                // Developer / Testing Instant Simulator Switch
                Surface(
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                text = "Pro Simulator / Demo Mode",
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            )
                            Text(
                                text = "Instantly toggle Premium on/off for testing",
                                style = MaterialTheme.typography.labelSmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                            )
                        }
                        Switch(
                            checked = isPremium,
                            onCheckedChange = { onToggleSimulator() },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = GoldPremium,
                                checkedTrackColor = GoldPremiumDark.copy(alpha = 0.5f)
                            )
                        )
                    }
                }

                // License Key Input
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = "Or enter License Key (Demo: INDUS-PRO-2026):",
                        style = MaterialTheme.typography.labelSmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = licenseInput,
                            onValueChange = {
                                licenseInput = it
                                keyError = false
                            },
                            placeholder = { Text("INDUS-PRO-2026", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 12.sp) },
                            singleLine = true,
                            isError = keyError,
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(8.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = MaterialTheme.colorScheme.primary,
                                unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                                focusedTextColor = MaterialTheme.colorScheme.onSurface,
                                unfocusedTextColor = MaterialTheme.colorScheme.onSurface
                            )
                        )
                        Button(
                            onClick = {
                                val success = onActivateKey(licenseInput)
                                if (!success) keyError = true
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primary,
                                contentColor = MaterialTheme.colorScheme.onPrimary
                            ),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.height(52.dp)
                        ) {
                            Text("Activate", fontWeight = FontWeight.Bold)
                        }
                    }
                    if (keyError) {
                        Text(
                            text = "Invalid key. Use promo code INDUS-PRO-2026 or use switch above.",
                            style = MaterialTheme.typography.labelSmall.copy(color = EmergencyRed)
                        )
                    }
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

@Composable
private fun TierComparisonRow(feature: String, free: Boolean, pro: Boolean) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = feature,
            style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurface),
            modifier = Modifier.weight(1f)
        )
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            Text(
                text = if (free) "✓ Free" else "✕ Lock",
                style = MaterialTheme.typography.labelSmall.copy(
                    fontFamily = FontFamily.Monospace,
                    color = if (free) MachineGreen else Slate500
                ),
                modifier = Modifier.width(55.dp)
            )
            Text(
                text = "✓ Pro",
                style = MaterialTheme.typography.labelSmall.copy(
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    color = GoldPremium
                ),
                modifier = Modifier.width(45.dp)
            )
        }
    }
}
