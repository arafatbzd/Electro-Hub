package com.example.data.samples

import com.example.data.model.*

object DefaultMachineryData {

    fun getMachines(): List<Machine> {
        return listOf(
            createFa494RovingFrame(),
            createDanfossFC302Drive(),
            createDanfossFC360Drive(),
            createSiemensS71200Panel(),
            createMitsubishiFX5UPanel(),
            createRieterG35RingFrame(),
            createTrutzschlerTC19iCard(),
            createToyotaJAT810Loom(),
            createDeltaDvpServoSystem(),
            createMurataQProWinder(),
            createSchneiderATV630(),
            createYaskawaGA700(),
            createOmronCP1H(),
            createMarzoliFT6ERoving(),
            createZinser72XLSpinning(),
            createPicanolOmniPlusLoom(),
            createSulzerG6300Rapier(),
            createSaurerBD7Rotor(),
            createSiemensSinamicsS120(),
            createABBACS880Drive(),
            createSickC4000Curtain(),
            createKeyenceFSN41NArray(),
            createBannerQ4XSensor(),
            createIFMVibrationMonitor(),
            createWeidmullerUR20RemoteIO(),
            createWagoPFC200Controller(),
            createAutonicsRotaryEncoder(),
            createFestoValveTerminal()
        )
    }

