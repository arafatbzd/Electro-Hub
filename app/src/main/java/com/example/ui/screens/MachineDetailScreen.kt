package com.example.ui.screens

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Machine
import com.example.data.repository.SyncStatusState
import com.example.ui.components.*
import com.example.ui.theme.*
import com.example.ui.viewmodel.DetailTab

@Composable
fun MachineDetailScreen(
    machine: Machine,
    syncStatus: SyncStatusState,
    selectedTab: DetailTab,
    currentSchematicPage: Int,
    activeTracedWireId: String?,
    onBack: () -> Unit,
    onTabSelected: (DetailTab) -> Unit,
    onSchematicPageSelected: (Int) -> Unit,
    onWireTraceSelected: (String?) -> Unit,
    onBookmarkToggle: () -> Unit,
    onSyncClick: () -> Unit,
    onMenuClick: () -> Unit = {},
    isEditModeEnabled: Boolean = false,
    isDarkTheme: Boolean = true,
    onToggleTheme: () -> Unit = {},
    onEditMachineClick: () -> Unit = {},
    onDeleteMachineClick: () -> Unit = {},
    onAddPlcIOClick: () -> Unit = {},
    onEditPlcIOClick: (com.example.data.model.PlcIOItem) -> Unit = {},
    onDeletePlcIOClick: (String) -> Unit = {},
    onAddPartClick: () -> Unit = {},
    onEditPartClick: (com.example.data.model.PartItem) -> Unit = {},
    onDeletePartClick: (Int) -> Unit = {}
) {
    BackHandler {
        onBack()
    }

    Scaffold(
        topBar = {
            IndustrialTopBar(
                title = machine.name,
                syncStatus = syncStatus,
                onSyncClick = onSyncClick,
                onMenuClick = onMenuClick,
                onBackClick = onBack,
                isEditModeEnabled = isEditModeEnabled,
                isDarkTheme = isDarkTheme,
                onToggleTheme = onToggleTheme
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(innerPadding)
        ) {
            // Machine Profile Banner
            Surface(
                color = MaterialTheme.colorScheme.surface,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 10.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "${machine.model} — ${machine.manufacturer}",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            )
                            Text(
                                text = "PLC: ${machine.plcModel}  •  VFD: ${machine.inverterModel}",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontFamily = FontFamily.Monospace,
                                    color = MaterialTheme.colorScheme.primary
                                ),
                                maxLines = 1
                            )
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            if (isEditModeEnabled) {
                                IconButton(onClick = onEditMachineClick) {
                                    Icon(
                                        imageVector = Icons.Default.Edit,
                                        contentDescription = "Edit Machine Specs",
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                }
                                IconButton(onClick = onDeleteMachineClick) {
                                    Icon(
                                        imageVector = Icons.Default.DeleteOutline,
                                        contentDescription = "Delete Machine",
                                        tint = EmergencyRed
                                    )
                                }
                            }
                            IconButton(onClick = onBookmarkToggle) {
                                Icon(
                                    imageVector = if (machine.isBookmarked) Icons.Default.Bookmark else Icons.Outlined.BookmarkBorder,
                                    contentDescription = "Bookmark",
                                    tint = if (machine.isBookmarked) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }

                    // Tab Navigation Row (Hierarchical Multi-sheet profile)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        DetailTab.values().forEach { tab ->
                            val isSelected = selectedTab == tab
                            FilterChip(
                                selected = isSelected,
                                onClick = { onTabSelected(tab) },
                                label = {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                                    ) {
                                        Icon(
                                            imageVector = when (tab) {
                                                DetailTab.SPECS -> Icons.Default.Info
                                                DetailTab.SCHEMATICS -> Icons.Default.Timeline
                                                DetailTab.PLC_IO -> Icons.Default.Memory
                                                DetailTab.PARTS_BOM -> Icons.Default.FormatListBulleted
                                                DetailTab.TERMINALS -> Icons.Default.Cable
                                                DetailTab.SHEET_SYNC -> Icons.Default.TableChart
                                            },
                                            contentDescription = null,
                                            modifier = Modifier.size(14.dp)
                                        )
                                        Text(
                                            text = tab.title,
                                            fontSize = 12.sp,
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                        )
                                    }
                                },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = MaterialTheme.colorScheme.primary,
                                    selectedLabelColor = MaterialTheme.colorScheme.onPrimary,
                                    containerColor = MaterialTheme.colorScheme.surfaceVariant,
                                    labelColor = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            )
                        }
                    }
                }
            }

            // Tab Content
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                when (selectedTab) {
                    DetailTab.SPECS -> {
                        SpecsTabContent(machine = machine)
                    }

                    DetailTab.SCHEMATICS -> {
                        val totalPages = machine.schematicPages.size.coerceAtLeast(1)
                        val safePageNum = currentSchematicPage.coerceIn(1, totalPages)
                        val page = machine.schematicPages.getOrElse(safePageNum - 1) {
                            machine.schematicPages.first()
                        }

                        InteractiveSchematicCanvas(
                            page = page,
                            totalPages = totalPages,
                            currentPageNumber = safePageNum,
                            activeTracedWireId = activeTracedWireId,
                            onPageSelected = onSchematicPageSelected,
                            onWireTraceSelected = onWireTraceSelected
                        )
                    }

                    DetailTab.PLC_IO -> {
                        PlcIoTable(
                            ioList = machine.plcIOList,
                            onAddClick = if (isEditModeEnabled) onAddPlcIOClick else null,
                            onEditClick = if (isEditModeEnabled) onEditPlcIOClick else null,
                            onDeleteClick = if (isEditModeEnabled) onDeletePlcIOClick else null
                        )
                    }

                    DetailTab.PARTS_BOM -> {
                        PartsListTable(
                            partsList = machine.partsList,
                            onAddClick = if (isEditModeEnabled) onAddPartClick else null,
                            onEditClick = if (isEditModeEnabled) onEditPartClick else null,
                            onDeleteClick = if (isEditModeEnabled) onDeletePartClick else null
                        )
                    }

                    DetailTab.TERMINALS -> {
                        TerminalsAndWireView(
                            wireGauges = machine.wireGauges,
                            encoderSpecs = machine.encoderSpecs,
                            terminalBlocks = machine.terminalBlocks
                        )
                    }

                    DetailTab.SHEET_SYNC -> {
                        SheetSyncTabContent(
                            machine = machine,
                            onSyncClick = onSyncClick
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SpecsTabContent(machine: Machine) {
    val industrialColors = LocalIndustrialColors.current

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Machine Description Card
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(12.dp))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "Machine Overview & Application",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    )
                    Text(
                        text = machine.description,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            lineHeight = 22.sp
                        )
                    )
                }
            }
        }

        // Ratings & Electrical Key Parameters Grid
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(12.dp))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "Electrical & Control Ratings",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        SpecRatingBox("Power Rating", machine.powerRating, industrialColors.accentPrimary, Modifier.weight(1f))
                        SpecRatingBox("Supply Voltage", machine.supplyVoltage, MaterialTheme.colorScheme.onSurface, Modifier.weight(1f))
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        SpecRatingBox("Control Bus", machine.controlVoltage, industrialColors.accentWarning, Modifier.weight(1f))
                        SpecRatingBox("Full Load Current", machine.fullLoadCurrent, industrialColors.accentSuccess, Modifier.weight(1f))
                    }
                }
            }
        }

        // Complete Specs List Table
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(12.dp))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = "Technical Specification Schedule",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    )

                    machine.specs.forEach { spec ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(6.dp))
                                .padding(horizontal = 12.dp, vertical = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = spec.name,
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        fontWeight = FontWeight.SemiBold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                )
                                Text(
                                    text = spec.category,
                                    style = MaterialTheme.typography.labelSmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                                )
                            }
                            Text(
                                text = "${spec.value} ${spec.unit}".trim(),
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SpecRatingBox(label: String, value: String, valueColor: Color, modifier: Modifier = Modifier) {
    Surface(
        color = MaterialTheme.colorScheme.surfaceVariant,
        shape = RoundedCornerShape(8.dp),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = label.uppercase(),
                style = MaterialTheme.typography.labelSmall.copy(
                    fontSize = 9.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    letterSpacing = 0.5.sp
                )
            )
            Text(
                text = value,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    color = valueColor
                )
            )
        }
    }
}

