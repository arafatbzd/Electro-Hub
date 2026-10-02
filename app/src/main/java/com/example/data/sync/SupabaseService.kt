package com.example.data.sync

import com.example.data.model.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

sealed class SupabaseResult {
    data class Success(val machines: List<Machine>, val message: String) : SupabaseResult()
    data class Error(val error: String) : SupabaseResult()
}

class SupabaseService {

    private val jsonMediaType = "application/json; charset=utf-8".toMediaType()

    private val httpClient: OkHttpClient = OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(20, TimeUnit.SECONDS)
        .build()

    /**
     * Connects to Supabase REST API (PostgREST) and fetches the machines table.
     */
    suspend fun fetchFromSupabase(
        projectUrl: String,
        anonKey: String
    ): SupabaseResult = withContext(Dispatchers.IO) {
        try {
            val cleanUrl = projectUrl.trim().removeSuffix("/")
            val cleanKey = anonKey.trim()

            if (cleanUrl.isBlank() || !cleanUrl.startsWith("http")) {
                return@withContext SupabaseResult.Error("Invalid Supabase Project URL. Example: https://xyzcompany.supabase.co")
            }
            if (cleanKey.isBlank()) {
                return@withContext SupabaseResult.Error("Supabase Anon Public API Key is required.")
            }

            val endpoint = "$cleanUrl/rest/v1/machines?select=*"

            val request = Request.Builder()
                .url(endpoint)
                .header("apikey", cleanKey)
                .header("Authorization", "Bearer $cleanKey")
                .header("Accept", "application/json")
                .header("User-Agent", "ElectroHub-Android/1.0")
                .build()

            val response = httpClient.newCall(request).execute()
            if (!response.isSuccessful) {
                return@withContext SupabaseResult.Error("Supabase HTTP ${response.code}: ${response.message}. Ensure 'machines' table exists and RLS policy allows read.")
            }

            val responseBody = response.body?.string() ?: ""
            if (responseBody.isBlank() || responseBody == "[]") {
                return@withContext SupabaseResult.Error("Connection successful, but 'machines' table is empty. Tap 'Push All Machines' to bulk seed!")
            }

            val parsedMachines = parseSupabaseMachinesJson(responseBody)
            if (parsedMachines.isEmpty()) {
                return@withContext SupabaseResult.Error("Failed to parse machine records from Supabase.")
            }

            SupabaseResult.Success(
                machines = parsedMachines,
                message = "Successfully synchronized ${parsedMachines.size} machines with PLC I/O & BOM from Supabase!"
            )
        } catch (e: Exception) {
            SupabaseResult.Error(e.message ?: "Failed to connect to Supabase. Check internet connection and URL.")
        }
    }

    /**
     * 1-Click Bulk Push/Upload all machines to Supabase table
     */
    suspend fun pushAllMachinesToSupabase(
        projectUrl: String,
        anonKey: String,
        machines: List<Machine>
    ): SupabaseResult = withContext(Dispatchers.IO) {
        try {
            val cleanUrl = projectUrl.trim().removeSuffix("/")
            val cleanKey = anonKey.trim()

            if (cleanUrl.isBlank() || !cleanUrl.startsWith("http")) {
                return@withContext SupabaseResult.Error("Invalid Supabase Project URL.")
            }
            if (cleanKey.isBlank()) {
                return@withContext SupabaseResult.Error("Supabase Anon Public API Key is required.")
            }

            val jsonArray = JSONArray()
            machines.forEach { machine ->
                jsonArray.put(serializeMachineToJson(machine))
            }

            val endpoint = "$cleanUrl/rest/v1/machines"
            val requestBody = jsonArray.toString().toRequestBody(jsonMediaType)

            val request = Request.Builder()
                .url(endpoint)
                .header("apikey", cleanKey)
                .header("Authorization", "Bearer $cleanKey")
                .header("Content-Type", "application/json")
                .header("Prefer", "resolution=merge-duplicates")
                .post(requestBody)
                .build()

            val response = httpClient.newCall(request).execute()
            if (response.isSuccessful || response.code == 201 || response.code == 200) {
                SupabaseResult.Success(
                    machines = machines,
                    message = "Successfully pushed ${machines.size} machines (including PLC I/O & Parts BOM) into Supabase Electro Hub!"
                )
            } else {
                val errorBody = response.body?.string() ?: ""
                SupabaseResult.Error("Supabase Error ${response.code}: $errorBody. Ensure RLS INSERT policy is enabled and plc_io JSONB column exists.")
            }
        } catch (e: Exception) {
            SupabaseResult.Error(e.message ?: "Failed to upload machines to Supabase.")
        }
    }

