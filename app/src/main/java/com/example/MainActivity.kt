package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Surface
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.components.PremiumSidebarDrawerContent
import com.example.ui.dialogs.*
import com.example.ui.screens.DashboardScreen
import com.example.ui.screens.MachineDetailScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.Slate950
import com.example.ui.viewmodel.MainViewModel
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val viewModel: MainViewModel = viewModel()
            val uiState by viewModel.uiState.collectAsStateWithLifecycle()

            MyApplicationTheme(darkTheme = uiState.isDarkTheme) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    IndustrialSchematicsApp(viewModel = viewModel)
                }
            }
        }
    }
}

@Composable
fun IndustrialSchematicsApp(
    viewModel: MainViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val machines by viewModel.filteredMachines.collectAsStateWithLifecycle()
    val allMachines by viewModel.allMachines.collectAsStateWithLifecycle()
    val selectedMachine by viewModel.selectedMachine.collectAsStateWithLifecycle()
    val syncStatus by viewModel.syncStatus.collectAsStateWithLifecycle()

    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val coroutineScope = rememberCoroutineScope()

    val totalPlcPoints = allMachines.sumOf { it.plcIOList.size }
    val totalPartsCount = allMachines.sumOf { it.partsList.sumOf { p -> p.quantity } }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            PremiumSidebarDrawerContent(
                totalMachinesCount = allMachines.size,
                totalPlcPointsCount = totalPlcPoints,
                totalPartsCount = totalPartsCount,
                isEditModeEnabled = uiState.isEditModeEnabled,
                onToggleEditMode = { viewModel.toggleEditMode(it) },
                isDarkTheme = uiState.isDarkTheme,
                onToggleTheme = { viewModel.toggleTheme(it) },
                onOpenGuide = { viewModel.setArchitectureGuideVisible(true) },
                onOpenSyncModal = { viewModel.setSheetSyncDialogVisible(true) },
                onResetDefaults = { viewModel.resetToFactoryDefaults() },
                onCloseDrawer = {
                    coroutineScope.launch { drawerState.close() }
                }
            )
        }
    ) {
        val currentMachine = selectedMachine
        if (currentMachine != null) {
            MachineDetailScreen(
                machine = currentMachine,
                syncStatus = syncStatus,
                selectedTab = uiState.selectedDetailTab,
                currentSchematicPage = uiState.currentSchematicPage,
                activeTracedWireId = uiState.activeTracedWireId,
                isEditModeEnabled = uiState.isEditModeEnabled,
                isDarkTheme = uiState.isDarkTheme,
                onToggleTheme = { viewModel.toggleTheme(!uiState.isDarkTheme) },
                onBack = { viewModel.selectMachine(null) },
                onMenuClick = {
                    coroutineScope.launch { drawerState.open() }
                },
                onTabSelected = { viewModel.selectDetailTab(it) },
                onSchematicPageSelected = { viewModel.setSchematicPage(it) },
                onWireTraceSelected = { viewModel.selectTracedWire(it) },
                onBookmarkToggle = { viewModel.toggleBookmark(currentMachine.id) },
                onSyncClick = { viewModel.setSheetSyncDialogVisible(true) },
                onEditMachineClick = { viewModel.openEditMachineDialog(currentMachine) },
                onDeleteMachineClick = { viewModel.openDeleteDialog(currentMachine) },
                onAddPlcIOClick = { viewModel.openAddPlcDialog() },
                onEditPlcIOClick = { viewModel.openEditPlcDialog(it) },
                onDeletePlcIOClick = { viewModel.deletePlcIO(it) },
                onAddPartClick = { viewModel.openAddPartDialog() },
                onEditPartClick = { viewModel.openEditPartDialog(it) },
                onDeletePartClick = { viewModel.deletePart(it) }
            )
        } else {
            DashboardScreen(
                machines = machines,
                totalMachinesCount = allMachines.size,
                searchQuery = uiState.searchQuery,
                selectedCategory = uiState.selectedCategory,
                showBookmarksOnly = uiState.showBookmarksOnly,
                syncStatus = syncStatus,
                isEditModeEnabled = uiState.isEditModeEnabled,
                isDarkTheme = uiState.isDarkTheme,
                onToggleTheme = { viewModel.toggleTheme(!uiState.isDarkTheme) },
                onSearchQueryChanged = { viewModel.setSearchQuery(it) },
                onCategorySelected = { viewModel.selectCategory(it) },
                onToggleBookmarks = { viewModel.toggleBookmarksFilter() },
                onMachineClick = { machineId -> viewModel.selectMachine(machineId) },
                onBookmarkToggle = { machineId -> viewModel.toggleBookmark(machineId) },
                onSyncClick = { viewModel.setSheetSyncDialogVisible(true) },
                onMenuClick = {
                    coroutineScope.launch { drawerState.open() }
                },
                onAddMachineClick = { viewModel.openAddMachineDialog() }
            )
        }

        // Add / Edit Machine Dialog
        if (uiState.isAddEditMachineDialogOpen) {
            AddEditMachineDialog(
                initialMachine = uiState.machineBeingEdited,
                onDismiss = { viewModel.closeAddEditDialog() },
                onSaveMachine = { viewModel.saveMachine(it) }
            )
        }

        // Delete Confirmation Dialog
        val machineToDelete = uiState.machineBeingDeleted
        if (machineToDelete != null) {
            DeleteMachineConfirmDialog(
                machine = machineToDelete,
                onDismiss = { viewModel.closeDeleteDialog() },
                onConfirmDelete = { viewModel.deleteConfirmedMachine() }
            )
        }

        // Add / Edit PLC I/O Channel Dialog
        if (uiState.isAddEditPlcDialogOpen) {
            AddEditPlcIODialog(
                initialItem = uiState.plcBeingEdited,
                onDismiss = { viewModel.closePlcDialog() },
                onSavePlcIO = { viewModel.savePlcIO(it) }
            )
        }

        // Add / Edit Part (BOM Item) Dialog
        if (uiState.isAddEditPartDialogOpen) {
            val nextItemNo = (currentMachine?.partsList?.maxOfOrNull { it.itemNo } ?: 0) + 1
            AddEditPartDialog(
                initialPart = uiState.partBeingEdited,
                nextItemNo = nextItemNo,
                onDismiss = { viewModel.closePartDialog() },
                onSavePart = { viewModel.savePart(it) }
            )
        }

        // Cloud Database Sync Dialog (Supabase + Google Sheets)
        if (uiState.isSheetSyncDialogOpen) {
            GoogleSheetSyncModal(
                currentSheetId = uiState.enteredSheetId,
                currentSupabaseUrl = uiState.enteredSupabaseUrl,
                currentSupabaseKey = uiState.enteredSupabaseKey,
                bulkSqlScript = viewModel.getBulkSqlScript(),
                syncStatus = syncStatus,
                onDismiss = { viewModel.setSheetSyncDialogVisible(false) },
                onSheetIdChanged = { viewModel.updateEnteredSheetId(it) },
                onSupabaseUrlChanged = { viewModel.updateEnteredSupabaseUrl(it) },
                onSupabaseKeyChanged = { viewModel.updateEnteredSupabaseKey(it) },
                onSyncSheetTrigger = { viewModel.triggerGoogleSheetSync() },
                onSyncSupabaseTrigger = { viewModel.triggerSupabaseSync() },
                onBulkPushSupabase = { viewModel.pushAllMachinesToSupabase() },
                onResetDefaults = { viewModel.resetToFactoryDefaults() }
            )
        }

        // Architecture Guide Dialog (Opened from Sidebar Navigation)
        if (uiState.isArchitectureGuideOpen) {
            ArchitectureGuideModal(
                onDismiss = { viewModel.setArchitectureGuideVisible(false) }
            )
        }
    }
}