    // 1. FA494 Computerized Roving Frame
    private fun createFa494RovingFrame(): Machine {
        val pages = (1..24).map { pageNum ->
            when (pageNum) {
                1 -> SchematicPage(
                    pageNumber = 1,
                    title = "Page 1: 3-Phase 380V Main Distribution & Circuit Breakers",
                    dwgCode = "FA494-E01-01",
                    description = "Main breaker QF1 (63A 3P), RFI filter, line contactor KM1, and surge protector.",
                    components = listOf(
                        SchematicComponent("QF1", "Main Breaker 63A", "QF1", 200f, 150f, 120f, 80f, listOf("L1_IN", "L2_IN", "L3_IN", "L1_OUT", "L2_OUT", "L3_OUT")),
                        SchematicComponent("KM1", "Line Contactor 50A", "KM1", 450f, 150f, 120f, 80f, listOf("A1", "A2", "1/L1", "3/L2", "5/L3")),
                        SchematicComponent("FL1", "EMC Line Filter", "FL1", 700f, 150f, 120f, 80f, listOf("L1'", "L2'", "L3'", "PE"))
                    ),
                    wireTraces = listOf(
                        WireTrace("L1", "Phase L1 (380V)", 0xFFFF5722, "380V AC", listOf(SchematicPoint(50f, 160f), SchematicPoint(200f, 160f), SchematicPoint(320f, 160f), SchematicPoint(450f, 160f), SchematicPoint(570f, 160f), SchematicPoint(700f, 160f), SchematicPoint(900f, 160f))),
                        WireTrace("L2", "Phase L2 (380V)", 0xFFFFB300, "380V AC", listOf(SchematicPoint(50f, 190f), SchematicPoint(200f, 190f), SchematicPoint(320f, 190f), SchematicPoint(450f, 190f), SchematicPoint(570f, 190f), SchematicPoint(700f, 190f), SchematicPoint(900f, 190f))),
                        WireTrace("L3", "Phase L3 (380V)", 0xFF00E5FF, "380V AC", listOf(SchematicPoint(50f, 220f), SchematicPoint(200f, 220f), SchematicPoint(320f, 220f), SchematicPoint(450f, 220f), SchematicPoint(570f, 220f), SchematicPoint(700f, 220f), SchematicPoint(900f, 220f))),
                        WireTrace("PE", "Protective Earth Ground", 0xFF76FF03, "GND", listOf(SchematicPoint(50f, 280f), SchematicPoint(260f, 280f), SchematicPoint(510f, 280f), SchematicPoint(760f, 280f), SchematicPoint(900f, 280f)))
                    )
                )
                2 -> SchematicPage(
                    pageNumber = 2,
                    title = "Page 2: 24VDC Auxiliary Power Supply & Dual Safety E-Stop Loop",
                    dwgCode = "FA494-E01-02",
                    description = "24V 10A switched power supply G1, Pilz safety relay K1, and emergency stop button string.",
                    components = listOf(
                        SchematicComponent("G1", "PSU 24V 10A", "G1", 150f, 200f, 110f, 90f, listOf("L", "N", "PE", "+24V", "0V")),
                        SchematicComponent("K1", "Safety Relay PNOZ", "K1", 450f, 200f, 130f, 90f, listOf("A1", "A2", "S11", "S12", "S21", "S22", "13", "14")),
                        SchematicComponent("SB1", "E-Stop Head Headstock", "SB1", 720f, 160f, 90f, 60f, listOf("NC1", "NC2")),
                        SchematicComponent("SB2", "E-Stop Tailstock", "SB2", 720f, 260f, 90f, 60f, listOf("NC1", "NC2"))
                    ),
                    wireTraces = listOf(
                        WireTrace("+24V", "+24VDC Bus", 0xFF00E5FF, "24V DC", listOf(SchematicPoint(260f, 210f), SchematicPoint(380f, 210f), SchematicPoint(450f, 210f), SchematicPoint(580f, 210f), SchematicPoint(720f, 170f))),
                        WireTrace("0V", "0VDC / COM", 0xFF90CAF9, "0V DC", listOf(SchematicPoint(260f, 260f), SchematicPoint(450f, 260f), SchematicPoint(900f, 260f))),
                        WireTrace("ESTOP_CH1", "Safety Loop Ch 1", 0xFFFF1744, "24V DC", listOf(SchematicPoint(580f, 220f), SchematicPoint(720f, 180f), SchematicPoint(810f, 180f), SchematicPoint(850f, 220f), SchematicPoint(580f, 250f)))
                    )
                )
                3 -> SchematicPage(
                    pageNumber = 3,
                    title = "Page 3: Danfoss FC302 Spindle Inverter & Dynamic Braking",
                    dwgCode = "FA494-E01-03",
                    description = "Main drafting spindle motor drive (18.5kW), braking resistor BR1, and Safe Torque Off (STO) wiring.",
                    components = listOf(
                        SchematicComponent("INV1", "Danfoss FC-302 18.5kW", "INV1", 300f, 150f, 200f, 160f, listOf("91/L1", "92/L2", "93/L3", "96/U", "97/V", "98/W", "81/R+", "82/R-", "37/STO")),
                        SchematicComponent("BR1", "Braking Resistor 40Ω 2kW", "BR1", 650f, 180f, 120f, 70f, listOf("R+", "R-")),
                        SchematicComponent("M1", "Spindle Motor 18.5kW", "M1", 650f, 320f, 110f, 100f, listOf("U", "V", "W", "PE"))
                    ),
                    wireTraces = listOf(
                        WireTrace("INV_OUT", "Motor 3-Phase Drive Lead", 0xFF00E5FF, "0-400V AC Var", listOf(SchematicPoint(500f, 240f), SchematicPoint(600f, 240f), SchematicPoint(600f, 340f), SchematicPoint(650f, 340f))),
                        WireTrace("BR_BUS", "Braking Chopper DC Bus", 0xFFFF9100, "560V DC", listOf(SchematicPoint(500f, 200f), SchematicPoint(650f, 200f)))
                    )
                )
                4 -> SchematicPage(
                    pageNumber = 4,
                    title = "Page 4: PLC Digital Inputs (Front Roller, Sliver Break, Photoeyes)",
                    dwgCode = "FA494-E01-04",
                    description = "Mitsubishi FX5U inputs X0-X17 connecting photo-electric roving sensors, sliver stop motion, and limit switches.",
                    components = listOf(
                        SchematicComponent("PLC_IN", "FX5U Input Block X0-X17", "PLC_IN", 400f, 120f, 200f, 220f, listOf("S/S", "24V", "X0", "X1", "X2", "X3", "X4", "X5", "X6", "X7")),
                        SchematicComponent("SQ1", "Front Roller Jam (Prox)", "SQ1", 120f, 160f, 100f, 60f, listOf("+24V", "0V", "OUT")),
                        SchematicComponent("SQ2", "Sliver Break Sensor", "SQ2", 120f, 250f, 100f, 60f, listOf("+24V", "0V", "OUT"))
                    ),
                    wireTraces = listOf(
                        WireTrace("X0_TRACE", "Front Roller Sensor Wire (W101)", 0xFF00E5FF, "24V Signal", listOf(SchematicPoint(220f, 180f), SchematicPoint(320f, 180f), SchematicPoint(400f, 180f))),
                        WireTrace("X1_TRACE", "Sliver Break Sensor Wire (W102)", 0xFFFFB300, "24V Signal", listOf(SchematicPoint(220f, 270f), SchematicPoint(320f, 270f), SchematicPoint(400f, 210f)))
                    )
                )
                5 -> SchematicPage(
                    pageNumber = 5,
                    title = "Page 5: PLC Digital Outputs (Main Contactor, Solenoids, Fan)",
                    dwgCode = "FA494-E01-05",
                    description = "Relay outputs Y0-Y17 switching drafting lift solenoid, suction motor contactor, and optical tower beacon.",
                    components = listOf(
                        SchematicComponent("PLC_OUT", "FX5U Relay Block Y0-Y17", "PLC_OUT", 250f, 120f, 200f, 220f, listOf("COM0", "Y0", "Y1", "Y2", "Y3", "Y4", "COM1", "Y5")),
                        SchematicComponent("KA1", "Intermediate Relay (Suction)", "KA1", 600f, 140f, 120f, 70f, listOf("A1", "A2", "11", "14")),
                        SchematicComponent("YV1", "Pneumatic Bobbin Release", "YV1", 600f, 240f, 120f, 70f, listOf("+", "-"))
                    ),
                    wireTraces = listOf(
                        WireTrace("Y0_SIG", "Drafting Suction Relay Coil", 0xFF00E5FF, "24V DC", listOf(SchematicPoint(450f, 160f), SchematicPoint(600f, 160f))),
                        WireTrace("Y1_SIG", "Bobbin Release Solenoid", 0xFFFF9100, "24V DC", listOf(SchematicPoint(450f, 190f), SchematicPoint(550f, 190f), SchematicPoint(550f, 260f), SchematicPoint(600f, 260f)))
                    )
                )
                6 -> SchematicPage(
                    pageNumber = 6,
                    title = "Page 6: Spindle Rotary Encoder & High Speed Counter Wiring",
                    dwgCode = "FA494-E01-06",
                    description = "1024 PPR differential line driver encoder coupled to front roller, connected to FX5U high-speed counter ch1.",
                    components = listOf(
                        SchematicComponent("ENC1", "Front Roller Encoder 1024PPR", "ENC1", 150f, 150f, 140f, 120f, listOf("VCC", "0V", "A+", "A-", "B+", "B-", "Z+", "Z-", "SHIELD")),
                        SchematicComponent("HSC_MOD", "FX5-40SSC-G / FX5U High Speed In", "HSC", 500f, 150f, 180f, 140f, listOf("X0(A)", "X1(B)", "X2(Z)", "COM", "SHIELD_GND"))
                    ),
                    wireTraces = listOf(
                        WireTrace("ENC_A", "Channel A Differential Pulse", 0xFF00E5FF, "5V RS422", listOf(SchematicPoint(290f, 180f), SchematicPoint(500f, 180f))),
                        WireTrace("ENC_B", "Channel B Differential Pulse", 0xFFFFB300, "5V RS422", listOf(SchematicPoint(290f, 210f), SchematicPoint(500f, 210f)))
                    )
                )
                7 -> SchematicPage(
                    pageNumber = 7,
                    title = "Page 7: RS-485 Modbus RTU Inverter Communication Bus",
                    dwgCode = "FA494-E01-07",
                    description = "2-wire shielded twisted pair daisy chain connecting FX5-485ADP to Danfoss FC302 drives (Node 1 to 4).",
                    components = listOf(
                        SchematicComponent("PLC_COM", "FX5-485ADP Master", "FX5_COM", 150f, 180f, 140f, 90f, listOf("SDA(A)", "SDB(B)", "RDA", "RDB", "SG")),
                        SchematicComponent("INV_NODE1", "Danfoss FC302 Node 1 (Spindle)", "INV_1", 420f, 180f, 140f, 90f, listOf("68(+)", "69(-)", "61(GND)")),
                        SchematicComponent("INV_NODE2", "Danfoss FC302 Node 2 (Drafting)", "INV_2", 680f, 180f, 140f, 90f, listOf("68(+)", "69(-)", "TERM_SW"))
                    ),
                    wireTraces = listOf(
                        WireTrace("RS485_POS", "Modbus Data + (Term 68)", 0xFF00E5FF, "RS-485", listOf(SchematicPoint(290f, 200f), SchematicPoint(420f, 200f), SchematicPoint(560f, 200f), SchematicPoint(680f, 200f))),
                        WireTrace("RS485_NEG", "Modbus Data - (Term 69)", 0xFFFF5722, "RS-485", listOf(SchematicPoint(290f, 230f), SchematicPoint(420f, 230f), SchematicPoint(560f, 230f), SchematicPoint(680f, 230f)))
                    )
                )
                8 -> SchematicPage(
                    pageNumber = 8,
                    title = "Page 8: Multi-Motor Drafting & Carriage Lift Servos",
                    dwgCode = "FA494-E01-08",
                    description = "Servo drive wiring for bobbin rail lifting and electronic drafting coordinate sync.",
                    components = listOf(
                        SchematicComponent("SRV1", "Carriage Lift Servo Drive", "SRV1", 250f, 150f, 160f, 140f, listOf("L1", "L2", "L3", "U", "V", "W", "CN1_PULSE", "CN1_DIR")),
                        SchematicComponent("M_LIFT", "Permanent Magnet Servo 4kW", "M_LIFT", 600f, 150f, 120f, 100f, listOf("U", "V", "W", "BRK+", "BRK-"))
                    ),
                    wireTraces = listOf(
                        WireTrace("SRV_PWR", "Servo Power 3-Phase", 0xFF00E5FF, "380V AC", listOf(SchematicPoint(410f, 190f), SchematicPoint(600f, 190f)))
                    )
                )
                else -> SchematicPage(
                    pageNumber = pageNum,
                    title = "Page $pageNum: Terminal Strip TB$pageNum Interconnect & Field Schematics",
                    dwgCode = "FA494-E01-${pageNum.toString().padStart(2, '0')}",
                    description = "Detailed panel terminal connections, field wire numbering (W${pageNum}01-W${pageNum}18), and earthing bus.",
                    components = listOf(
                        SchematicComponent("TB$pageNum", "Terminal Block TB$pageNum", "TB$pageNum", 300f, 140f, 220f, 180f, listOf("Pin 1", "Pin 2", "Pin 3", "Pin 4", "Pin 5", "Pin 6", "Pin 7", "Pin 8")),
                        SchematicComponent("FLD$pageNum", "Field Sensor Array Block", "FLD$pageNum", 650f, 140f, 160f, 150f, listOf("SIG+", "SIG-", "PE", "24V"))
                    ),
                    wireTraces = listOf(
                        WireTrace("TB_NET_$pageNum", "Circuit Terminal Trace Line", 0xFF00E5FF, "24V / 220V", listOf(SchematicPoint(100f, 200f), SchematicPoint(300f, 200f), SchematicPoint(520f, 200f), SchematicPoint(650f, 200f)))
                    )
                )
            }
        }

        val plcIoList = listOf(
            PlcIOItem("X0", "TB2-01", "COM0 (24V+)", PlcIOType.DIGITAL_INPUT, "Emergency Stop Safety Loop OK", "Pilz PNOZ Relay Contact", "W101 (0.75mm² Red)", "NC"),
            PlcIOItem("X1", "TB2-02", "COM0 (24V+)", PlcIOType.DIGITAL_INPUT, "Front Roller Jam Detection", "Inductive Sensor SQ1 (Autonics PR18)", "W102 (0.75mm² Blue)", "NO"),
            PlcIOItem("X2", "TB2-03", "COM0 (24V+)", PlcIOType.DIGITAL_INPUT, "Sliver Break Stop Motion Left", "Optoelectronic Photogate PH1", "W103 (0.75mm² Blue)", "NC"),
            PlcIOItem("X3", "TB2-04", "COM0 (24V+)", PlcIOType.DIGITAL_INPUT, "Sliver Break Stop Motion Right", "Optoelectronic Photogate PH2", "W104 (0.75mm² Blue)", "NC"),
            PlcIOItem("X4", "TB2-05", "COM0 (24V+)", PlcIOType.DIGITAL_INPUT, "Bobbin Rail Upper Limit", "Roller Limit Switch SQ2", "W105 (0.75mm² Blue)", "NC"),
            PlcIOItem("X5", "TB2-06", "COM0 (24V+)", PlcIOType.DIGITAL_INPUT, "Bobbin Rail Lower Limit", "Roller Limit Switch SQ3", "W106 (0.75mm² Blue)", "NC"),
            PlcIOItem("X6", "TB2-07", "COM0 (24V+)", PlcIOType.DIGITAL_INPUT, "Main Motor Inverter Trip Fault", "Danfoss FC302 Relay 1 (Term 01/02)", "W107 (0.75mm² Blue)", "NC"),
            PlcIOItem("X7", "TB2-08", "COM0 (24V+)", PlcIOType.DIGITAL_INPUT, "Drafting Inverter Trip Fault", "Danfoss FC302 Relay 2", "W108 (0.75mm² Blue)", "NC"),
            PlcIOItem("X10", "TB2-09", "COM0 (24V+)", PlcIOType.HIGH_SPEED_COUNTER, "Front Roller Encoder A Phase", "Autonics ENC 1024 PPR (Phase A)", "W109 (Twisted Shield)", "Pulse"),
            PlcIOItem("X11", "TB2-10", "COM0 (24V+)", PlcIOType.HIGH_SPEED_COUNTER, "Front Roller Encoder B Phase", "Autonics ENC 1024 PPR (Phase B)", "W110 (Twisted Shield)", "Pulse"),
            PlcIOItem("X12", "TB2-11", "COM0 (24V+)", PlcIOType.DIGITAL_INPUT, "Doffing Cycle Start PB", "Illuminated Pushbutton SB3", "W111 (0.75mm² Blue)", "NO"),
            PlcIOItem("X13", "TB2-12", "COM0 (24V+)", PlcIOType.DIGITAL_INPUT, "Pneumatic Pressure Low (<4.5 Bar)", "SMC Digital Pressure Switch ISE30A", "W112 (0.75mm² Blue)", "NC"),
            PlcIOItem("Y0", "TB3-01", "COM1 (24V+)", PlcIOType.DIGITAL_OUTPUT, "Main Line Contactor Enable (KM1)", "Siemens 3RT2026 Contactor Coil", "W201 (1.0mm² Red)", "NO"),
            PlcIOItem("Y1", "TB3-02", "COM1 (24V+)", PlcIOType.DIGITAL_OUTPUT, "Main Spindle Inverter Run Forward", "Danfoss FC302 Terminal 18 (DI)", "W202 (0.75mm² Blue)", "NO"),
            PlcIOItem("Y2", "TB3-03", "COM1 (24V+)", PlcIOType.DIGITAL_OUTPUT, "Drafting Motor Inverter Run Forward", "Danfoss FC302 Terminal 18 (DI)", "W203 (0.75mm² Blue)", "NO"),
            PlcIOItem("Y3", "TB3-04", "COM1 (24V+)", PlcIOType.DIGITAL_OUTPUT, "Suction Fan Motor Contactor (KM2)", "Siemens 3RT2015 Contactor Coil", "W204 (1.0mm² Red)", "NO"),
            PlcIOItem("Y4", "TB3-05", "COM2 (24V+)", PlcIOType.DIGITAL_OUTPUT, "Bobbin Clamping Pneumatic Solenoid", "Festo Solenoid Valve 24VDC", "W205 (0.75mm² Blue)", "NO"),
            PlcIOItem("Y5", "TB3-06", "COM2 (24V+)", PlcIOType.DIGITAL_OUTPUT, "Tower Beacon Red (Fault Alarm)", "Patlite Warning Tower Red Light", "W206 (0.75mm² Blue)", "NO"),
            PlcIOItem("Y6", "TB3-07", "COM2 (24V+)", PlcIOType.DIGITAL_OUTPUT, "Tower Beacon Amber (Sliver Break)", "Patlite Warning Tower Amber Light", "W207 (0.75mm² Blue)", "NO"),
            PlcIOItem("Y7", "TB3-08", "COM2 (24V+)", PlcIOType.DIGITAL_OUTPUT, "Tower Beacon Green (Machine Running)", "Patlite Warning Tower Green Light", "W208 (0.75mm² Blue)", "NO")
        )

        val parts = listOf(
            PartItem(1, "QF1", "NSX100F 63A", "Schneider Electric", "Molded Case Circuit Breaker 63A 3P 36kA", "63A 3P 400V", 1, "Main Power Bay"),
            PartItem(2, "KM1", "3RT2026-1BB40", "Siemens", "Main Line Power Contactor 25A 24VDC Coil", "25A 11kW AC-3", 1, "Main Power Bay"),
            PartItem(3, "INV1", "131B0044 (FC-302P18KT4)", "Danfoss", "VLT AutomationDrive FC302 18.5kW IP20", "18.5kW 37.5A 380-480V", 1, "Inverter Rack"),
            PartItem(4, "INV2", "131B0032 (FC-302P5K5T4)", "Danfoss", "VLT AutomationDrive FC302 5.5kW IP20", "5.5kW 13.0A 380-480V", 1, "Inverter Rack"),
            PartItem(5, "PLC-CPU", "FX5U-64MR/ES", "Mitsubishi Electric", "MELSEC iQ-F Series PLC CPU Unit 32DI/32DO", "24VDC In / Relay Out", 1, "Control Enclosure"),
            PartItem(6, "PLC-COM", "FX5-485ADP", "Mitsubishi Electric", "RS-485 Serial Communication Adapter", "Modbus RTU Master", 1, "Control Enclosure"),
            PartItem(7, "HMI", "GS2110-WTBD", "Mitsubishi Electric", "GOT2000 10.4-inch Color TFT Touchscreen", "24VDC Ethernet/RS422", 1, "Front Operator Panel"),
            PartItem(8, "G1", "QUINT-PS/1AC/24DC/10", "Phoenix Contact", "Industrial Switched Mode Power Supply", "100-240VAC In, 24VDC 10A Out", 1, "Control Enclosure"),
            PartItem(9, "K1", "PNOZ s4 750104", "Pilz", "Safety Relay Dual Channel E-Stop Monitor", "24VDC, 3 n/o, 1 n/c", 1, "Safety Rail"),
            PartItem(10, "ENC1", "ENC-1-1-T-24", "Autonics", "Wheel Type Incremental Rotary Encoder", "1024 PPR, Totem Pole, 12-24V", 1, "Front Drafting Roller Shaft"),
            PartItem(11, "BR1", "CBR-V 40R 2000W", "Danfoss / Danotherm", "Heavy Duty Ceramic Braking Resistor", "40 Ohm, 2000 Watt Continuous", 1, "Roof Exhaust Bay"),
            PartItem(12, "SQ1", "PR18-8DN", "Autonics", "Inductive Proximity Sensor M18 NPN NO", "8mm sensing range, 10-30VDC", 4, "Drafting Roller Housings")
        )

        val wireGauges = listOf(
            WireGaugeItem("Mains Supply 380V", "16 mm² (6 AWG)", "Black / Brown / Grey + Green-Yellow", "Rated for 70A, conduit enclosed"),
            WireGaugeItem("18.5kW Spindle Inverter Output", "10 mm² (8 AWG)", "4-Core Shielded VFD Cable (Top cable)", "Braid shield grounded 360° at EMC gland"),
            WireGaugeItem("5.5kW Drafting Motor Output", "4.0 mm² (12 AWG)", "4-Core Shielded VFD Cable", "Minimizes inverter PWM electromagnetic noise"),
            WireGaugeItem("24VDC Control Voltage Bus", "1.5 mm² (16 AWG)", "Dark Blue (+24V), White/Blue (0V)", "DIN EN 60204-1 color standard"),
            WireGaugeItem("PLC Digital Inputs & Outputs", "0.75 mm² (18 AWG)", "Light Blue (DI), Orange (DO)", "Flexible stranded copper with crimp ferrules"),
            WireGaugeItem("RS-485 Modbus Network", "2x0.5 mm² (20 AWG)", "Twisted Shielded Pair (Belden 9841)", "120Ω characteristic impedance with termination resistor")
        )

        val encoderSpecs = listOf(
            EncoderSpecItem("Pin 1", "+24VDC", "Brown", "Encoder Power Supply (12-24VDC)"),
            EncoderSpecItem("Pin 2", "0V / GND", "Blue", "Common Power Ground"),
            EncoderSpecItem("Pin 3", "Channel A", "Black", "A Phase Incremental Pulse (1024 PPR)"),
            EncoderSpecItem("Pin 4", "Channel B", "White", "B Phase 90° Quad Pulse (Directional)"),
            EncoderSpecItem("Pin 5", "Channel Z", "Orange", "Zero Index Reference Pulse (1 pulse/rev)"),
            EncoderSpecItem("Shield", "Cable Shield", "Bare Braid", "Connected directly to PE ground bar")
        )

        val terminalBlocks = listOf(
            TerminalBlock(
                name = "TB1 - High Voltage Mains",
                terminalCount = 12,
                description = "380-480V 3-phase power distribution and motor output feeds",
                entries = listOf(
                    TerminalEntry("TB1-01", "QF1-L1", "Mains Supply L1", "L1-CAB-01", "380VAC Phase 1"),
                    TerminalEntry("TB1-02", "QF1-L2", "Mains Supply L2", "L2-CAB-01", "380VAC Phase 2"),
                    TerminalEntry("TB1-03", "QF1-L3", "Mains Supply L3", "L3-CAB-01", "380VAC Phase 3"),
                    TerminalEntry("TB1-04", "KM1-T1", "Spindle Motor U", "U-M1-01", "Inverter Motor Phase U"),
                    TerminalEntry("TB1-05", "KM1-T2", "Spindle Motor V", "V-M1-01", "Inverter Motor Phase V"),
                    TerminalEntry("TB1-06", "KM1-T3", "Spindle Motor W", "W-M1-01", "Inverter Motor Phase W")
                )
            ),
            TerminalBlock(
                name = "TB2 - 24VDC Digital Field Inputs",
                terminalCount = 20,
                description = "Machine bed proximity sensors, limits, and safety circuits",
                entries = listOf(
                    TerminalEntry("TB2-01", "PLC-X0", "Safety Relay K1 (13/14)", "S-ESTOP", "E-Stop Safety Loop Feedback"),
                    TerminalEntry("TB2-02", "PLC-X1", "SQ1 Front Roller Sensor", "S-FR-JAM", "Front Roller Jam Stop"),
                    TerminalEntry("TB2-03", "PLC-X2", "PH1 Left Sliver Sensor", "S-SLV-L", "Sliver Break Detection"),
                    TerminalEntry("TB2-04", "PLC-X3", "PH2 Right Sliver Sensor", "S-SLV-R", "Sliver Break Detection"),
                    TerminalEntry("TB2-05", "PLC-X4", "SQ2 Upper Limit Switch", "S-RAIL-UP", "Bobbin Rail Over-travel Protection")
                )
            )
        )

        return Machine(
            id = "mach-01",
            name = "FA494 Computerized Roving Frame",
            model = "FA494 Fly Frame",
            manufacturer = "Jingwei Textile Machinery",
            category = MachineCategory.TEXTILE,
            powerRating = "18.5 kW + 5.5 kW drafting",
            supplyVoltage = "380-415V AC 3-Phase 50Hz",
            controlVoltage = "24V DC / 220V AC",
            fullLoadCurrent = "48.5 A",
            plcModel = "Mitsubishi FX5U-64MR/ES",
            inverterModel = "Danfoss VLT AutomationDrive FC302 (18.5kW + 5.5kW)",
            description = "High-speed computerized roving / speed frame machine featuring 4-roller drafting, synchronized inverter drives, touch panel HMI, differential encoder feedback, and 24-page complete electrical schematics.",
            isPremium = true,
            specs = listOf(
                SpecItem("General", "Spindle Count", "120 - 156 spindles"),
                SpecItem("General", "Spindle Speed", "400 - 1400 rpm", "rpm"),
                SpecItem("Drafting", "Drafting System", "4-roller double apron with pneumatic weighting"),
                SpecItem("Drafting", "Draft Ratio", "4.2 - 12.5"),
                SpecItem("Electrical", "Main Motor", "18.5 kW 4-pole Squirrel Cage", "kW"),
                SpecItem("Electrical", "Drafting Servo Motor", "5.5 kW Synchronous Servo", "kW"),
                SpecItem("Electrical", "Control PLC", "Mitsubishi FX5U-64MR/ES"),
                SpecItem("Electrical", "HMI Touchscreen", "GOT2000 10.4 inch TFT Color"),
                SpecItem("Safety", "Safety Classification", "EN ISO 13849-1 Category 4, PLe"),
                SpecItem("Pneumatics", "Operating Air Pressure", "0.6 - 0.8 MPa (6-8 Bar)", "MPa")
            ),
            plcIOList = plcIoList,
            partsList = parts,
            schematicPages = pages,
            wireGauges = wireGauges,
            encoderSpecs = encoderSpecs,
            terminalBlocks = terminalBlocks
        )
    }