    /**
     * Upsert a single machine (Add, Edit, or modify PLC/Parts) to Supabase
     */
    suspend fun upsertMachineToSupabase(
        projectUrl: String,
        anonKey: String,
        machine: Machine
    ): SupabaseResult = withContext(Dispatchers.IO) {
        try {
            val cleanUrl = projectUrl.trim().removeSuffix("/")
            val cleanKey = anonKey.trim()

            if (cleanUrl.isBlank() || cleanKey.isBlank()) {
                return@withContext SupabaseResult.Success(listOf(machine), "Saved locally in app.")
            }

            val jsonArray = JSONArray().apply {
                put(serializeMachineToJson(machine))
            }

            val endpoint = "$cleanUrl/rest/v1/machines"
            val requestBody = jsonArray.toString().toRequestBody(jsonMediaType)

            val request = Request.Builder()
                .url(endpoint)
                .header("apikey", cleanKey)
                .header("Authorization", "Bearer $cleanKey")
                .header("Content-Type", "application/json")
                .header("Prefer", "resolution=merge-duplicates")
                .post(requestBody)
                .build()

            val response = httpClient.newCall(request).execute()
            if (response.isSuccessful || response.code == 201 || response.code == 200) {
                SupabaseResult.Success(listOf(machine), "Saved locally and synced to Supabase (Electro Hub)!")
            } else {
                SupabaseResult.Success(listOf(machine), "Saved locally. Supabase HTTP ${response.code}")
            }
        } catch (e: Exception) {
            SupabaseResult.Success(listOf(machine), "Saved locally (Supabase offline)")
        }
    }

    /**
     * Delete a machine from Supabase
     */
    suspend fun deleteMachineFromSupabase(
        projectUrl: String,
        anonKey: String,
        machineId: String
    ): SupabaseResult = withContext(Dispatchers.IO) {
        try {
            val cleanUrl = projectUrl.trim().removeSuffix("/")
            val cleanKey = anonKey.trim()

            if (cleanUrl.isBlank() || cleanKey.isBlank()) {
                return@withContext SupabaseResult.Success(emptyList(), "Deleted locally.")
            }

            val endpoint = "$cleanUrl/rest/v1/machines?id=eq.$machineId"

            val request = Request.Builder()
                .url(endpoint)
                .header("apikey", cleanKey)
                .header("Authorization", "Bearer $cleanKey")
                .delete()
                .build()

            val response = httpClient.newCall(request).execute()
            if (response.isSuccessful || response.code == 204 || response.code == 200) {
                SupabaseResult.Success(emptyList(), "Deleted from Supabase and local storage!")
            } else {
                SupabaseResult.Success(emptyList(), "Deleted locally. (Supabase HTTP ${response.code})")
            }
        } catch (e: Exception) {
            SupabaseResult.Success(emptyList(), "Deleted locally.")
        }
    }

    fun serializeMachineToJson(machine: Machine): JSONObject {
        return JSONObject().apply {
            put("id", machine.id)
            put("name", machine.name)
            put("model", machine.model)
            put("manufacturer", machine.manufacturer)
            put("category", machine.category.name)
            put("power_rating", machine.powerRating)
            put("supply_voltage", machine.supplyVoltage)
            put("control_voltage", machine.controlVoltage)
            put("full_load_current", machine.fullLoadCurrent)
            put("plc_model", machine.plcModel)
            put("inverter_model", machine.inverterModel)
            put("description", machine.description)

            // Serialize PLC I/O
            val plcArray = JSONArray()
            machine.plcIOList.forEach { io ->
                val ioObj = JSONObject().apply {
                    put("address", io.address)
                    put("terminalPin", io.terminalPin)
                    put("comPort", io.comPort)
                    put("type", io.type.name)
                    put("signalName", io.signalName)
                    put("device", io.device)
                    put("wireTag", io.wireTag)
                    put("normallyState", io.normallyState)
                }
                plcArray.put(ioObj)
            }
            put("plc_io", plcArray)

            // Serialize Parts List / BOM
            val partsArray = JSONArray()
            machine.partsList.forEach { part ->
                val partObj = JSONObject().apply {
                    put("itemNo", part.itemNo)
                    put("designation", part.designation)
                    put("partNumber", part.partNumber)
                    put("manufacturer", part.manufacturer)
                    put("description", part.description)
                    put("specification", part.specification)
                    put("quantity", part.quantity)
                    put("location", part.location)
                }
                partsArray.put(partObj)
            }
            put("parts_list", partsArray)

            // Serialize Specs
            val specsArray = JSONArray()
            machine.specs.forEach { spec ->
                val specObj = JSONObject().apply {
                    put("category", spec.category)
                    put("name", spec.name)
                    put("value", spec.value)
                    put("unit", spec.unit)
                }
                specsArray.put(specObj)
            }
            put("specs", specsArray)
        }
    }

