package com.example.data.sync

import com.example.data.model.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.util.concurrent.TimeUnit

sealed class SyncResult {
    data class Success(val machines: List<Machine>, val message: String) : SyncResult()
    data class Error(val error: String) : SyncResult()
}

class SheetsSyncService {

    private val httpClient: OkHttpClient = OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(20, TimeUnit.SECONDS)
        .build()

    /**
     * Fetches CSV data from a Google Sheet given its Sheet ID and Sheet Name/GID.
     */
    suspend fun syncFromGoogleSheet(sheetIdOrUrl: String): SyncResult = withContext(Dispatchers.IO) {
        try {
            val cleanId = extractSheetId(sheetIdOrUrl)
            if (cleanId.isBlank()) {
                return@withContext SyncResult.Error("Invalid Google Sheet ID or URL. Please provide a valid Sheet ID.")
            }

            // Google Sheets Visualization API CSV export endpoint (works with shared view-only sheets)
            val url = "https://docs.google.com/spreadsheets/d/$cleanId/gviz/tq?tqx=out:csv"

            val request = Request.Builder()
                .url(url)
                .header("User-Agent", "InduSchemPro-Android/1.0")
                .build()

            val response = httpClient.newCall(request).execute()
            if (!response.isSuccessful) {
                return@withContext SyncResult.Error("Google Sheets HTTP Error ${response.code}: Ensure the sheet is shared as 'Anyone with the link can view'")
            }

            val csvContent = response.body?.string() ?: ""
            if (csvContent.isBlank()) {
                return@withContext SyncResult.Error("Received empty response from Google Sheet.")
            }

            val parsedMachines = parseMachinesCsv(csvContent)
            if (parsedMachines.isEmpty()) {
                return@withContext SyncResult.Error("No valid machine rows could be parsed. Check column headers.")
            }

            SyncResult.Success(
                machines = parsedMachines,
                message = "Successfully synchronized ${parsedMachines.size} industrial machines from Google Sheet!"
            )
        } catch (e: Exception) {
            SyncResult.Error(e.message ?: "Failed to connect to Google Sheets. Check internet connection.")
        }
    }

    private fun extractSheetId(input: String): String {
        val trimmed = input.trim()
        if (trimmed.contains("/d/")) {
            val parts = trimmed.split("/d/")
            if (parts.size > 1) {
                return parts[1].substringBefore("/")
            }
        }
        return trimmed
    }

    /**
     * Parses CSV lines into Machine objects.
     */
    fun parseMachinesCsv(csv: String): List<Machine> {
        val lines = csv.lines().filter { it.isNotBlank() }
        if (lines.size <= 1) return emptyList()

        val machines = mutableListOf<Machine>()
        // Skip header line
        for (i in 1 until lines.size) {
            val cols = parseCsvLine(lines[i])
            if (cols.size >= 4) {
                val model = cols.getOrElse(0) { "M-${i}" }
                val name = cols.getOrElse(1) { "Industrial Machine $i" }
                val manufacturer = cols.getOrElse(2) { "Generic Automation" }
                val catStr = cols.getOrElse(3) { "TEXTILE" }
                val power = cols.getOrElse(4) { "15 kW" }
                val voltage = cols.getOrElse(5) { "380V AC 3-Phase" }
                val plc = cols.getOrElse(6) { "Standard PLC" }
                val inv = cols.getOrElse(7) { "Standard Inverter" }
                val desc = cols.getOrElse(8) { "Industrial automation system synchronized from Google Sheet." }
                val isPrem = cols.getOrElse(9) { "true" }.trim().equals("true", ignoreCase = true)

                val category = when {
                    catStr.contains("DRIVE", true) || catStr.contains("INVERTER", true) -> MachineCategory.INVERTER_DRIVE
                    catStr.contains("PLC", true) || catStr.contains("PANEL", true) -> MachineCategory.PLC_PANEL
                    catStr.contains("WEAV", true) || catStr.contains("LOOM", true) -> MachineCategory.WEAVING
                    catStr.contains("SENSOR", true) || catStr.contains("REMOTE", true) -> MachineCategory.SENSORS_IO
                    else -> MachineCategory.TEXTILE
                }

                machines.add(
                    Machine(
                        id = "gsheet-$i-$model",
                        name = name,
                        model = model,
                        manufacturer = manufacturer,
                        category = category,
                        powerRating = power,
                        supplyVoltage = voltage,
                        controlVoltage = "24V DC",
                        fullLoadCurrent = "32.0 A",
                        plcModel = plc,
                        inverterModel = inv,
                        description = desc,
                        isPremium = isPrem,
                        specs = listOf(
                            SpecItem("Google Sheets Sync", "Source", "Live Cloud Sheet"),
                            SpecItem("General", "Model Code", model),
                            SpecItem("General", "Manufacturer", manufacturer),
                            SpecItem("Electrical", "Supply Voltage", voltage),
                            SpecItem("Ratings", "Power Rating", power)
                        ),
                        plcIOList = listOf(
                            PlcIOItem("I0.0", "TB1-01", "COM (24V)", PlcIOType.DIGITAL_INPUT, "System Run Command", "Pushbutton SB1", "W001", "NO"),
                            PlcIOItem("I0.1", "TB1-02", "COM (24V)", PlcIOType.DIGITAL_INPUT, "Emergency Stop Status", "Safety Relay", "W002", "NC"),
                            PlcIOItem("Q0.0", "TB2-01", "COM (24V)", PlcIOType.DIGITAL_OUTPUT, "Line Contactor Enable", "KM1 Coil", "W101", "NO")
                        ),
                        partsList = listOf(
                            PartItem(1, "MAIN-BRK", "MCCB-63A", manufacturer, "Main Molded Case Breaker", "63A 3P", 1, "Panel Bay"),
                            PartItem(2, "CTRL-PLC", plc, "OEM", "Master Logic Controller", "24VDC", 1, "Control Rail")
                        ),
                        schematicPages = (1..12).map { pageNum ->
                            SchematicPage(
                                pageNumber = pageNum,
                                title = "Page $pageNum: $name Electrical Schematic",
                                dwgCode = "GS-$model-P$pageNum",
                                description = "Synchronized schematic diagram for $name from Google Sheet Master Database.",
                                components = listOf(
                                    SchematicComponent("C1", name, model, 300f, 150f, 220f, 160f, listOf("L1", "L2", "L3", "24V", "0V"))
                                ),
                                wireTraces = listOf(
                                    WireTrace("T1", "Main Bus Trace", 0xFF00E5FF, "24V DC", listOf(SchematicPoint(100f, 200f), SchematicPoint(300f, 200f)))
                                )
                            )
                        },
                        wireGauges = listOf(
                            WireGaugeItem("Mains Line", "6.0 mm²", "Black/Brown/Grey", "Standard 3-Phase")
                        ),
                        encoderSpecs = emptyList(),
                        terminalBlocks = emptyList()
                    )
                )
            }
        }
        return machines
    }

    private fun parseCsvLine(line: String): List<String> {
        val tokens = mutableListOf<String>()
        val sb = StringBuilder()
        var inQuotes = false

        for (ch in line) {
            when (ch) {
                '\"' -> inQuotes = !inQuotes
                ',' -> {
                    if (inQuotes) {
                        sb.append(ch)
                    } else {
                        tokens.add(sb.toString().trim().removeSurrounding("\""))
                        sb.clear()
                    }
                }
                else -> sb.append(ch)
            }
        }
        tokens.add(sb.toString().trim().removeSurrounding("\""))
        return tokens
    }
}
