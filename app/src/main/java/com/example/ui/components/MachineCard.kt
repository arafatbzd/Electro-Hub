package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Machine
import com.example.data.model.MachineCategory
import com.example.ui.theme.*

@Composable
fun MachineCard(
    machine: Machine,
    onMachineClick: () -> Unit,
    onBookmarkToggle: () -> Unit,
    modifier: Modifier = Modifier
) {
    val industrialColors = LocalIndustrialColors.current
    val isDark = LocalIsDarkTheme.current

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .clickable { onMachineClick() }
            .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(12.dp)),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Category & Status Header Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    color = when (machine.category) {
                        MachineCategory.TEXTILE -> industrialColors.tagPrimaryBg
                        MachineCategory.INVERTER_DRIVE -> if (isDark) ElectricCyanDim else Color(0xFFE0F2FE)
                        MachineCategory.PLC_PANEL -> industrialColors.tagWarningBg
                        MachineCategory.WEAVING -> industrialColors.tagSuccessBg
                        MachineCategory.SENSORS_IO -> if (isDark) Color(0xFF9C27B0).copy(alpha = 0.2f) else Color(0xFFF3E8FF)
                    },
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = when (machine.category) {
                                MachineCategory.TEXTILE -> Icons.Default.PrecisionManufacturing
                                MachineCategory.INVERTER_DRIVE -> Icons.Default.Speed
                                MachineCategory.PLC_PANEL -> Icons.Default.Memory
                                MachineCategory.WEAVING -> Icons.Default.GridOn
                                MachineCategory.SENSORS_IO -> Icons.Default.Sensors
                            },
                            contentDescription = null,
                            tint = when (machine.category) {
                                MachineCategory.TEXTILE -> industrialColors.accentPrimary
                                MachineCategory.INVERTER_DRIVE -> industrialColors.accentPrimary
                                MachineCategory.PLC_PANEL -> industrialColors.accentWarning
                                MachineCategory.WEAVING -> industrialColors.accentSuccess
                                MachineCategory.SENSORS_IO -> if (isDark) Color(0xFFBA68C8) else Color(0xFF7E22CE)
                            },
                            modifier = Modifier.size(14.dp)
                        )
                        Text(
                            text = machine.category.label,
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        )
                    }
                }

                IconButton(
                    onClick = onBookmarkToggle,
                    modifier = Modifier.size(28.dp)
                ) {
                    Icon(
                        imageVector = if (machine.isBookmarked) Icons.Default.Bookmark else Icons.Outlined.BookmarkBorder,
                        contentDescription = "Bookmark",
                        tint = if (machine.isBookmarked) industrialColors.accentPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            // Machine Title & Model
            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(
                    text = machine.name,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    ),
                    maxLines = 1
                )
                Text(
                    text = "${machine.model}  •  ${machine.manufacturer}",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                )
            }

            // Technical Specs Highlights Grid
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(8.dp))
                    .padding(8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "POWER",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontSize = 9.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            letterSpacing = 0.5.sp
                        )
                    )
                    Text(
                        text = machine.powerRating,
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            color = industrialColors.accentPrimary
                        )
                    )
                }

                Column {
                    Text(
                        text = "VOLTAGE",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontSize = 9.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            letterSpacing = 0.5.sp
                        )
                    )
                    Text(
                        text = machine.supplyVoltage.take(12),
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    )
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "SCHEMATICS",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontSize = 9.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            letterSpacing = 0.5.sp
                        )
                    )
                    Text(
                        text = "${machine.schematicPages.size} Pgs (CAD)",
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            color = industrialColors.accentWarning
                        )
                    )
                }
            }

            // Footer Chips (PLC, Inverter, I/O count)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Surface(
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Text(
                            text = machine.plcModel.take(20),
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontSize = 10.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            ),
                            maxLines = 1,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }

                    if (machine.plcIOList.isNotEmpty()) {
                        Surface(
                            color = industrialColors.tagPrimaryBg,
                            shape = RoundedCornerShape(4.dp)
                        ) {
                            Text(
                                text = "${machine.plcIOList.size} I/O pts",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = industrialColors.accentPrimary
                                ),
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    Text(
                        text = "View",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = industrialColors.accentPrimary
                        )
                    )
                    Icon(
                        imageVector = Icons.Default.ArrowForward,
                        contentDescription = null,
                        tint = industrialColors.accentPrimary,
                        modifier = Modifier.size(14.dp)
                    )
                }
            }
        }
    }
}