    /**
     * Generates complete copy-pasteable SQL Script for Supabase SQL Editor
     * with JSONB support for PLC I/O, Parts List, and Specs
     */
    fun generateBulkInsertSql(machines: List<Machine>): String {
        val sb = StringBuilder()
        sb.appendLine("-- ====================================================================")
        sb.appendLine("-- ELECTRO HUB: COMPLETE BULK MACHINERY, PLC I/O & BOM PARTS SEED")
        sb.appendLine("-- Run this script in Supabase 'SQL Editor' ➔ 'New Query' ➔ 'Run'")
        sb.appendLine("-- ====================================================================")
        sb.appendLine("CREATE TABLE IF NOT EXISTS machines (")
        sb.appendLine("  id TEXT PRIMARY KEY,")
        sb.appendLine("  name TEXT NOT NULL,")
        sb.appendLine("  model TEXT NOT NULL,")
        sb.appendLine("  manufacturer TEXT NOT NULL,")
        sb.appendLine("  category TEXT NOT NULL,")
        sb.appendLine("  power_rating TEXT,")
        sb.appendLine("  supply_voltage TEXT,")
        sb.appendLine("  control_voltage TEXT,")
        sb.appendLine("  full_load_current TEXT,")
        sb.appendLine("  plc_model TEXT,")
        sb.appendLine("  inverter_model TEXT,")
        sb.appendLine("  description TEXT,")
        sb.appendLine("  plc_io JSONB DEFAULT '[]'::jsonb,")
        sb.appendLine("  parts_list JSONB DEFAULT '[]'::jsonb,")
        sb.appendLine("  specs JSONB DEFAULT '[]'::jsonb,")
        sb.appendLine("  created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW()")
        sb.appendLine(");")
        sb.appendLine()
        sb.appendLine("ALTER TABLE machines ENABLE ROW LEVEL SECURITY;")
        sb.appendLine("DROP POLICY IF EXISTS \"Allow public full access\" ON machines;")
        sb.appendLine("CREATE POLICY \"Allow public full access\" ON machines FOR ALL USING (true) WITH CHECK (true);")
        sb.appendLine()
        sb.appendLine("INSERT INTO machines (id, name, model, manufacturer, category, power_rating, supply_voltage, control_voltage, full_load_current, plc_model, inverter_model, description, plc_io, parts_list, specs)")
        sb.appendLine("VALUES")

        val rows = machines.map { m ->
            val json = serializeMachineToJson(m)
            val plcIoJson = json.getJSONArray("plc_io").toString().replace("'", "''")
            val partsJson = json.getJSONArray("parts_list").toString().replace("'", "''")
            val specsJson = json.getJSONArray("specs").toString().replace("'", "''")
            val safeDesc = m.description.replace("'", "''")
            val safeName = m.name.replace("'", "''")
            "('${m.id}', '$safeName', '${m.model}', '${m.manufacturer}', '${m.category.name}', '${m.powerRating}', '${m.supplyVoltage}', '${m.controlVoltage}', '${m.fullLoadCurrent}', '${m.plcModel}', '${m.inverterModel}', '$safeDesc', '$plcIoJson'::jsonb, '$partsJson'::jsonb, '$specsJson'::jsonb)"
        }
        sb.append(rows.joinToString(",\n"))
        sb.appendLine()
        sb.appendLine("ON CONFLICT (id) DO UPDATE SET")
        sb.appendLine("  name = EXCLUDED.name,")
        sb.appendLine("  model = EXCLUDED.model,")
        sb.appendLine("  manufacturer = EXCLUDED.manufacturer,")
        sb.appendLine("  category = EXCLUDED.category,")
        sb.appendLine("  power_rating = EXCLUDED.power_rating,")
        sb.appendLine("  supply_voltage = EXCLUDED.supply_voltage,")
        sb.appendLine("  plc_model = EXCLUDED.plc_model,")
        sb.appendLine("  inverter_model = EXCLUDED.inverter_model,")
        sb.appendLine("  description = EXCLUDED.description,")
        sb.appendLine("  plc_io = EXCLUDED.plc_io,")
        sb.appendLine("  parts_list = EXCLUDED.parts_list,")
        sb.appendLine("  specs = EXCLUDED.specs;")
        return sb.toString()
    }

