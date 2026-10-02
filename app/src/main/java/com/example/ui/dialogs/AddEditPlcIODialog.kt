package com.example.ui.dialogs

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
import com.example.data.model.PlcIOItem
import com.example.data.model.PlcIOType
import com.example.ui.theme.*

@Composable
fun AddEditPlcIODialog(
    initialItem: PlcIOItem? = null,
    onDismiss: () -> Unit,
    onSavePlcIO: (PlcIOItem) -> Unit
) {
    val isEditMode = initialItem != null

    var address by remember { mutableStateOf(initialItem?.address ?: "X0") }
    var terminalPin by remember { mutableStateOf(initialItem?.terminalPin ?: "TB2-01") }
    var comPort by remember { mutableStateOf(initialItem?.comPort ?: "COM0 (24V+)") }
    var type by remember { mutableStateOf(initialItem?.type ?: PlcIOType.DIGITAL_INPUT) }
    var signalName by remember { mutableStateOf(initialItem?.signalName ?: "") }
    var device by remember { mutableStateOf(initialItem?.device ?: "") }
    var wireTag by remember { mutableStateOf(initialItem?.wireTag ?: "W101") }
    var normallyState by remember { mutableStateOf(initialItem?.normallyState ?: "NO") }

    var errorMessage by remember { mutableStateOf<String?>(null) }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.9f)
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
                                imageVector = if (isEditMode) Icons.Default.EditNote else Icons.Default.SettingsInputComponent,
                                contentDescription = null,
                                tint = ElectricCyan,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }
                    Column {
                        Text(
                            text = if (isEditMode) "Edit PLC I/O Channel" else "Add PLC I/O Channel",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        )
                        Text(
                            text = "Live mapping to PLC & Supabase (Electro Hub)",
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

                // Channel Type Selector
                Text(
                    text = "I/O CHANNEL TYPE:",
                    style = MaterialTheme.typography.labelSmall.copy(color = Slate400, fontWeight = FontWeight.Bold)
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    PlcIOType.values().forEach { t ->
                        FilterChip(
                            selected = type == t,
                            onClick = { type = t },
                            label = { Text("${t.shortLabel} (${t.name})", fontSize = 11.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = if (t.isInput) ElectricCyan else SafetyAmber,
                                selectedLabelColor = Slate950,
                                containerColor = Slate800,
                                labelColor = Slate300
                            )
                        )
                    }
                }

                // Address & Terminal
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = address,
                        onValueChange = { address = it.uppercase() },
                        label = { Text("Address *") },
                        placeholder = { Text("e.g. X0, Y0, %IX0.0", color = Slate500) },
                        singleLine = true,
                        colors = customFieldColors(),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.weight(1f)
                    )

                    OutlinedTextField(
                        value = terminalPin,
                        onValueChange = { terminalPin = it },
                        label = { Text("Terminal Block Pin *") },
                        placeholder = { Text("e.g. TB2-01", color = Slate500) },
                        singleLine = true,
                        colors = customFieldColors(),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.weight(1f)
                    )
                }

                // Signal Name
                OutlinedTextField(
                    value = signalName,
                    onValueChange = { signalName = it },
                    label = { Text("Signal Description *") },
                    placeholder = { Text("e.g. Emergency Stop Status, Spindle Inverter Run", color = Slate500) },
                    singleLine = true,
                    colors = customFieldColors(),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                // Connected Device & Wire Tag
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = device,
                        onValueChange = { device = it },
                        label = { Text("Connected Device / Sensor") },
                        placeholder = { Text("e.g. Pilz Safety Relay", color = Slate500) },
                        singleLine = true,
                        colors = customFieldColors(),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.weight(1f)
                    )

                    OutlinedTextField(
                        value = wireTag,
                        onValueChange = { wireTag = it },
                        label = { Text("Wire Ferrule Tag") },
                        placeholder = { Text("e.g. W101", color = Slate500) },
                        singleLine = true,
                        colors = customFieldColors(),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.weight(1f)
                    )
                }

                // Common Bus & Logic State
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = comPort,
                        onValueChange = { comPort = it },
                        label = { Text("Common Port / Bus") },
                        placeholder = { Text("COM0 (24V+)", color = Slate500) },
                        singleLine = true,
                        colors = customFieldColors(),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.weight(1f)
                    )

                    OutlinedTextField(
                        value = normallyState,
                        onValueChange = { normallyState = it },
                        label = { Text("Logic State / Range") },
                        placeholder = { Text("NO, NC, 4-20mA", color = Slate500) },
                        singleLine = true,
                        colors = customFieldColors(),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                // Actions
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Slate300),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.weight(1f).height(48.dp)
                    ) {
                        Text("Cancel")
                    }

                    Button(
                        onClick = {
                            if (address.isBlank() || signalName.isBlank() || terminalPin.isBlank()) {
                                errorMessage = "Please enter Address, Terminal Pin, and Signal Description."
                                return@Button
                            }

                            val item = PlcIOItem(
                                address = address.trim(),
                                terminalPin = terminalPin.trim(),
                                comPort = comPort.trim(),
                                type = type,
                                signalName = signalName.trim(),
                                device = device.trim().ifEmpty { "Field Device" },
                                wireTag = wireTag.trim().ifEmpty { "W-" + address.trim() },
                                normallyState = normallyState.trim()
                            )
                            onSavePlcIO(item)
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
                        Text(if (isEditMode) "Save Changes" else "Add Point", fontWeight = FontWeight.Bold)
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
    focusedContainerColor = MaterialTheme.colorScheme.surface,
    unfocusedContainerColor = MaterialTheme.colorScheme.surface
)