    // 2. Danfoss VLT AutomationDrive FC302
    private fun createDanfossFC302Drive(): Machine {
        val pages = (1..18).map { pageNum ->
            SchematicPage(
                pageNumber = pageNum,
                title = "Page $pageNum: FC302 Terminal Diagram & Control Board (Slot A/B)",
                dwgCode = "DANFOSS-FC302-E0$pageNum",
                description = "Danfoss FC302 high performance terminal wiring, RS-485 Modbus RTU, 24VDC logic inputs (18, 19, 27, 29, 32, 33), and Safe Torque Off (STO Term 37).",
                components = listOf(
                    SchematicComponent("FC302_CORE", "FC302 Control Board", "FC302", 300f, 150f, 250f, 180f, listOf("Term 12 (+24V)", "Term 18 (Start)", "Term 27 (Coast)", "Term 37 (STO)", "Term 68 (RS485+)", "Term 69 (RS485-)"))
                ),
                wireTraces = listOf(
                    WireTrace("STO_LOOP", "Safe Torque Off SIL3 Dual Channel", 0xFFFF1744, "24V DC", listOf(SchematicPoint(100f, 180f), SchematicPoint(300f, 180f))),
                    WireTrace("RS485_A", "Modbus RTU Data +", 0xFF00E5FF, "RS-485", listOf(SchematicPoint(100f, 220f), SchematicPoint(300f, 220f)))
                )
            )
        }

        return Machine(
            id = "mach-02",
            name = "Danfoss VLT AutomationDrive FC 302",
            model = "FC-302P18KT4",
            manufacturer = "Danfoss Drives",
            category = MachineCategory.INVERTER_DRIVE,
            powerRating = "18.5 kW (25 HP)",
            supplyVoltage = "380-500V AC 3-Phase",
            controlVoltage = "24V DC Internal / External",
            fullLoadCurrent = "37.5 A @ 400V",
            plcModel = "Integrated VLT Motion Controller / Fieldbus",
            inverterModel = "FC302 Industrial Vector Controller",
            description = "Flagship industrial variable frequency drive for asynchronous and permanent magnet motors, featuring Safe Torque Off (STO), built-in DC chokes, coated PCBs, and Modbus/Profinet communication.",
            isPremium = true,
            specs = listOf(
                SpecItem("Ratings", "Continuous Output Current", "37.5 A"),
                SpecItem("Ratings", "Intermittent Current (60s)", "60.0 A (160% torque)"),
                SpecItem("Efficiency", "Efficiency at Rated Load", "98.2%"),
                SpecItem("I/O", "Programmable Digital Inputs", "6 (PNP or NPN 24V)"),
                SpecItem("I/O", "Analog Inputs", "2 (0-10V or 0/4-20mA)"),
                SpecItem("I/O", "Relay Outputs", "2 Form C (240VAC 2A)"),
                SpecItem("Safety", "Functional Safety", "STO (Safe Torque Off) SIL 3 / PL e")
            ),
            plcIOList = listOf(
                PlcIOItem("Term 12", "TB-CTRL-12", "Internal 24V", PlcIOType.DIGITAL_OUTPUT, "+24VDC Logic Supply (200mA max)", "Internal Supply", "W012 (0.75mm²)", "24VDC"),
                PlcIOItem("Term 18", "TB-CTRL-18", "COM 20 (0V)", PlcIOType.DIGITAL_INPUT, "Digital Input 18 (Start/Stop Forward)", "Pushbutton / PLC DO", "W018 (0.75mm²)", "NO"),
                PlcIOItem("Term 19", "TB-CTRL-19", "COM 20 (0V)", PlcIOType.DIGITAL_INPUT, "Digital Input 19 (Reversing / Preset Ref)", "Selector Switch", "W019 (0.75mm²)", "NO"),
                PlcIOItem("Term 27", "TB-CTRL-27", "COM 20 (0V)", PlcIOType.DIGITAL_INPUT, "Digital Input 27 (Coast Inverse / Safe Stop)", "E-Stop Safety Chain", "W027 (0.75mm²)", "NC"),
                PlcIOItem("Term 29", "TB-CTRL-29", "COM 20 (0V)", PlcIOType.DIGITAL_INPUT, "Digital Input 29 (Jog Speed 15Hz)", "Jog Button", "W029 (0.75mm²)", "NO"),
                PlcIOItem("Term 37", "TB-CTRL-37", "0V / STO Ground", PlcIOType.DIGITAL_INPUT, "Safe Torque Off (STO Terminal 37)", "Dual Channel Safety Relay", "W037 (Shielded)", "NC (Dual)"),
                PlcIOItem("Term 53", "TB-CTRL-53", "GND 55", PlcIOType.ANALOG_INPUT, "Analog Input 53 (0-10V Master Speed Ref)", "Potentiometer / PLC AO", "W053 (Twisted Pair)", "0-10V"),
                PlcIOItem("Term 54", "TB-CTRL-54", "GND 55", PlcIOType.ANALOG_INPUT, "Analog Input 54 (4-20mA Dancer Feedback)", "Ultrasonic Dancer Arm", "W054 (Twisted Pair)", "4-20mA"),
                PlcIOItem("Term 68", "TB-CTRL-68", "RS485 COM 61", PlcIOType.COMMUNICATION, "Modbus RTU Data (+) RS485 Terminal 68", "PLC Serial Card", "W068 (Shielded Pair)", "RS-485+"),
                PlcIOItem("Term 69", "TB-CTRL-69", "RS485 COM 61", PlcIOType.COMMUNICATION, "Modbus RTU Data (-) RS485 Terminal 69", "PLC Serial Card", "W069 (Shielded Pair)", "RS-485-")
            ),
            partsList = listOf(
                PartItem(1, "INV1", "131B0044", "Danfoss", "VLT AutomationDrive FC-302P18KT4E20H1", "18.5kW 380-480V IP20", 1, "Drive Cabinet"),
                PartItem(2, "LCP102", "130B1107", "Danfoss", "Numerical/Graphical Local Control Panel", "Hot-pluggable IP65 front", 1, "Cabinet Door"),
                PartItem(3, "MCA120", "130B1135", "Danfoss", "Profinet Dual Port Fieldbus Option", "Industrial Ethernet 100Mbps", 1, "Slot A"),
                PartItem(4, "BR1", "175U2841", "Danfoss", "Braking Resistor 40 Ohm 1400W", "External IP54 Aluminum case", 1, "Roof Bay")
            ),
            schematicPages = pages,
            wireGauges = listOf(
                WireGaugeItem("Mains Input 400V", "10 mm²", "Black/Brown/Grey", "Copper 75°C rated"),
                WireGaugeItem("Motor Feed Cable", "10 mm² 4-Core", "Shielded VFD Cable", "Screen grounded at drive & motor"),
                WireGaugeItem("Control Wiring", "0.75 mm²", "Dark Blue / White", "Ferruled ends, stripped 8mm")
            ),
            encoderSpecs = listOf(
                EncoderSpecItem("Slot B", "MCB102", "D-Sub 9", "Option card for 5V TTL incremental encoder (1024-4096 PPR)")
            ),
            terminalBlocks = listOf(
                TerminalBlock("TB-CTRL", 12, "Danfoss FC302 Pluggable Terminal Strip", listOf(
                    TerminalEntry("12", "Internal", "Field 24V+", "W12", "+24V Supply"),
                    TerminalEntry("18", "Control Logic", "Start Pushbutton", "W18", "Digital Input 18 Start"),
                    TerminalEntry("37", "Safety Relay", "Pilz Safety Out", "W37", "Safe Torque Off")
                ))
            )
        )
    }