    private fun parseSupabaseMachinesJson(jsonStr: String): List<Machine> {
        val list = mutableListOf<Machine>()
        val jsonArray = JSONArray(jsonStr)

        for (i in 0 until jsonArray.length()) {
            val obj = jsonArray.getJSONObject(i)
            val id = obj.optString("id", "supa-$i")
            val name = obj.optString("name", "Industrial Machine $i")
            val model = obj.optString("model", "M-$i")
            val manufacturer = obj.optString("manufacturer", "Generic")
            val catStr = obj.optString("category", "TEXTILE")
            val power = obj.optString("power_rating", "15 kW")
            val supply = obj.optString("supply_voltage", "380-415V 3~")
            val control = obj.optString("control_voltage", "24V DC")
            val current = obj.optString("full_load_current", "32.0 A")
            val plc = obj.optString("plc_model", "Standard PLC")
            val inv = obj.optString("inverter_model", "Standard VFD")
            val desc = obj.optString("description", "Synced from Supabase Electro Hub")

            val category = when {
                catStr.contains("DRIVE", true) || catStr.contains("INVERTER", true) -> MachineCategory.INVERTER_DRIVE
                catStr.contains("PLC", true) || catStr.contains("PANEL", true) -> MachineCategory.PLC_PANEL
                catStr.contains("WEAV", true) || catStr.contains("LOOM", true) -> MachineCategory.WEAVING
                catStr.contains("SENSOR", true) || catStr.contains("REMOTE", true) -> MachineCategory.SENSORS_IO
                else -> MachineCategory.TEXTILE
            }

            // Parse PLC I/O from JSONB if present
            val parsedPlcIO = if (obj.has("plc_io") && !obj.isNull("plc_io")) {
                try {
                    val rawPlc = obj.get("plc_io")
                    val arr = when (rawPlc) {
                        is JSONArray -> rawPlc
                        is String -> JSONArray(rawPlc)
                        else -> JSONArray()
                    }
                    (0 until arr.length()).map { idx ->
                        val itemObj = arr.getJSONObject(idx)
                        val typeStr = itemObj.optString("type", "DIGITAL_INPUT")
                        val plcType = try { PlcIOType.valueOf(typeStr) } catch(e: Exception) { PlcIOType.DIGITAL_INPUT }
                        PlcIOItem(
                            address = itemObj.optString("address", "X$idx"),
                            terminalPin = itemObj.optString("terminalPin", "TB2-0$idx"),
                            comPort = itemObj.optString("comPort", "COM0 (24V+)"),
                            type = plcType,
                            signalName = itemObj.optString("signalName", "Signal $idx"),
                            device = itemObj.optString("device", "Sensor / Actuator"),
                            wireTag = itemObj.optString("wireTag", "W10$idx"),
                            normallyState = itemObj.optString("normallyState", "NO")
                        )
                    }
                } catch(e: Exception) {
                    emptyList()
                }
            } else {
                emptyList()
            }

            val finalPlcIO = if (parsedPlcIO.isNotEmpty()) parsedPlcIO else listOf(
                PlcIOItem("X0", "TB2-01", "COM0 (24V+)", PlcIOType.DIGITAL_INPUT, "Emergency Stop Status", "Pilz Safety Relay", "W101", "NC"),
                PlcIOItem("X1", "TB2-02", "COM0 (24V+)", PlcIOType.DIGITAL_INPUT, "Door Interlock Switch", "Schmersal Safety Switch", "W102", "NO"),
                PlcIOItem("Y0", "TB3-01", "COM1 (24V+)", PlcIOType.DIGITAL_OUTPUT, "Main Contactor Enable", "KM1 Coil", "W201", "NO"),
                PlcIOItem("Y1", "TB3-02", "COM1 (24V+)", PlcIOType.DIGITAL_OUTPUT, "Inverter Run Command", "Danfoss FC302 STO", "W202", "NO")
            )

            // Parse Parts List / BOM from JSONB if present
            val parsedParts = if (obj.has("parts_list") && !obj.isNull("parts_list")) {
                try {
                    val rawParts = obj.get("parts_list")
                    val arr = when (rawParts) {
                        is JSONArray -> rawParts
                        is String -> JSONArray(rawParts)
                        else -> JSONArray()
                    }
                    (0 until arr.length()).map { idx ->
                        val partObj = arr.getJSONObject(idx)
                        PartItem(
                            itemNo = partObj.optInt("itemNo", idx + 1),
                            designation = partObj.optString("designation", "CMP-$idx"),
                            partNumber = partObj.optString("partNumber", "P-$idx"),
                            manufacturer = partObj.optString("manufacturer", manufacturer),
                            description = partObj.optString("description", "Industrial Component"),
                            specification = partObj.optString("specification", "Standard"),
                            quantity = partObj.optInt("quantity", 1),
                            location = partObj.optString("location", "Control Cabinet")
                        )
                    }
                } catch(e: Exception) {
                    emptyList()
                }
            } else {
                emptyList()
            }

            val finalParts = if (parsedParts.isNotEmpty()) parsedParts else listOf(
                PartItem(1, "QF1", "MCCB-63A", manufacturer, "Main Molded Case Breaker", "63A 3P 25kA", 1, "Panel Bay 1"),
                PartItem(2, "KM1", "LC1D32BD", "Schneider", "Main Motor Line Contactor", "32A AC-3 24VDC", 1, "Power Rail"),
                PartItem(3, "PLC-CPU", plc, "OEM", "Programmable Logic Controller", "24VDC 64 I/O", 1, "Control Rail")
            )

            // Parse Specs from JSONB if present
            val parsedSpecs = if (obj.has("specs") && !obj.isNull("specs")) {
                try {
                    val rawSpecs = obj.get("specs")
                    val arr = when (rawSpecs) {
                        is JSONArray -> rawSpecs
                        is String -> JSONArray(rawSpecs)
                        else -> JSONArray()
                    }
                    (0 until arr.length()).map { idx ->
                        val specObj = arr.getJSONObject(idx)
                        SpecItem(
                            category = specObj.optString("category", "General"),
                            name = specObj.optString("name", "Spec $idx"),
                            value = specObj.optString("value", "-"),
                            unit = specObj.optString("unit", "")
                        )
                    }
                } catch(e: Exception) {
                    emptyList()
                }
            } else {
                emptyList()
            }

            val finalSpecs = if (parsedSpecs.isNotEmpty()) parsedSpecs else listOf(
                SpecItem("General", "Model Code", model),
                SpecItem("General", "Manufacturer", manufacturer),
                SpecItem("Electrical", "Supply Voltage", supply),
                SpecItem("Ratings", "Power Rating", power),
                SpecItem("Automation", "PLC Controller", plc),
                SpecItem("Automation", "Inverter / VFD", inv)
            )

            // Create schematic pages (Pages 1 to 24)
            val pages = (1..24).map { pageNum ->
                SchematicPage(
                    pageNumber = pageNum,
                    title = "Page $pageNum: $name Electrical Schematic",
                    dwgCode = "SUPA-$model-P$pageNum",
                    description = "Industrial circuit drawing from Supabase cloud database.",
                    components = listOf(
                        SchematicComponent("CMP1", name, model, 300f, 150f, 220f, 160f, listOf("L1", "L2", "L3", "24V", "0V"))
                    ),
                    wireTraces = listOf(
                        WireTrace("L1", "Phase L1 (380V)", 0xFFFF5722, "380V AC", listOf(SchematicPoint(50f, 160f), SchematicPoint(300f, 160f))),
                        WireTrace("+24V", "Control Bus +24V", 0xFF00E5FF, "24V DC", listOf(SchematicPoint(50f, 220f), SchematicPoint(300f, 220f)))
                    )
                )
            }

            list.add(
                Machine(
                    id = id,
                    name = name,
                    model = model,
                    manufacturer = manufacturer,
                    category = category,
                    powerRating = power,
                    supplyVoltage = supply,
                    controlVoltage = control,
                    fullLoadCurrent = current,
                    plcModel = plc,
                    inverterModel = inv,
                    description = desc,
                    isPremium = false,
                    specs = finalSpecs,
                    plcIOList = finalPlcIO,
                    partsList = finalParts,
                    schematicPages = pages,
                    wireGauges = listOf(
                        WireGaugeItem("Mains Line", "10 mm²", "Black/Brown/Grey", "Standard 3-Phase")
                    ),
                    encoderSpecs = emptyList(),
                    terminalBlocks = emptyList()
                )
            )
        }
        return list
    }
}
