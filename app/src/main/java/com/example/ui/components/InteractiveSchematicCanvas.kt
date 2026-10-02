package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.SchematicComponent
import com.example.data.model.SchematicPage
import com.example.data.model.WireTrace
import com.example.ui.theme.*

@Composable
fun InteractiveSchematicCanvas(
    page: SchematicPage,
    totalPages: Int,
    currentPageNumber: Int,
    activeTracedWireId: String?,
    onPageSelected: (Int) -> Unit,
    onWireTraceSelected: (String?) -> Unit,
    modifier: Modifier = Modifier
) {
    var scale by remember { mutableStateOf(1f) }
    var offsetX by remember { mutableStateOf(0f) }
    var offsetY by remember { mutableStateOf(0f) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Page Navigation & Drawing Title Bar
        Surface(
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 2.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = page.title,
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            ),
                            maxLines = 1
                        )
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = "DWG: ${page.dwgCode}",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontFamily = FontFamily.Monospace,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            )
                            Text(
                                text = "Page $currentPageNumber of $totalPages",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            )
                        }
                    }

                    // Page Navigation Buttons
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(
                            onClick = { if (currentPageNumber > 1) onPageSelected(currentPageNumber - 1) },
                            enabled = currentPageNumber > 1,
                            modifier = Modifier.size(36.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.ChevronLeft,
                                contentDescription = "Previous Page",
                                tint = if (currentPageNumber > 1) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.outline
                            )
                        }

                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant,
                            modifier = Modifier.padding(horizontal = 4.dp)
                        ) {
                            Text(
                                text = "P.$currentPageNumber",
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            )
                        }

                        IconButton(
                            onClick = { if (currentPageNumber < totalPages) onPageSelected(currentPageNumber + 1) },
                            enabled = currentPageNumber < totalPages,
                            modifier = Modifier.size(36.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.ChevronRight,
                                contentDescription = "Next Page",
                                tint = if (currentPageNumber < totalPages) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.outline
                            )
                        }
                    }
                }

                // Horizontal Page Selector Pills
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 6.dp)
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    (1..totalPages).forEach { pNum ->
                        val isCurrent = pNum == currentPageNumber
                        FilterChip(
                            selected = isCurrent,
                            onClick = { onPageSelected(pNum) },
                            label = { Text("P$pNum", fontSize = 11.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MaterialTheme.colorScheme.primary,
                                selectedLabelColor = MaterialTheme.colorScheme.onPrimary,
                                containerColor = MaterialTheme.colorScheme.surfaceVariant,
                                labelColor = MaterialTheme.colorScheme.onSurfaceVariant
                            ),
                            modifier = Modifier.height(28.dp)
                        )
                    }
                }
            }
        }

        // Wire Net Tracing Filter Strip (Industrial Feature - 100% Unlocked)
        Surface(
            color = MaterialTheme.colorScheme.surfaceVariant,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 6.dp)
                    .horizontalScroll(rememberScrollState()),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.ElectricBolt,
                    contentDescription = "Wire Tracing",
                    tint = SafetyAmber,
                    modifier = Modifier.size(16.dp)
                )
                Text(
                    text = "Wire Tracer:",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = SafetyAmber
                    )
                )

                AssistChip(
                    onClick = { onWireTraceSelected(null) },
                    label = { Text("All Nets", fontSize = 11.sp) },
                    colors = AssistChipDefaults.assistChipColors(
                        containerColor = if (activeTracedWireId == null) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                        labelColor = if (activeTracedWireId == null) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                    ),
                    modifier = Modifier.height(26.dp)
                )

                page.wireTraces.forEach { wire ->
                    val isTraced = activeTracedWireId == wire.id
                    val traceColor = Color(wire.colorHex)
                    FilterChip(
                        selected = isTraced,
                        onClick = { onWireTraceSelected(wire.id) },
                        label = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(8.dp)
                                        .background(traceColor, RoundedCornerShape(50))
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "${wire.id} (${wire.voltageLevel})",
                                    fontSize = 11.sp,
                                    fontWeight = if (isTraced) FontWeight.Bold else FontWeight.Normal
                                )
                            }
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = traceColor.copy(alpha = 0.35f),
                            selectedLabelColor = MaterialTheme.colorScheme.onSurface,
                            containerColor = MaterialTheme.colorScheme.surfaceVariant,
                            labelColor = MaterialTheme.colorScheme.onSurfaceVariant
                        ),
                        modifier = Modifier.height(26.dp)
                    )
                }
            }
        }

        // Drawing Canvas with Pan & Zoom & Grid (Direct Crystal-Clear Access)
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .clip(RoundedCornerShape(0.dp))
                .pointerInput(Unit) {
                    detectTransformGestures { _, pan, zoom, _ ->
                        scale = (scale * zoom).coerceIn(0.6f, 5.0f)
                        offsetX += pan.x
                        offsetY += pan.y
                    }
                }
        ) {
            // Blueprint Technical Canvas
            val isDark = LocalIsDarkTheme.current

            Canvas(
                modifier = Modifier.fillMaxSize()
            ) {
                val canvasW = size.width
                val canvasH = size.height

                // Draw technical grid background
                drawBlueprintGrid(scale, offsetX, offsetY, canvasW, canvasH, isDark)

                // Render schematic components and wire traces
                drawSchematicPage(
                    page = page,
                    activeWireId = activeTracedWireId,
                    scale = scale,
                    panX = offsetX,
                    panY = offsetY,
                    canvasW = canvasW,
                    canvasH = canvasH,
                    isDark = isDark
                )
            }

            // Canvas Zoom & Reset Overlay Controls
            Row(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilledTonalIconButton(
                    onClick = {
                        scale = 1f
                        offsetX = 0f
                        offsetY = 0f
                    },
                    colors = IconButtonDefaults.filledTonalIconButtonColors(
                        containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f),
                        contentColor = MaterialTheme.colorScheme.onSurface
                    ),
                    modifier = Modifier.size(40.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.FilterCenterFocus,
                        contentDescription = "Fit to Screen",
                        modifier = Modifier.size(20.dp)
                    )
                }

                FilledTonalIconButton(
                    onClick = { scale = (scale * 1.25f).coerceAtMost(5.0f) },
                    colors = IconButtonDefaults.filledTonalIconButtonColors(
                        containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f),
                        contentColor = MaterialTheme.colorScheme.onSurface
                    ),
                    modifier = Modifier.size(40.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.ZoomIn,
                        contentDescription = "Zoom In",
                        modifier = Modifier.size(20.dp)
                    )
                }

                FilledTonalIconButton(
                    onClick = { scale = (scale / 1.25f).coerceAtLeast(0.6f) },
                    colors = IconButtonDefaults.filledTonalIconButtonColors(
                        containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f),
                        contentColor = MaterialTheme.colorScheme.onSurface
                    ),
                    modifier = Modifier.size(40.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.ZoomOut,
                        contentDescription = "Zoom Out",
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            // Zoom level indicator tag
            Surface(
                color = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f),
                shape = RoundedCornerShape(4.dp),
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(16.dp)
            ) {
                Text(
                    text = "${(scale * 100).toInt()}% ZOOM",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontFamily = FontFamily.Monospace,
                        color = MaterialTheme.colorScheme.onSurface
                    ),
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                )
            }
        }
    }
}