    // 3. Danfoss FC 360 Industrial Inverter
    private fun createDanfossFC360Drive(): Machine {
        val pages = (1..12).map { pageNum ->
            SchematicPage(
                pageNumber = pageNum,
                title = "Page $pageNum: Danfoss FC360 Dedicated Inverter Circuit",
                dwgCode = "DANFOSS-FC360-E$pageNum",
                description = "Compact OEM variable frequency drive schematic for textile, food and packaging machinery.",
                components = listOf(
                    SchematicComponent("FC360", "Danfoss FC 360 OEM Drive", "FC360", 350f, 150f, 220f, 150f, listOf("L1", "L2", "L3", "U", "V", "W", "+24V", "DI1", "DI2", "GND"))
                ),
                wireTraces = listOf(
                    WireTrace("FC360_RUN", "Run Command Loop", 0xFF00E5FF, "24V DC", listOf(SchematicPoint(150f, 200f), SchematicPoint(350f, 200f)))
                )
            )
        }

        return Machine(
            id = "mach-03",
            name = "Danfoss VLT AutomationDrive FC 360",
            model = "FC-360P11KT4",
            manufacturer = "Danfoss Drives",
            category = MachineCategory.INVERTER_DRIVE,
            powerRating = "11.0 kW (15 HP)",
            supplyVoltage = "380-480V AC 3-Phase",
            controlVoltage = "24V DC",
            fullLoadCurrent = "23.0 A @ 400V",
            plcModel = "Modbus RTU Integrated",
            inverterModel = "FC360 General Industrial Inverter",
            description = "Robust and cost-effective variable frequency drive specially engineered for OEM textile machinery, winding machines, and pumps with high starting torque.",
            isPremium = false,
            specs = listOf(
                SpecItem("Ratings", "Rated Power", "11.0 kW"),
                SpecItem("Ratings", "Rated Output Current", "23.0 A"),
                SpecItem("Protection", "Enclosure Rating", "IP20 Compact Bookstyle"),
                SpecItem("Ambient", "Operating Temp", "-10°C to +50°C without derating")
            ),
            plcIOList = listOf(
                PlcIOItem("Term 12", "TB1-12", "24V Internal", PlcIOType.DIGITAL_OUTPUT, "+24VDC Supply (100mA)", "Internal Drive Supply", "W12 (0.5mm²)", "24VDC"),
                PlcIOItem("Term 18", "TB1-18", "0V (Term 20)", PlcIOType.DIGITAL_INPUT, "Digital Input 18: Forward Run", "External Contactor Aux", "W18 (0.5mm²)", "NO"),
                PlcIOItem("Term 19", "TB1-19", "0V (Term 20)", PlcIOType.DIGITAL_INPUT, "Digital Input 19: Reverse Run", "Reverse PB", "W19 (0.5mm²)", "NO"),
                PlcIOItem("Term 27", "TB1-27", "0V (Term 20)", PlcIOType.DIGITAL_INPUT, "Digital Input 27: Fault Reset", "Reset Pushbutton", "W27 (0.5mm²)", "NO")
            ),
            partsList = listOf(
                PartItem(1, "INV_FC360", "134F2983", "Danfoss", "FC-360P11KT4E20H2", "11kW 400V IP20", 1, "Drive Subpanel"),
                PartItem(2, "LCP21", "132B0254", "Danfoss", "Numeric LCP Display Panel", "Plug-in panel", 1, "Drive Front")
            ),
            schematicPages = pages,
            wireGauges = listOf(
                WireGaugeItem("Mains Line", "6.0 mm²", "Black/Brown/Grey", "400V 3-Phase"),
                WireGaugeItem("Motor Cable", "6.0 mm²", "Shielded 4-Core", "EMC compliant")
            ),
            encoderSpecs = emptyList(),
            terminalBlocks = listOf(
                TerminalBlock("TB-FC360", 8, "FC360 Control Terminals", listOf(
                    TerminalEntry("12", "24VDC", "External Loop", "W12", "+24V Supply"),
                    TerminalEntry("18", "DI-18", "PLC Y0", "W18", "Run Command")
                ))
            )
        )
    }

