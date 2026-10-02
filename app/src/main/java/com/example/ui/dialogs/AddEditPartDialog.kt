package com.example.ui.dialogs

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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.model.PartItem
import com.example.ui.theme.*

@Composable
fun AddEditPartDialog(
    initialPart: PartItem? = null,
    nextItemNo: Int = 1,
    onDismiss: () -> Unit,
    onSavePart: (PartItem) -> Unit
) {
    val isEditMode = initialPart != null

    var itemNoStr by remember { mutableStateOf((initialPart?.itemNo ?: nextItemNo).toString()) }
    var designation by remember { mutableStateOf(initialPart?.designation ?: "KM1") }
    var partNumber by remember { mutableStateOf(initialPart?.partNumber ?: "") }
    var manufacturer by remember { mutableStateOf(initialPart?.manufacturer ?: "") }
    var description by remember { mutableStateOf(initialPart?.description ?: "") }
    var specification by remember { mutableStateOf(initialPart?.specification ?: "") }
    var quantityStr by remember { mutableStateOf((initialPart?.quantity ?: 1).toString()) }
    var location by remember { mutableStateOf(initialPart?.location ?: "Main Cabinet") }

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
                                imageVector = if (isEditMode) Icons.Default.EditNote else Icons.Default.Inventory2,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }
                    Column {
                        Text(
                            text = if (isEditMode) "Edit Parts List Item (BOM)" else "Add Part to BOM List",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        )
                        Text(
                            text = "Electrical Component Bill of Materials",
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

                // Item # & Designation
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = itemNoStr,
                        onValueChange = { itemNoStr = it },
                        label = { Text("Item #") },
                        placeholder = { Text("1", color = Slate500) },
                        singleLine = true,
                        colors = customFieldColors(),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.weight(0.7f)
                    )

                    OutlinedTextField(
                        value = designation,
                        onValueChange = { designation = it.uppercase() },
                        label = { Text("Designation *") },
                        placeholder = { Text("e.g. QF1, KM1, INV1", color = Slate500) },
                        singleLine = true,
                        colors = customFieldColors(),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.weight(1.3f)
                    )
                }

                // Part Number & Manufacturer
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = partNumber,
                        onValueChange = { partNumber = it },
                        label = { Text("Part Number / Model *") },
                        placeholder = { Text("e.g. LC1D32BD", color = Slate500) },
                        singleLine = true,
                        colors = customFieldColors(),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.weight(1f)
                    )

                    OutlinedTextField(
                        value = manufacturer,
                        onValueChange = { manufacturer = it },
                        label = { Text("Manufacturer *") },
                        placeholder = { Text("e.g. Schneider, Danfoss", color = Slate500) },
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
                    label = { Text("Component Description *") },
                    placeholder = { Text("e.g. Main Motor Line Contactor", color = Slate500) },
                    singleLine = true,
                    colors = customFieldColors(),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                // Technical Specification
                OutlinedTextField(
                    value = specification,
                    onValueChange = { specification = it },
                    label = { Text("Technical Specifications") },
                    placeholder = { Text("e.g. 32A AC-3, 24VDC coil, 1NO+1NC", color = Slate500) },
                    singleLine = true,
                    colors = customFieldColors(),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                // Quantity & Panel Location
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = quantityStr,
                        onValueChange = { quantityStr = it },
                        label = { Text("Qty") },
                        placeholder = { Text("1", color = Slate500) },
                        singleLine = true,
                        colors = customFieldColors(),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.weight(0.7f)
                    )

                    OutlinedTextField(
                        value = location,
                        onValueChange = { location = it },
                        label = { Text("Cabinet Bay / Location") },
                        placeholder = { Text("e.g. Main Panel, Bay 2", color = Slate500) },
                        singleLine = true,
                        colors = customFieldColors(),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.weight(1.3f)
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
                            if (designation.isBlank() || partNumber.isBlank() || description.isBlank()) {
                                errorMessage = "Please enter Designation, Part Number, and Description."
                                return@Button
                            }

                            val num = itemNoStr.toIntOrNull() ?: nextItemNo
                            val qty = quantityStr.toIntOrNull() ?: 1

                            val part = PartItem(
                                itemNo = num,
                                designation = designation.trim(),
                                partNumber = partNumber.trim(),
                                manufacturer = manufacturer.trim().ifEmpty { "OEM" },
                                description = description.trim(),
                                specification = specification.trim().ifEmpty { "Standard" },
                                quantity = qty,
                                location = location.trim().ifEmpty { "Main Cabinet" }
                            )
                            onSavePart(part)
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
                        Text(if (isEditMode) "Save Changes" else "Add Part", fontWeight = FontWeight.Bold)
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