@Composable
private fun SheetSyncTabContent(
    machine: Machine,
    onSyncClick: () -> Unit
) {
    val context = LocalContext.current
    val industrialColors = LocalIndustrialColors.current
    var isExported by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Card(
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(12.dp))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(Icons.Default.CloudDone, contentDescription = null, tint = MachineGreen)
                    Text(
                        text = "Google Sheet Cloud Integration",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    )
                }

                Text(
                    text = "This machine's technical specifications, terminal pins, and parts catalog are synchronized with the central Google Sheets master database.",
                    style = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Machine ID: ${machine.id}",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontFamily = FontFamily.Monospace,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    )
                    Button(
                        onClick = onSyncClick,
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary, contentColor = MaterialTheme.colorScheme.onPrimary),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Icon(Icons.Default.Sync, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Sync Latest Sheet")
                    }
                }
            }
        }

        // Export Data Sheet Section
        Card(
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(12.dp))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(Icons.Default.FileDownload, contentDescription = null, tint = MachineGreen)
                    Text(
                        text = "Export Machine Technical Datasheet",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    )
                }
                Text(
                    text = "Export Excel/CSV data sheets for the complete Bill of Materials (BOM), PLC I/O mapping, and terminal pins.",
                    style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                )

                Button(
                    onClick = {
                        isExported = true
                        Toast.makeText(context, "Datasheet for ${machine.name} ready & exported!", Toast.LENGTH_SHORT).show()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = industrialColors.accentSuccess, contentColor = Color.White),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth().height(48.dp)
                ) {
                    Icon(Icons.Default.Download, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (isExported) "✓ Exported (${machine.partsList.size} parts, ${machine.plcIOList.size} I/O)" else "Export CSV Datasheet (${machine.partsList.size} parts, ${machine.plcIOList.size} I/O)",
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