    // 4. Siemens S7-1200 Automation Panel
    private fun createSiemensS71200Panel(): Machine {
        val pages = (1..16).map { pageNum ->
            SchematicPage(
                pageNumber = pageNum,
                title = "Page $pageNum: Siemens S7-1200 CPU 1214C Wiring Schematic",
                dwgCode = "SIEMENS-S7-1200-E0$pageNum",
                description = "CPU 1214C DC/DC/DC, 14 Digital Inputs (24VDC sink/source), 10 Transistor Outputs (0.5A 24VDC), 2 Analog Inputs (0-10V).",
                components = listOf(
                    SchematicComponent("S7_1200", "Siemens S7-1200 CPU 1214C", "CPU1214C", 300f, 150f, 260f, 200f, listOf("L+", "M", "1M", "%IX0.0", "%IX0.1", "%IX0.2", "3M", "%QX0.0", "%QX0.1", "PROFINET"))
                ),
                wireTraces = listOf(
                    WireTrace("PROFINET_BUS", "Profinet Industrial Ethernet Green Cable", 0xFF00E676, "Ethernet 100Mbps", listOf(SchematicPoint(100f, 280f), SchematicPoint(300f, 280f)))
                )
            )
        }

        return Machine(
            id = "mach-04",
            name = "Siemens S7-1200 Industrial Control Panel",
            model = "CPU 1214C DC/DC/DC (6ES7214-1AG40-0XB0)",
            manufacturer = "Siemens AG",
            category = MachineCategory.PLC_PANEL,
            powerRating = "Control System 24VDC 500W",
            supplyVoltage = "24V DC (20.4 to 28.8 VDC)",
            controlVoltage = "24V DC",
            fullLoadCurrent = "12.0 A (Aux System)",
            plcModel = "Simatic S7-1200 CPU 1214C",
            inverterModel = "Sinamics G120C via Profinet",
            description = "Standard modular industrial PLC control cabinet schematic featuring S7-1200 CPU, SM 1223 expansion modules, KTP700 Basic HMI, SITOP power supply, and safety relay integration.",
            isPremium = true,
            specs = listOf(
                SpecItem("Processor", "Work Memory", "100 KB Integrated"),
                SpecItem("I/O", "Onboard Digital Inputs", "14 DI 24VDC (IEC Type 1 sink/source)"),
                SpecItem("I/O", "Onboard Digital Outputs", "10 DQ 24VDC Transistor 0.5A"),
                SpecItem("I/O", "Onboard Analog Inputs", "2 AI (0 to 10V DC, 10-bit resolution)"),
                SpecItem("Comm", "Profinet Interface", "1 RJ45 port (10/100 Mbit/s)")
            ),
            plcIOList = listOf(
                PlcIOItem("%IX0.0", "X10-1", "1M (0VDC)", PlcIOType.DIGITAL_INPUT, "Main Safety E-Stop Healthy Status", "Safety Relay Aux Contact", "W001", "NC"),
                PlcIOItem("%IX0.1", "X10-2", "1M (0VDC)", PlcIOType.DIGITAL_INPUT, "Control Voltage 24V OK", "SITOP Power Supply Aux", "W002", "NO"),
                PlcIOItem("%IX0.2", "X10-3", "1M (0VDC)", PlcIOType.DIGITAL_INPUT, "Machine Guard Safety Interlock", "Euchner Guard Switch", "W003", "NC"),
                PlcIOItem("%IX0.3", "X10-4", "1M (0VDC)", PlcIOType.DIGITAL_INPUT, "Start Pushbutton Green", "Siemens Sirius Pushbutton", "W004", "NO"),
                PlcIOItem("%QX0.0", "X11-1", "3L+ (+24V)", PlcIOType.DIGITAL_OUTPUT, "Main Contactor Enable KM1", "Sirius 3RT20 Contactor Coil", "W101", "NO"),
                PlcIOItem("%QX0.1", "X11-2", "3L+ (+24V)", PlcIOType.DIGITAL_OUTPUT, "Hydraulic Pack Run Command", "Pump Starter Solenoid", "W102", "NO"),
                PlcIOItem("%IW64", "X12-1", "2M (Analog GND)", PlcIOType.ANALOG_INPUT, "Chamber Temperature Sensor", "PT100 Transmitter 0-10V", "W201 (Shielded)", "0-10V")
            ),
            partsList = listOf(
                PartItem(1, "PLC_CPU", "6ES7214-1AG40-0XB0", "Siemens", "SIMATIC S7-1200 CPU 1214C DC/DC/DC", "14DI/10DQ/2AI 100KB", 1, "Control Panel DIN Rail"),
                PartItem(2, "SM1223", "6ES7223-1BL32-0XB0", "Siemens", "Digital I/O Extension Module 16DI/16DQ", "24VDC 0.5A Transistor", 1, "Control Panel DIN Rail"),
                PartItem(3, "HMI", "6AV2123-2GB03-0AX0", "Siemens", "SIMATIC HMI KTP700 Basic 7-inch", "TFT 800x480 Profinet", 1, "Panel Front Door"),
                PartItem(4, "PSU", "6EP1334-3BA10", "Siemens", "SITOP PSU8200 24V/10A Stabilized Power", "120-230VAC in, 24V 10A out", 1, "Power Rail")
            ),
            schematicPages = pages,
            wireGauges = listOf(
                WireGaugeItem("24V Supply Bus", "2.5 mm²", "Dark Blue (+24V), White/Blue (M)", "Main 10A distribution"),
                WireGaugeItem("PLC DI/DQ Wiring", "0.75 mm²", "Dark Blue / Violet", "Class 5 stranded copper"),
                WireGaugeItem("Profinet Cable", "Cat5e SF/UTP", "Green Industrial Ethernet", "Profinet Type B flex")
            ),
            encoderSpecs = listOf(
                EncoderSpecItem("%IX0.0", "HSC1 Clock", "Brown", "Phase A pulse high speed counter up to 100 kHz")
            ),
            terminalBlocks = listOf(
                TerminalBlock("TB-S7", 14, "CPU 1214C Input Terminal Block", listOf(
                    TerminalEntry("1", "L+", "24VDC Bus", "W-PWR-01", "+24V CPU Power"),
                    TerminalEntry("2", "M", "0VDC Bus", "W-PWR-02", "0V Ground")
                ))
            )
        )
    }

