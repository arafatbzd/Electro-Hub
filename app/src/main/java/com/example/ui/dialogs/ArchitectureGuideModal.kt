package com.example.ui.dialogs

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.ui.theme.*

@Composable
fun ArchitectureGuideModal(
    onDismiss: () -> Unit
) {
    val industrialColors = LocalIndustrialColors.current

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
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
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
                                imageVector = Icons.Default.Architecture,
                                contentDescription = null,
                                tint = industrialColors.accentPrimary,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }
                    Column {
                        Text(
                            text = "Architectural & Engineering Guide",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        )
                        Text(
                            text = "Full-Stack Industrial Automation App Blueprint",
                            style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                        )
                    }
                }

                // Section 1: Data Structure & Organization
                GuideSection(
                    title = "1. Data Structure & Multi-Sheet Hierarchy",
                    description = "How 20-30 machines with 24+ page schematics, PLC I/O, and BOMs are modeled:"
                ) {
                    CodeBlock(
                        label = "JSON Schema Structure",
                        code = """{
  "machine_id": "FA494-ROVING-01",
  "name": "FA494 Computerized Roving Frame",
  "specs": {
    "spindles": 144,
    "main_motor_kw": 18.5,
    "drafting_motor_kw": 5.5,
    "supply_voltage": "380-415VAC 3~ 50Hz"
  },
  "plc_io": [
    {
      "address": "X0",
      "terminal": "TB2-01",
      "com": "COM0 (+24V)",
      "type": "DI",
      "signal": "E-Stop Safety Chain OK",
      "device": "Pilz PNOZ Relay",
      "wire_tag": "W101 (0.75mm² Red)"
    }
  ],
  "schematic_pages": [
    {
      "page": 1,
      "dwg": "DWG-FA494-01",
      "title": "380V Main Distribution",
      "wire_traces": [
        {"id": "L1", "voltage": "380V", "points": [{"x":50,"y":160},{"x":200,"y":160}]}
      ]
    }
  ]
}"""
                    )
                }

                // Section 2: Premium View & Access Control
                GuideSection(
                    title = "2. Premium Tier & Secure Blurring Architecture",
                    description = "Dual-tier security model for industrial blueprints and schematics:"
                ) {
                    Text(
                        text = "• Client Tier Gate: UI blur modifier (Modifier.blur(16.dp)) + Frosted Glass Lock Card overlay.\n" +
                               "• Data Protection: High-resolution vector paths (CAD traces) and downloadable CSV/PDF datasheets are conditionally stripped unless authenticated with Premium token or Room DB flag.\n" +
                               "• Offline Industrial Factory Support: Once unlocked, full machine packs are cached locally in Room SQLite database so field technicians can inspect schematics deep inside mills without Wi-Fi.",
                        style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant, lineHeight = 20.sp)
                    )
                }

                // Section 3: High-Performance Vector Drawing Viewer
                GuideSection(
                    title = "3. Interactive Drawing Viewer & Wire Tracing",
                    description = "Engineering implementation for high-density CAD schematics:"
                ) {
                    Text(
                        text = "• Vector Canvas: Rendered using Compose DrawScope and android.graphics.Canvas native text for zero lag.\n" +
                               "• Gesture Matrix: Transform gestures calculate scale (0.5x to 5.0x) and pan offset seamlessly.\n" +
                               "• Live Wire Tracing: When an automation engineer taps an electrical net (e.g. 'L1', '+24V', 'E-Stop'), all associated vector segments illuminate with a glowing neon halo path, dimming other lines for rapid tracing!",
                        style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant, lineHeight = 20.sp)
                    )
                }

                // Section 4: Supabase / Google Sheets Integration
                GuideSection(
                    title = "4. Supabase & Google Sheets Real-Time Sync",
                    description = "Live sync pipeline for enterprise maintenance schedules:"
                ) {
                    Text(
                        text = "• REST API / Ktor: Fetch JSON data from Google Sheets Public CSV Export or Supabase PostgREST tables.\n" +
                               "• Room Upsert Strategy: Data is atomically synced into SQLite using OnConflictStrategy.REPLACE.\n" +
                               "• Offline Fallback: If network drops, the app transparently switches to the local cache without user interruption.",
                        style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant, lineHeight = 20.sp)
                    )
                }

                // Action Close Button
                Button(
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth().height(48.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary, contentColor = MaterialTheme.colorScheme.onPrimary),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Got It, Return to Schematics", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
private fun GuideSection(
    title: String,
    description: String,
    content: @Composable () -> Unit
) {
    val industrialColors = LocalIndustrialColors.current

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(10.dp))
            .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(10.dp))
            .padding(14.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Bold,
                color = industrialColors.accentPrimary
            )
        )
        Text(
            text = description,
            style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
        )
        content()
    }
}

@Composable
private fun CodeBlock(label: String, code: String) {
    val industrialColors = LocalIndustrialColors.current

    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = FontWeight.Bold,
                color = industrialColors.accentWarning
            )
        )
        Surface(
            color = Slate950,
            shape = RoundedCornerShape(6.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = code,
                style = MaterialTheme.typography.bodySmall.copy(
                    fontFamily = FontFamily.Monospace,
                    fontSize = 11.sp,
                    color = Color(0xFF81D4FA),
                    lineHeight = 16.sp
                ),
                modifier = Modifier.padding(10.dp)
            )
        }
    }
}
