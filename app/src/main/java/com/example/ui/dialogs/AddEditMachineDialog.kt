package com.example.ui.dialogs

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.model.*
import com.example.ui.theme.*

@Composable
fun AddEditMachineDialog(
    initialMachine: Machine? = null,
    onDismiss: () -> Unit,
    onSaveMachine: (Machine) -> Unit
) {
    val isEditMode = initialMachine != null

    var model by remember { mutableStateOf(initialMachine?.model ?: "") }
    var name by remember { mutableStateOf(initialMachine?.name ?: "") }
    var manufacturer by remember { mutableStateOf(initialMachine?.manufacturer ?: "") }
    var category by remember { mutableStateOf(initialMachine?.category ?: MachineCategory.TEXTILE) }
    var powerRating by remember { mutableStateOf(initialMachine?.powerRating ?: "15 kW") }
    var supplyVoltage by remember { mutableStateOf(initialMachine?.supplyVoltage ?: "380V AC 3-Phase") }
    var controlVoltage by remember { mutableStateOf(initialMachine?.controlVoltage ?: "24V DC") }
    var fullLoadCurrent by remember { mutableStateOf(initialMachine?.fullLoadCurrent ?: "30.0 A") }
    var plcModel by remember { mutableStateOf(initialMachine?.plcModel ?: "Mitsubishi FX5U") }
    var inverterModel by remember { mutableStateOf(initialMachine?.inverterModel ?: "Danfoss FC302") }
    var description by remember { mutableStateOf(initialMachine?.description ?: "") }

    var errorMessage by remember { mutableStateOf<String?>(null) }

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
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Header
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Surface(
                        color = ElectricCyanDim,
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.size(40.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = if (isEditMode) Icons.Default.EditNote else Icons.Default.AddCircle,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }
                    Column {
                        Text(
                            text = if (isEditMode) "Edit Machine Specs" else "Add New Industrial Machine",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        )
                        Text(
                            text = "Syncs with Supabase (Electro Hub) & Local Database",
                            style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                        )
                    }
                }

                if (errorMessage != null) {
                    Surface(
                        color = EmergencyRedDim,
                        shape = RoundedCornerShape(6.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = errorMessage ?: "",
                            style = MaterialTheme.typography.bodySmall.copy(color = EmergencyRed),
                            modifier = Modifier.padding(10.dp)
                        )
                    }
                }

                // Category Chips Selector
                Text(
                    text = "MACHINE CATEGORY:",
                    style = MaterialTheme.typography.labelSmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant, fontWeight = FontWeight.Bold)
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    MachineCategory.values().forEach { cat ->
                        FilterChip(
                            selected = category == cat,
                            onClick = { category = cat },
                            label = { Text(cat.label, fontSize = 11.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MaterialTheme.colorScheme.primary,
                                selectedLabelColor = MaterialTheme.colorScheme.onPrimary,
                                containerColor = MaterialTheme.colorScheme.surfaceVariant,
                                labelColor = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        )
                    }
                }

                // Model & Name
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = model,
                        onValueChange = { model = it },
                        label = { Text("Model Code *") },
                        placeholder = { Text("e.g. FA494", color = Slate500) },
                        singleLine = true,
                        colors = customFieldColors(),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.weight(1f)
                    )

                    OutlinedTextField(
                        value = manufacturer,
                        onValueChange = { manufacturer = it },
                        label = { Text("Manufacturer *") },
                        placeholder = { Text("e.g. Jingwei", color = Slate500) },
                        singleLine = true,
                        colors = customFieldColors(),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.weight(1f)
                    )
                }

                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Full Machine Name *") },
                    placeholder = { Text("e.g. FA494 High-Speed Roving Frame", color = Slate500) },
                    singleLine = true,
                    colors = customFieldColors(),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                // Electrical Ratings
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = powerRating,
                        onValueChange = { powerRating = it },
                        label = { Text("Power Rating") },
                        placeholder = { Text("18.5 kW", color = Slate500) },
                        singleLine = true,
                        colors = customFieldColors(),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.weight(1f)
                    )

                    OutlinedTextField(
                        value = supplyVoltage,
                        onValueChange = { supplyVoltage = it },
                        label = { Text("Supply Voltage") },
                        placeholder = { Text("380V AC", color = Slate500) },
                        singleLine = true,
                        colors = customFieldColors(),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.weight(1f)
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = controlVoltage,
                        onValueChange = { controlVoltage = it },
                        label = { Text("Control Voltage") },
                        placeholder = { Text("24V DC", color = Slate500) },
                        singleLine = true,
                        colors = customFieldColors(),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.weight(1f)
                    )

                    OutlinedTextField(
                        value = fullLoadCurrent,
                        onValueChange = { fullLoadCurrent = it },
                        label = { Text("FLC (Current)") },
                        placeholder = { Text("42.0 A", color = Slate500) },
                        singleLine = true,
                        colors = customFieldColors(),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.weight(1f)
                    )
                }

                // Automation Components
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = plcModel,
                        onValueChange = { plcModel = it },
                        label = { Text("PLC Controller") },
                        placeholder = { Text("Mitsubishi FX5U", color = Slate500) },
                        singleLine = true,
                        colors = customFieldColors(),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.weight(1f)
                    )

                    OutlinedTextField(
                        value = inverterModel,
                        onValueChange = { inverterModel = it },
                        label = { Text("Inverter / VFD") },
                        placeholder = { Text("Danfoss FC302", color = Slate500) },
                        singleLine = true,
                        colors = customFieldColors(),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.weight(1f)
                    )
                }

                // Description
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description & Technical Summary") },
                    placeholder = { Text("Enter drafting ratio, speed specifications, and operating parameters...", color = Slate500) },
                    minLines = 3,
                    maxLines = 5,
                    colors = customFieldColors(),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(6.dp))

                // Actions
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.onSurfaceVariant),
                        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.weight(1f).height(48.dp)
                    ) {
                        Text("Cancel")
                    }

                    Button(
                        onClick = {
                            if (model.isBlank() || name.isBlank() || manufacturer.isBlank()) {
                                errorMessage = "Please enter Model, Machine Name, and Manufacturer."
                                return@Button
                            }

                            val machineId = initialMachine?.id ?: "MACH-${model.trim().replace(" ", "-")}-${System.currentTimeMillis() % 10000}"

                            // Inherit schematic pages or create default 24 pages
                            val pages = initialMachine?.schematicPages ?: (1..24).map { p ->
                                SchematicPage(
                                    pageNumber = p,
                                    title = "Page $p: $name Schematic",
                                    dwgCode = "$model-DWG-$p",
                                    description = "Schematic diagram for $name ($model)",
                                    components = listOf(
                                        SchematicComponent("CMP1", name, model, 320f, 160f, 220f, 150f, listOf("L1", "L2", "L3", "24V", "0V"))
                                    ),
                                    wireTraces = listOf(
                                        WireTrace("L1", "Line 1 Phase", 0xFFFF5722, "380V AC", listOf(SchematicPoint(40f, 170f), SchematicPoint(320f, 170f))),
                                        WireTrace("+24V", "24V DC Bus", 0xFF00E5FF, "24V DC", listOf(SchematicPoint(40f, 230f), SchematicPoint(320f, 230f)))
                                    )
                                )
                            }

                            val savedMachine = Machine(
                                id = machineId,
                                name = name.trim(),
                                model = model.trim(),
                                manufacturer = manufacturer.trim(),
                                category = category,
                                powerRating = powerRating.trim(),
                                supplyVoltage = supplyVoltage.trim(),
                                controlVoltage = controlVoltage.trim(),
                                fullLoadCurrent = fullLoadCurrent.trim(),
                                plcModel = plcModel.trim(),
                                inverterModel = inverterModel.trim(),
                                description = description.trim().ifEmpty { "Industrial documentation for $name" },
                                isPremium = false,
                                isBookmarked = initialMachine?.isBookmarked ?: false,
                                specs = initialMachine?.specs ?: listOf(
                                    SpecItem("General", "Model Code", model),
                                    SpecItem("General", "Manufacturer", manufacturer),
                                    SpecItem("Electrical", "Supply Voltage", supplyVoltage),
                                    SpecItem("Ratings", "Power Rating", powerRating)
                                ),
                                plcIOList = initialMachine?.plcIOList ?: listOf(
                                    PlcIOItem("X0", "TB2-01", "COM0 (24V+)", PlcIOType.DIGITAL_INPUT, "E-Stop Safety Relay", "Pilz PNOZ", "W101", "NC"),
                                    PlcIOItem("Y0", "TB3-01", "COM1 (24V+)", PlcIOType.DIGITAL_OUTPUT, "Main Contactor Enable", "KM1", "W201", "NO")
                                ),
                                partsList = initialMachine?.partsList ?: listOf(
                                    PartItem(1, "QF1", "MCCB-63A", manufacturer, "Main Molded Case Breaker", "63A 3P", 1, "Cabinet Bay 1")
                                ),
                                schematicPages = pages,
                                wireGauges = initialMachine?.wireGauges ?: listOf(WireGaugeItem("Mains Line", "10 mm²", "Black/Brown", "Standard")),
                                encoderSpecs = initialMachine?.encoderSpecs ?: emptyList(),
                                terminalBlocks = initialMachine?.terminalBlocks ?: emptyList()
                            )

                            onSaveMachine(savedMachine)
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.onPrimary
                        ),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.weight(1f).height(48.dp)
                    ) {
                        Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(if (isEditMode) "Save Changes" else "Add Machine", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
private fun customFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedBorderColor = MaterialTheme.colorScheme.primary,
    unfocusedBorderColor = MaterialTheme.colorScheme.outline,
    focusedTextColor = MaterialTheme.colorScheme.onSurface,
    unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
    focusedLabelColor = MaterialTheme.colorScheme.primary,
    unfocusedLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
    focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
    unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant
)