    // 5. Mitsubishi MELSEC iQ-F FX5U Panel
    private fun createMitsubishiFX5UPanel(): Machine {
        val pages = (1..16).map { pageNum ->
            SchematicPage(
                pageNumber = pageNum,
                title = "Page $pageNum: Mitsubishi FX5U-64MR/ES Schematic",
                dwgCode = "MITSUBISHI-FX5U-E0$pageNum",
                description = "FX5U high speed controller with embedded 4-axis 200kHz pulse outputs, 32 inputs (X0-X37), 32 relay outputs (Y0-Y37), and built-in Ethernet.",
                components = listOf(
                    SchematicComponent("FX5U", "Mitsubishi FX5U-64MR/ES", "FX5U", 320f, 150f, 240f, 180f, listOf("L", "N", "24+", "24-", "X0..X37", "Y0..Y37", "ETH"))
                ),
                wireTraces = listOf(
                    WireTrace("FX_PULSE", "High Speed Pulse Stream (200kHz)", 0xFF00E5FF, "Pulse/Dir", listOf(SchematicPoint(120f, 200f), SchematicPoint(320f, 200f)))
                )
            )
        }

        return Machine(
            id = "mach-05",
            name = "Mitsubishi MELSEC iQ-F FX5U Control Panel",
            model = "FX5U-64MR/ES",
            manufacturer = "Mitsubishi Electric",
            category = MachineCategory.PLC_PANEL,
            powerRating = "Control System 220VAC 150W",
            supplyVoltage = "100-240V AC 50/60Hz",
            controlVoltage = "24V DC Internal 400mA Supply",
            fullLoadCurrent = "0.8 A @ 220VAC",
            plcModel = "MELSEC iQ-F FX5U-64MR/ES",
            inverterModel = "Mitsubishi FR-A800 / FR-E800",
            description = "High-performance compact PLC automation panel widely used in textile roving frames, packaging, and automatic assembly systems.",
            isPremium = true,
            specs = listOf(
                SpecItem("CPU", "Program Capacity", "64K steps / 128K steps"),
                SpecItem("I/O", "Number of Inputs", "32 inputs (sink/source)"),
                SpecItem("I/O", "Number of Outputs", "32 relay outputs (2A/point)"),
                SpecItem("High-Speed", "High Speed Counter", "8 channels (up to 200 kHz)"),
                SpecItem("Network", "Built-in Ports", "Ethernet 100BASE-TX, RS-485")
            ),
            plcIOList = listOf(
                PlcIOItem("X0", "TB1-1", "S/S (+24V)", PlcIOType.DIGITAL_INPUT, "System Auto/Manual Selector Switch", "IDEC Selector 2-Pos", "W101", "NO"),
                PlcIOItem("X1", "TB1-2", "S/S (+24V)", PlcIOType.DIGITAL_INPUT, "Cycle Start Pushbutton", "Illuminated Green PB", "W102", "NO"),
                PlcIOItem("X2", "TB1-3", "S/S (+24V)", PlcIOType.DIGITAL_INPUT, "Cycle Stop Pushbutton", "Red Mushroom Head PB", "W103", "NC"),
                PlcIOItem("Y0", "TB2-1", "COM0 (24V+)", PlcIOType.DIGITAL_OUTPUT, "Auto Cycle Running Pilot Lamp", "LED Indicator Green", "W201", "NO"),
                PlcIOItem("Y1", "TB2-2", "COM0 (24V+)", PlcIOType.DIGITAL_OUTPUT, "Pneumatic Clamp Solenoid Valve", "SMC Solenoid 24VDC", "W202", "NO")
            ),
            partsList = listOf(
                PartItem(1, "FX5U", "FX5U-64MR/ES", "Mitsubishi Electric", "iQ-F Series PLC CPU 32DI/32DO Relay", "100-240VAC 30W", 1, "Cabinet Rail")
            ),
            schematicPages = pages,
            wireGauges = listOf(
                WireGaugeItem("Mains Supply", "1.5 mm²", "Black/Blue (L/N)", "220VAC Line")
            ),
            encoderSpecs = listOf(
                EncoderSpecItem("X0/X1", "Phase A/B", "Shielded", "2-Phase 200kHz input channel 1")
            ),
            terminalBlocks = listOf(
                TerminalBlock("TB-FX", 10, "FX5U Input Terminal Block", listOf(
                    TerminalEntry("1", "S/S", "24V Internal", "W-SS", "Sink/Source Select")
                ))
            )
        )
    }

    // 6. Rieter G35 Ring Spinning Machine
    private fun createRieterG35RingFrame(): Machine {
        val pages = (1..24).map { pageNum ->
            SchematicPage(
                pageNumber = pageNum,
                title = "Page $pageNum: Rieter G35 Ring Spinning Electrical Circuit",
                dwgCode = "RIETER-G35-E$pageNum",
                description = "Spindle drive synchronized inverter, ring rail servo electronic lifting, drafting system individual drive.",
                components = listOf(
                    SchematicComponent("G35_MAIN", "Rieter G35 Control Panel", "G35_DRIVE", 300f, 150f, 250f, 180f, listOf("M1_SPINDLE", "M2_DRAFT", "M3_RAIL", "PLC_SYS"))
                ),
                wireTraces = listOf(
                    WireTrace("SPINDLE_SYNC", "Spindle Speed VFD Master Line", 0xFF00E5FF, "400V PWM", listOf(SchematicPoint(100f, 200f), SchematicPoint(300f, 200f)))
                )
            )
        }

        return Machine(
            id = "mach-06",
            name = "Rieter G35 Ring Spinning Frame",
            model = "G35 Ring Frame",
            manufacturer = "Rieter Machine Works Ltd.",
            category = MachineCategory.TEXTILE,
            powerRating = "55.0 kW Main + 15 kW Servos",
            supplyVoltage = "380-440V AC 3-Phase 50Hz",
            controlVoltage = "24V DC / 230V AC",
            fullLoadCurrent = "118.0 A",
            plcModel = "Rieter Industrial Control System (IPC)",
            inverterModel = "ABB / Siemens Multi-Drive Common DC Bus",
            description = "High-productivity ring spinning machine up to 1824 spindles, featuring electronic drafting drive system (FLEXIdraft), individual spindle monitoring (ISM), and energy-saving suction tube control.",
            isPremium = true,
            specs = listOf(
                SpecItem("Capacity", "Spindle Number", "Up to 1824 spindles"),
                SpecItem("Speed", "Mechanical Spindle Speed", "Up to 25,000 rpm", "rpm"),
                SpecItem("Drafting", "Drafting System", "3-roller 2-apron drafting"),
                SpecItem("Power", "Total Connected Load", "75 kW", "kW")
            ),
            plcIOList = listOf(
                PlcIOItem("DI-01", "TB10-1", "24VDC", PlcIOType.DIGITAL_INPUT, "Underwinding Detector Left Side", "Optical Sensor", "W101", "NO"),
                PlcIOItem("DO-01", "TB20-1", "24VDC", PlcIOType.DIGITAL_OUTPUT, "Ring Rail Lower Rapid Descent", "Servo Command", "W201", "NO")
            ),
            partsList = listOf(
                PartItem(1, "M1", "1LG4 223-4AA60", "Siemens", "Main Spindle Squirrel Cage Motor 55kW", "55kW 400V 1475rpm", 1, "Headstock")
            ),
            schematicPages = pages,
            wireGauges = listOf(
                WireGaugeItem("Main Bus", "50 mm²", "Black/Brown/Grey", "400V Mains")
            ),
            encoderSpecs = emptyList(),
            terminalBlocks = emptyList()
        )
    }

    // 7. Trützschler TC 19i Carding Machine
    private fun createTrutzschlerTC19iCard(): Machine {
        val pages = (1..20).map { pageNum ->
            SchematicPage(
                pageNumber = pageNum,
                title = "Page $pageNum: Trützschler TC 19i Intelligent Carding Schematic",
                dwgCode = "TRUTZ-TC19-E$pageNum",
                description = "Piecing sensor, magnetic metal detector, variable speed licker-in inverter, and sliver autoleveller.",
                components = listOf(
                    SchematicComponent("TC19_DRIVE", "Carding Flat & Cylinder Drive", "TC19_PANEL", 300f, 150f, 240f, 160f, listOf("LICKER_IN", "CYLINDER", "DOFFER", "COILER"))
                ),
                wireTraces = listOf(
                    WireTrace("METAL_DET", "High Speed Metal Detection Trip", 0xFFFF1744, "24V Fast", listOf(SchematicPoint(100f, 200f), SchematicPoint(300f, 200f)))
                )
            )
        }

        return Machine(
            id = "mach-07",
            name = "Trützschler TC 19i Intelligent Carding Machine",
            model = "TC 19i",
            manufacturer = "Trützschler Group",
            category = MachineCategory.TEXTILE,
            powerRating = "22.5 kW Total",
            supplyVoltage = "400V AC 3-Phase 50/60Hz",
            controlVoltage = "24V DC",
            fullLoadCurrent = "52.0 A",
            plcModel = "Trützschler Card Control (T-CON)",
            inverterModel = "Danfoss VLT AutomationDrive Multi-inverters",
            description = "Next-generation intelligent carding machine with automatic carding gap setting (T-GO), optical nep monitoring (NEPCONTROL), and magnetic foreign matter separation.",
            isPremium = true,
            specs = listOf(
                SpecItem("Production", "Max Delivery Speed", "Up to 500 m/min", "m/min"),
                SpecItem("Working Width", "Cylinder Width", "1,280 mm", "mm"),
                SpecItem("Sensors", "Gap Sensor Resolution", "0.01 mm (10 microns)", "mm")
            ),
            plcIOList = listOf(
                PlcIOItem("X10", "TB1-10", "24V", PlcIOType.DIGITAL_INPUT, "Metal Infeed Detector Emergency Brake", "High-speed inductive sensor", "W010", "NC")
            ),
            partsList = listOf(
                PartItem(1, "INV_CYL", "FC-302P15K", "Danfoss", "Main Cylinder Inverter 15kW", "15kW 400V", 1, "Drive Cabinet")
            ),
            schematicPages = pages,
            wireGauges = listOf(
                WireGaugeItem("Cylinder Feed", "16 mm²", "Shielded 4-core", "Inverter cable")
            ),
            encoderSpecs = emptyList(),
            terminalBlocks = emptyList()
        )
    }

