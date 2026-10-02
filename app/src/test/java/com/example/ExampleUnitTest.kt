package com.example

import com.example.data.model.MachineCategory
import com.example.data.samples.DefaultMachineryData
import com.example.data.sync.SheetsSyncService
import com.example.data.sync.SupabaseService
import org.junit.Assert.*
import org.junit.Test

class ExampleUnitTest {

    @Test
    fun machineryCatalog_hasIndustrialMachines() {
        val machines = DefaultMachineryData.getMachines()
        assertTrue("Catalog should contain at least 25 machines", machines.size >= 25)

        val fa494 = machines.find { it.model.contains("FA494") }
        assertNotNull("FA494 Roving Frame must be present", fa494)
        assertEquals(24, fa494?.schematicPages?.size)
        assertTrue(fa494?.plcIOList?.isNotEmpty() == true)

        val danfoss = machines.find { it.model.contains("FC-302") }
        assertNotNull("Danfoss FC302 inverter must be present", danfoss)
    }

    @Test
    fun sheetsCsvParser_parsesValidColumns() {
        val csv = """
            Model,Name,Manufacturer,Category,PowerRating,SupplyVoltage,PLCType,InverterType,Description,IsPremium
            FA494,FA494 Roving Frame,Jingwei,TEXTILE,18.5 kW,380V,FX5U,FC302,Speed frame drafting,true
            FC-302,Danfoss VLT FC302,Danfoss,INVERTER,15 kW,400V,Embedded,FC302,Industrial VFD,true
        """.trimIndent()

        val parsed = SheetsSyncService().parseMachinesCsv(csv)
        assertEquals(2, parsed.size)
        assertEquals("FA494", parsed[0].model)
        assertEquals(MachineCategory.TEXTILE, parsed[0].category)
        assertEquals("FC-302", parsed[1].model)
        assertEquals(MachineCategory.INVERTER_DRIVE, parsed[1].category)
    }
}