private fun DrawScope.drawBlueprintGrid(
    scale: Float,
    panX: Float,
    panY: Float,
    width: Float,
    height: Float,
    isDark: Boolean
) {
    // Fill canvas background
    drawRect(color = if (isDark) Slate950 else Color(0xFFF8FAFC))

    val gridSize = 40f * scale
    val startX = (panX % gridSize) - gridSize
    val startY = (panY % gridSize) - gridSize
    val gridColor = if (isDark) BlueprintGrid.copy(alpha = 0.4f) else Color(0xFFCBD5E1).copy(alpha = 0.5f)

    var x = startX
    while (x < width + gridSize) {
        drawLine(
            color = gridColor,
            start = Offset(x, 0f),
            end = Offset(x, height),
            strokeWidth = 1f
        )
        x += gridSize
    }

    var y = startY
    while (y < height + gridSize) {
        drawLine(
            color = gridColor,
            start = Offset(0f, y),
            end = Offset(width, y),
            strokeWidth = 1f
        )
        y += gridSize
    }
}

private fun DrawScope.drawSchematicPage(
    page: SchematicPage,
    activeWireId: String?,
    scale: Float,
    panX: Float,
    panY: Float,
    canvasW: Float,
    canvasH: Float,
    isDark: Boolean
) {
    // Normalization scale: assume points are laid out in a 1000x500 virtual coordinate space
    val baseScaleX = canvasW / 1000f
    val baseScaleY = canvasH / 500f

    fun toScreenX(normX: Float): Float = (normX * baseScaleX) * scale + panX
    fun toScreenY(normY: Float): Float = (normY * baseScaleY) * scale + panY

    // 1. Draw Wire Traces
    page.wireTraces.forEach { wire ->
        val isTraced = activeWireId == null || activeWireId == wire.id
        val wireBaseColor = Color(wire.colorHex)
        val finalColor = if (isTraced) {
            if (activeWireId != null) Color(wire.colorHex) else wireBaseColor.copy(alpha = 0.85f)
        } else {
            wireBaseColor.copy(alpha = 0.15f)
        }
        val strokeW = if (activeWireId == wire.id) 5.5f * scale else 2.5f * scale

        if (wire.points.size >= 2) {
            val path = Path()
            val first = wire.points.first()
            path.moveTo(toScreenX(first.x), toScreenY(first.y))

            for (i in 1 until wire.points.size) {
                val pt = wire.points[i]
                path.lineTo(toScreenX(pt.x), toScreenY(pt.y))
            }

            // Draw glowing halo if actively traced
            if (activeWireId == wire.id) {
                drawPath(
                    path = path,
                    color = finalColor.copy(alpha = 0.35f),
                    style = Stroke(width = strokeW * 2.5f)
                )
            }

            drawPath(
                path = path,
                color = finalColor,
                style = Stroke(width = strokeW)
            )

            // Draw terminal junction circles at each vertex
            wire.points.forEach { pt ->
                drawCircle(
                    color = if (isTraced) Color.White else Color.Gray.copy(alpha = 0.3f),
                    radius = if (activeWireId == wire.id) 5f * scale else 3f * scale,
                    center = Offset(toScreenX(pt.x), toScreenY(pt.y))
                )
            }
        }
    }

    // 2. Draw Schematic Components (Breakers, Drives, Relays, PLCs)
    page.components.forEach { comp ->
        val x = toScreenX(comp.x)
        val y = toScreenY(comp.y)
        val w = (comp.width * baseScaleX) * scale
        val h = (comp.height * baseScaleY) * scale

        // Component background box
        drawRect(
            color = if (isDark) Slate900 else Color.White,
            topLeft = Offset(x, y),
            size = Size(w, h)
        )
        drawRect(
            color = if (activeWireId != null) {
                if (isDark) Slate700 else Color(0xFFCBD5E1)
            } else {
                if (isDark) ElectricCyan else BlueprintBlue
            },
            topLeft = Offset(x, y),
            size = Size(w, h),
            style = Stroke(width = 2f * scale)
        )

        // Component header banner
        drawRect(
            color = if (isDark) Slate800 else Color(0xFFE0F2FE),
            topLeft = Offset(x, y),
            size = Size(w, (22f * baseScaleY) * scale)
        )

        // Draw terminal pins indicators along top and bottom
        val pinCount = comp.terminalPins.size
        if (pinCount > 0) {
            val step = w / (pinCount + 1)
            comp.terminalPins.forEachIndexed { idx, _ ->
                val pinX = x + step * (idx + 1)
                // Top pin node
                drawCircle(
                    color = SafetyAmber,
                    radius = 3.5f * scale,
                    center = Offset(pinX, y)
                )
                // Bottom pin node
                drawCircle(
                    color = if (isDark) ElectricCyan else BlueprintBlue,
                    radius = 3.5f * scale,
                    center = Offset(pinX, y + h)
                )
            }
        }

        // Component label using nativeCanvas for sharp rendering
        drawContext.canvas.nativeCanvas.apply {
            val paintTitle = android.graphics.Paint().apply {
                color = if (isDark) android.graphics.Color.WHITE else android.graphics.Color.parseColor("#0F172A")
                textSize = (12f * scale).coerceIn(9f, 22f)
                typeface = android.graphics.Typeface.DEFAULT_BOLD
                isAntiAlias = true
            }
            val paintSub = android.graphics.Paint().apply {
                color = if (isDark) android.graphics.Color.parseColor("#94A3B8") else android.graphics.Color.parseColor("#475569")
                textSize = (10f * scale).coerceIn(8f, 18f)
                isAntiAlias = true
            }

            drawText(
                comp.designation,
                x + 8f * scale,
                y + (16f * scale).coerceAtLeast(10f),
                paintTitle
            )
            drawText(
                comp.label.take(18),
                x + 8f * scale,
                y + (38f * scale).coerceAtLeast(24f),
                paintSub
            )
        }
    }
}