    // 8. Toyota JAT810 Air Jet Loom
    private fun createToyotaJAT810Loom(): Machine {
        val pages = (1..20).map { pageNum ->
            SchematicPage(
                pageNumber = pageNum,
                title = "Page $pageNum: Toyota JAT810 Weaving Electrical Wiring",
                dwgCode = "TOYOTA-JAT810-E$pageNum",
                description = "Electronic Let-off (ELO), Electronic Take-up (ETU), main pneumatic jet nozzle valves, and warp stop motion.",
                components = listOf(
                    SchematicComponent("JAT810_MAIN", "Toyota Function Panel", "JAT810_CPU", 300f, 150f, 250f, 180f, listOf("ELO_SERVO", "ETU_SERVO", "VALVE_BLOCK", "OPTICAL_WEFT"))
                ),
                wireTraces = listOf(
                    WireTrace("SOL_VALVE", "High-Speed Solenoid Valve 48V Pulse", 0xFFFF9100, "48V DC Pulse", listOf(SchematicPoint(100f, 210f), SchematicPoint(300f, 210f)))
                )
            )
        }

        return Machine(
            id = "mach-08",
            name = "Toyota JAT810 Air Jet Loom",
            model = "JAT810 Weaving Machine",
            manufacturer = "Toyota Industries Corporation",
            category = MachineCategory.WEAVING,
            powerRating = "4.5 kW Main Drive + 2.0 kW Servos",
            supplyVoltage = "380-415V AC 3-Phase",
            controlVoltage = "24V DC / 48V DC (Solenoids)",
            fullLoadCurrent = "18.5 A",
            plcModel = "Toyota Multi-CPU High Speed Loom Controller",
            inverterModel = "Toyota Inverter Drive System",
            description = "World-leading air jet weaving loom engineered for ultra-high-speed fabric production with energy-saving sub-nozzle technology and electronic warp tension control.",
            isPremium = true,
            specs = listOf(
                SpecItem("Speed", "Maximum Insertion Rate", "1,250 rpm (fabric dependent)", "rpm"),
                SpecItem("Weft Selection", "Color Capability", "Up to 8 colors electronic selector")
            ),
            plcIOList = listOf(
                PlcIOItem("I0.1", "TB1-1", "24V", PlcIOType.DIGITAL_INPUT, "Warp Yarn Broken Left Motion", "Drop Wire Electric Bar", "W001", "NO")
            ),
            partsList = listOf(
                PartItem(1, "VALVE_SUB", "SYJ5140-5LZ", "SMC", "High Frequency 3-Way Sub-nozzle Valve", "24VDC 0.35W 1200cpm", 8, "Nozzle Rail")
            ),
            schematicPages = pages,
            wireGauges = listOf(
                WireGaugeItem("Servo Power", "2.5 mm²", "Shielded 4-core", "ELO/ETU Servos")
            ),
            encoderSpecs = emptyList(),
            terminalBlocks = emptyList()
        )
    }

    // 9. Delta DVP-28SV2 PLC + ASDA-A2 Servo
    private fun createDeltaDvpServoSystem(): Machine {
        val pages = (1..14).map { pageNum ->
            SchematicPage(
                pageNumber = pageNum,
                title = "Page $pageNum: Delta DVP28SV2 & ASDA-A2 Servo Wiring",
                dwgCode = "DELTA-DVP-E$pageNum",
                description = "High-speed differential pulse line driver CN1 connection, electronic gear ratio, and RS-485 Modbus ASCII/RTU.",
                components = listOf(
                    SchematicComponent("DELTA_PLC", "Delta DVP-28SV2", "DVP28SV2", 200f, 150f, 160f, 150f, listOf("X0", "X1", "Y0", "Y1", "COM", "RS485")),
                    SchematicComponent("ASDA_A2", "Delta ASDA-A2 1kW Servo", "ASDA_A2", 500f, 150f, 180f, 160f, listOf("PULSE+", "PULSE-", "SIGN+", "SIGN-", "ALARM", "SON"))
                ),
                wireTraces = listOf(
                    WireTrace("DELTA_PULSE", "High Speed Line Driver 200kHz", 0xFF00E5FF, "5V Diff", listOf(SchematicPoint(360f, 190f), SchematicPoint(500f, 190f)))
                )
            )
        }

        return Machine(
            id = "mach-09",
            name = "Delta DVP-28SV2 & ASDA-A2 Motion Panel",
            model = "DVP28SV211T + ASD-A2-1021-M",
            manufacturer = "Delta Electronics",
            category = MachineCategory.PLC_PANEL,
            powerRating = "1.0 kW Servo + 24V PLC",
            supplyVoltage = "220V AC 1-Phase / 3-Phase",
            controlVoltage = "24V DC",
            fullLoadCurrent = "7.5 A",
            plcModel = "Delta DVP-28SV2 (Transistor High Speed)",
            inverterModel = "Delta ASDA-A2 20-bit Absolute Servo",
            description = "Precision motion control system with 200kHz 4-axis pulse output, 20-bit optical encoder feedback (1,280,000 pulses/rev), and built-in electronic cam functions.",
            isPremium = false,
            specs = listOf(
                SpecItem("Speed", "Encoder Resolution", "20-bit (1,280,000 ppr)"),
                SpecItem("Pulse Rate", "Max Input Pulse Frequency", "500 kpps Differential / 200 kpps Open Collector")
            ),
            plcIOList = listOf(
                PlcIOItem("Y0", "CN1-41/43", "5V Differential", PlcIOType.DIGITAL_OUTPUT, "Axis 1 Pulse Train (+/-)", "Servo CN1 Pulse Input", "W001 (Twisted)", "Pulse"),
                PlcIOItem("Y1", "CN1-37/39", "5V Differential", PlcIOType.DIGITAL_OUTPUT, "Axis 1 Direction (+/-)", "Servo CN1 Sign Input", "W002 (Twisted)", "Dir")
            ),
            partsList = listOf(
                PartItem(1, "SERVO_DRV", "ASD-A2-1021-M", "Delta", "ASDA-A2 1kW AC Servo Drive", "220V 1kW Modbus/CANopen", 1, "Drive Bay")
            ),
            schematicPages = pages,
            wireGauges = listOf(
                WireGaugeItem("Servo Motor Cable", "2.5 mm²", "4-core with shield", "Motor Power U, V, W, PE")
            ),
            encoderSpecs = listOf(
                EncoderSpecItem("CN2", "20-bit Absolute", "Serial", "High-speed serial communication line (Delta proprietary)")
            ),
            terminalBlocks = emptyList()
        )
    }

    // 10. Murata QPRO Plus Automatic Cone Winder
    private fun createMurataQProWinder(): Machine {
        val pages = (1..18).map { pageNum ->
            SchematicPage(
                pageNumber = pageNum,
                title = "Page $pageNum: Murata QPRO Automatic Winder Schematic",
                dwgCode = "MURATA-QPRO-E$pageNum",
                description = "Yarn clearer electronic sensor, splicer air valve control, and drum inverter drive.",
                components = listOf(
                    SchematicComponent("QPRO_DRUM", "Drum Inverter & Clearer", "QPRO_SPINDLE", 300f, 150f, 220f, 160f, listOf("CLEARER", "SPLICER", "INVERTER", "BAL-CON"))
                ),
                wireTraces = listOf(
                    WireTrace("CLEARER_BUS", "Optical Yarn Clearer Signal", 0xFF00E5FF, "CanBus", listOf(SchematicPoint(100f, 190f), SchematicPoint(300f, 190f)))
                )
            )
        }

        return Machine(
            id = "mach-10",
            name = "Murata QPRO Plus Automatic Winder",
            model = "No. 21C / QPRO Plus",
            manufacturer = "Murata Machinery Ltd.",
            category = MachineCategory.TEXTILE,
            powerRating = "35 kW Total (60 drums)",
            supplyVoltage = "380-415V AC 3-Phase",
            controlVoltage = "24V DC",
            fullLoadCurrent = "72.0 A",
            plcModel = "Murata Multi-task Microprocessor System",
            inverterModel = "Individual Drum DC Inverter Modules",
            description = "Automatic cone winder featuring individual drum motor drives, Uster Quantum yarn clearer integration, automatic piecing splicer, and energy-recovery braking.",
            isPremium = true,
            specs = listOf(
                SpecItem("Capacity", "Number of Spindles", "60 Spindles / machine"),
                SpecItem("Speed", "Winding Speed", "Up to 2,000 m/min", "m/min")
            ),
            plcIOList = listOf(
                PlcIOItem("SP-01", "TB-DRUM-1", "24V", PlcIOType.DIGITAL_INPUT, "Yarn Clearer Cut Signal", "Uster Quantum 4", "W001", "Fast Pulse")
            ),
            partsList = listOf(
                PartItem(1, "DRUM_MTR", "BLDC-150W", "Murata", "Brushless DC Drum Motor 150W", "48VDC 150W 12000rpm", 60, "Spindle Bays")
            ),
            schematicPages = pages,
            wireGauges = listOf(
                WireGaugeItem("DC Bus", "16 mm²", "Red/Black", "48VDC Main Spindle Bus")
            ),
            encoderSpecs = emptyList(),
            terminalBlocks = emptyList()
        )
    }

