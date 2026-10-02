package com.example.data.model

enum class MachineCategory(val label: String) {
    TEXTILE("Textile & Spinning"),
    INVERTER_DRIVE("Inverters & Drives"),
    PLC_PANEL("PLC & Control Panels"),
    WEAVING("Weaving & Looms"),
    SENSORS_IO("Sensors & Remote I/O")
}

data class SpecItem(
    val category: String,
    val name: String,
    val value: String,
    val unit: String = ""
)

enum class PlcIOType(val shortLabel: String, val isInput: Boolean) {
    DIGITAL_INPUT("DI", true),
    DIGITAL_OUTPUT("DO", false),
    ANALOG_INPUT("AI", true),
    ANALOG_OUTPUT("AO", false),
    HIGH_SPEED_COUNTER("HSC", true),
    COMMUNICATION("COM", false)
}

data class PlcIOItem(
    val address: String,          // e.g. "X0", "X1", "%IX0.0", "Y0", "%QX0.0"
    val terminalPin: String,      // e.g. "TB2-14", "PLC-IN-0"
    val comPort: String,          // e.g. "COM0 (24V+)", "COM1 (0V)"
    val type: PlcIOType,
    val signalName: String,       // e.g. "Front Roller Jam Stop"
    val device: String,           // e.g. "Inductive Proximity Sensor SQ1"
    val wireTag: String,          // e.g. "W102 (0.75mm² Blue)"
    val normallyState: String     // "NO", "NC", "4-20mA", "0-10V"
)

data class PartItem(
    val itemNo: Int,
    val designation: String,      // e.g. "QF1", "KM1", "INV1", "PLC-CPU"
    val partNumber: String,       // e.g. "131B0044", "3RT2026-1BB40"
    val manufacturer: String,     // e.g. "Danfoss", "Siemens", "Schneider"
    val description: String,
    val specification: String,    // e.g. "63A 3P 25kA", "24VDC coil, 18A AC-3"
    val quantity: Int,
    val location: String          // e.g. "Main Panel", "Machine Bed", "Inverter Bay"
)

data class SchematicPoint(
    val x: Float, // Normalized 0f..1000f
    val y: Float
)

data class WireTrace(
    val id: String,               // e.g. "L1", "+24V", "X0_STOP", "RS485_A"
    val label: String,            // "3-Phase Phase 1", "+24VDC Control Bus"
    val colorHex: Long,           // Color hex value
    val voltageLevel: String,     // "400V AC", "24V DC", "RS-485", "5V TTL"
    val points: List<SchematicPoint>
)

data class SchematicComponent(
    val id: String,               // "QF1", "KM1", "PLC_CPU", "DANFOSS_FC302"
    val label: String,
    val designation: String,
    val x: Float,                 // Normalized 0f..1000f
    val y: Float,
    val width: Float,
    val height: Float,
    val terminalPins: List<String>
)

data class SchematicPage(
    val pageNumber: Int,
    val title: String,
    val dwgCode: String,
    val description: String,
    val components: List<SchematicComponent>,
    val wireTraces: List<WireTrace>
)

data class WireGaugeItem(
    val circuitType: String,      // "Main Power 380V", "Inverter Motor Lead", "24V Control Bus", "Analog/Shielded"
    val gauge: String,            // "10 mm² (8 AWG)", "0.75 mm² (18 AWG)", "Twisted Pair 0.5 mm²"
    val colorCode: String,        // "Black / Brown / Grey", "Dark Blue / White-Blue", "Shielded Twisted"
    val notes: String
)

data class EncoderSpecItem(
    val pin: String,              // "Pin 1", "Pin 2", ...
    val signal: String,           // "A+", "A-", "B+", "B-", "Z+", "Z-", "+24V", "0V", "Shield"
    val wireColor: String,        // "Brown", "White", "Green", "Yellow"
    val function: String          // "Spindle incremental pulse ch A"
)

data class TerminalBlock(
    val name: String,             // "TB1 - High Voltage Mains", "TB2 - 24VDC Digital I/O"
    val terminalCount: Int,
    val description: String,
    val entries: List<TerminalEntry>
)

data class TerminalEntry(
    val terminalNo: String,
    val internalWire: String,
    val fieldDevice: String,
    val externalCable: String,
    val function: String
)

data class Machine(
    val id: String,
    val name: String,
    val model: String,
    val manufacturer: String,
    val category: MachineCategory,
    val powerRating: String,
    val supplyVoltage: String,
    val controlVoltage: String,
    val fullLoadCurrent: String,
    val plcModel: String,
    val inverterModel: String,
    val description: String,
    val isPremium: Boolean,
    val isBookmarked: Boolean = false,
    val specs: List<SpecItem>,
    val plcIOList: List<PlcIOItem>,
    val partsList: List<PartItem>,
    val schematicPages: List<SchematicPage>,
    val wireGauges: List<WireGaugeItem>,
    val encoderSpecs: List<EncoderSpecItem>,
    val terminalBlocks: List<TerminalBlock>
)