    // Additional Machines (11 - 28)
    private fun createSchneiderATV630(): Machine = createGenericMachine("mach-11", "Schneider Electric Altivar ATV630", "ATV630D22N4", "Schneider Electric", MachineCategory.INVERTER_DRIVE, "22 kW (30 HP)", "380-480V 3-Phase", "Altivar Process variable speed drive with dual Ethernet ports and embedded power measurement.")
    private fun createYaskawaGA700(): Machine = createGenericMachine("mach-12", "Yaskawa GA700 High Performance Inverter", "CIPR-GA70T4044", "Yaskawa Electric", MachineCategory.INVERTER_DRIVE, "18.5 kW", "380-480V 3-Phase", "Heavy-duty industrial AC drive with SIL3 functional safety and integrated braking transistor.")
    private fun createOmronCP1H(): Machine = createGenericMachine("mach-13", "OMRON CP1H All-in-One Controller", "CP1H-X40DT-D", "OMRON Corporation", MachineCategory.PLC_PANEL, "24VDC 50W", "24V DC", "Micro-PLC with 4 high-speed counter inputs (100kHz) and 4 pulse outputs for multi-axis positioning.")
    private fun createMarzoliFT6ERoving(): Machine = createGenericMachine("mach-14", "Marzoli FT6E Electronic Roving Frame", "FT6E", "Marzoli Machines Textile", MachineCategory.TEXTILE, "22 kW", "400V 3-Phase", "Individual bobbin rail servo motors, independent electronic drafting, and smart roving monitoring.")
    private fun createZinser72XLSpinning(): Machine = createGenericMachine("mach-15", "Zinser 72XL Ring Spinning Machine", "Zinser 72XL", "Saurer Zinser", MachineCategory.TEXTILE, "65 kW", "400V 3-Phase", "High-capacity spinning machine with decentralized drive concept and energy-optimized suction system.")
    private fun createPicanolOmniPlusLoom(): Machine = createGenericMachine("mach-16", "Picanol OmniPlus-i Connect Airjet Loom", "OmniPlus-i Connect", "Picanol NV", MachineCategory.WEAVING, "5.5 kW Main Drive", "380-440V 3-Phase", "Connected smart airjet weaving loom with SmartShed electronic shedding motion and BlueTouch display.")
    private fun createSulzerG6300Rapier(): Machine = createGenericMachine("mach-17", "Sulzer Textil G6300 Rapier Weaving Machine", "G6300", "Itema Group", MachineCategory.WEAVING, "7.5 kW", "380V 3-Phase", "High-precision rapier loom with electronic weft insertion and dynamic warp let-off control.")
    private fun createSaurerBD7Rotor(): Machine = createGenericMachine("mach-18", "Saurer Schlafhorst BD 7 Semi-Automated Rotor", "BD 7", "Saurer Schlafhorst", MachineCategory.TEXTILE, "45 kW", "400V 3-Phase", "Semi-automated rotor spinning machine with individual spinning unit sensors and digital take-up.")
    private fun createSiemensSinamicsS120(): Machine = createGenericMachine("mach-19", "Siemens Sinamics S120 Multi-Axis Servo System", "CU320-2 PN + Booksize", "Siemens AG", MachineCategory.INVERTER_DRIVE, "30 kW Common DC", "400V 3-Phase", "Modular multi-axis motion drive system with DRIVE-CLiQ digital encoder telemetry and Profinet IRT.")
    private fun createABBACS880Drive(): Machine = createGenericMachine("mach-20", "ABB ACS880 Industrial Vector Drive", "ACS880-01-045A-3", "ABB Ltd.", MachineCategory.INVERTER_DRIVE, "22 kW", "380-415V 3-Phase", "Direct Torque Control (DTC) industrial drive with removable memory unit and high starting torque.")
    private fun createSickC4000Curtain(): Machine = createGenericMachine("mach-21", "Sick C4000 Advanced Safety Light Curtain", "C4000 Standard", "SICK AG", MachineCategory.SENSORS_IO, "24VDC 25W", "24V DC", "Type 4 / SIL 3 safety light curtain with dual OSSD solid-state outputs and external device monitoring.")
    private fun createKeyenceFSN41NArray(): Machine = createGenericMachine("mach-22", "Keyence FS-N41N Digital Fiber Optic Array", "FS-N41N Multi-channel", "Keyence Corporation", MachineCategory.SENSORS_IO, "24VDC 15W", "24V DC", "Ultra-high-speed fiber optic amplifier array with cross-talk prevention and dynamic auto-tuning.")
    private fun createBannerQ4XSensor(): Machine = createGenericMachine("mach-23", "Banner Engineering Q4X Laser Distance Sensor", "Q4XTBLAF300-Q8", "Banner Engineering", MachineCategory.SENSORS_IO, "24VDC 5W", "10-30V DC", "Sub-millimeter laser distance sensor with IO-Link communication, dual bipolar outputs, and 4-20mA.")
    private fun createIFMVibrationMonitor(): Machine = createGenericMachine("mach-24", "IFM Electronic VVB001 Vibration Sensor", "VVB001", "ifm electronic gmbh", MachineCategory.SENSORS_IO, "24VDC 2W", "18-30V DC", "Continuous machinery condition monitoring sensor tracking RMS velocity, peak acceleration, and temp.")
    private fun createWeidmullerUR20RemoteIO(): Machine = createGenericMachine("mach-25", "Weidmüller u-remote UR20 Fieldbus I/O System", "UR20-FBC-PN", "Weidmüller Interface", MachineCategory.SENSORS_IO, "24VDC 60W", "24V DC", "High-density modular remote I/O slice system with Profinet coupler, 16 DI push-in, and 16 DO transistors.")
    private fun createWagoPFC200Controller(): Machine = createGenericMachine("mach-26", "WAGO 750-8212 PFC200 Industrial Controller", "750-8212", "WAGO GmbH & Co. KG", MachineCategory.PLC_PANEL, "24VDC 10W", "24V DC", "Linux-based real-time programmable controller with dual Ethernet ports, RS-232/485 serial, and Modbus.")
    private fun createAutonicsRotaryEncoder(): Machine = createGenericMachine("mach-27", "Autonics ENC Optical Rotary Encoder Array", "ENC-1-1-T-24", "Autonics Corporation", MachineCategory.SENSORS_IO, "12-24VDC", "12-24V DC", "Wheel-type incremental rotary encoder with ABZ quadrature output, complementary totem pole output.")
    private fun createFestoValveTerminal(): Machine = createGenericMachine("mach-28", "Festo CPX-AP-I Decentralized Valve Terminal", "CPX-AP-I-VTUG", "Festo AG & Co. KG", MachineCategory.SENSORS_IO, "24VDC 45W", "24V DC", "Decentralized pneumatic valve manifold with IO-Link connectivity, pilot solenoids, and 24V bus.")

    private fun createGenericMachine(
        id: String,
        name: String,
        model: String,
        manufacturer: String,
        category: MachineCategory,
        powerRating: String,
        supplyVoltage: String,
        description: String
    ): Machine {
        val pages = (1..12).map { pageNum ->
            SchematicPage(
                pageNumber = pageNum,
                title = "Page $pageNum: $name Circuit Schematics",
                dwgCode = "DWG-${model.take(6)}-$pageNum",
                description = "Standard technical schematic diagram, circuit terminal connections, and wiring legend for $name.",
                components = listOf(
                    SchematicComponent("CMP1", name, model, 300f, 150f, 240f, 160f, listOf("L1", "L2", "L3", "24V+", "0V", "SIG"))
                ),
                wireTraces = listOf(
                    WireTrace("NET1", "Primary Control Circuit", 0xFF00E5FF, "24V DC", listOf(SchematicPoint(100f, 200f), SchematicPoint(300f, 200f)))
                )
            )
        }

        return Machine(
            id = id,
            name = name,
            model = model,
            manufacturer = manufacturer,
            category = category,
            powerRating = powerRating,
            supplyVoltage = supplyVoltage,
            controlVoltage = "24V DC",
            fullLoadCurrent = "N/A",
            plcModel = "Standard Industrial Fieldbus / I/O",
            inverterModel = "Integrated / Compatible",
            description = description,
            isPremium = category == MachineCategory.TEXTILE || category == MachineCategory.INVERTER_DRIVE,
            specs = listOf(
                SpecItem("General", "Model Code", model),
                SpecItem("General", "Manufacturer", manufacturer),
                SpecItem("Electrical", "Operating Supply", supplyVoltage),
                SpecItem("Ratings", "Power Rating", powerRating)
            ),
            plcIOList = listOf(
                PlcIOItem("I0.0", "TB1-1", "24V", PlcIOType.DIGITAL_INPUT, "Run Status Feedback", "Internal Contact", "W01", "NO"),
                PlcIOItem("Q0.0", "TB2-1", "24V", PlcIOType.DIGITAL_OUTPUT, "Alarm Fault Output", "Relay Contact", "W02", "NC")
            ),
            partsList = listOf(
                PartItem(1, "CORE", model, manufacturer, "$name Assembly", powerRating, 1, "Main Cabinet")
            ),
            schematicPages = pages,
            wireGauges = listOf(
                WireGaugeItem("Mains Lead", "4.0 mm²", "Black/Blue", "Standard installation")
            ),
            encoderSpecs = emptyList(),
            terminalBlocks = emptyList()
        )
    }
}
