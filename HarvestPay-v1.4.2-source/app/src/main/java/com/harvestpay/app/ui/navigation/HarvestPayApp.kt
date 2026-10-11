package com.harvestpay.app.ui.navigation

import com.harvestpay.app.localization.AppText
import com.harvestpay.app.R
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.outlined.AddBusiness
import androidx.compose.material.icons.outlined.Agriculture
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material.icons.outlined.LocalGasStation
import androidx.compose.material.icons.outlined.PersonAdd
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.ChevronRight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.layout.Spacer
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.harvestpay.app.AuthState
import com.harvestpay.app.HarvestViewModel
import com.harvestpay.app.data.CustomerEntity
import com.harvestpay.app.data.appDisplayName
import com.harvestpay.app.domain.DashboardInsight
import com.harvestpay.app.ui.components.EmptyState
import com.harvestpay.app.ui.components.SuccessFeedbackHost
import com.harvestpay.app.ui.screens.AboutScreen
import com.harvestpay.app.ui.screens.LanguageScreen
import com.harvestpay.app.ui.screens.VersionHistoryScreen
import com.harvestpay.app.ui.screens.BackupScreen
import com.harvestpay.app.ui.screens.ChangePasswordScreen
import com.harvestpay.app.ui.screens.CustomerFormScreen
import com.harvestpay.app.ui.screens.CustomerProfileScreen
import com.harvestpay.app.ui.screens.CustomersScreen
import com.harvestpay.app.ui.screens.DashboardScreen
import com.harvestpay.app.ui.screens.DashboardInsightScreen
import com.harvestpay.app.ui.screens.InsightTarget
import com.harvestpay.app.ui.screens.DieselEntryScreen
import com.harvestpay.app.ui.screens.FieldFormScreen
import com.harvestpay.app.ui.screens.GlobalSearchScreen
import com.harvestpay.app.ui.screens.LoginScreen
import com.harvestpay.app.ui.screens.MoreScreen
import com.harvestpay.app.ui.screens.PaymentFormScreen
import com.harvestpay.app.ui.screens.PaymentsScreen
import com.harvestpay.app.ui.screens.ReminderFormScreen
import com.harvestpay.app.ui.screens.ReportsScreen
import com.harvestpay.app.ui.screens.SettingsScreen
import com.harvestpay.app.ui.screens.WorkFormScreen
import com.harvestpay.app.ui.screens.WorkTypeManagementScreen
import com.harvestpay.app.ui.screens.ServiceReminderScreen
import com.harvestpay.app.ui.screens.CurrentHoursUpdateDialog
import com.harvestpay.app.ui.screens.ArchivedYearScreen
import com.harvestpay.app.ui.screens.BiometricLockScreen
import com.harvestpay.app.ui.screens.YearArchivePromptDialog

import com.harvestpay.app.measurement.*
import com.harvestpay.app.HarvestPayApplication
import androidx.compose.ui.platform.LocalContext
import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.TextButton
import androidx.compose.material.icons.outlined.Map
import androidx.compose.material.icons.outlined.MyLocation

private object Routes {
    const val DASHBOARD = "dashboard"
    const val DASHBOARD_INSIGHT = "insight/{insight}"
    const val CUSTOMERS = "customers"
    const val NEW_WORK = "new_work"
    const val PAYMENTS = "payments"
    const val MORE = "more"
    const val SEARCH = "search"
    const val REPORTS = "reports"
    const val WORK_TYPES = "work_types"
    const val BACKUP = "backup"
    const val SETTINGS = "settings"
    const val BIOMETRIC_LOCK = "biometric_lock"
    const val CHANGE_PASSWORD = "change_password"
    const val ABOUT = "about"
    const val DIESEL = "diesel"
    const val DIESEL_RECORD = "diesel_record/{entryId}"
    const val SERVICE = "service_reminder"
    const val CUSTOMER = "customer/{customerId}"
    const val ARCHIVED_YEAR = "archive/{customerId}/{year}"
    const val CUSTOMER_FORM = "customer_form/{customerId}"
    const val FIELD_FORM = "field_form/{customerId}/{fieldId}?measured={measured}"
    const val WORK_FORM = "work_form/{customerId}"
    const val WORK_EDIT = "work_edit/{workId}"
    const val PAYMENT_FORM = "payment_form/{customerId}"
    const val REMINDER_FORM = "reminder_form/{customerId}"
    const val PICKER = "picker/{action}"
}

private data class NavItem(val route: String, val label: String, val icon: ImageVector)

@Composable
fun HarvestPayRoot(viewModel: HarvestViewModel, auth: AuthState) {
    AnimatedContent(
        targetState = auth.authenticated,
        transitionSpec = { fadeIn(tween(80)) togetherWith fadeOut(tween(60)) },
        label = "authentication",
    ) { authenticated ->
        if (!authenticated) {
            LoginScreen(
                auth = auth,
                onLogin = viewModel::login,
                onInputChanged = viewModel::clearLoginError,
            )
        } else {
            AuthenticatedApp(viewModel)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AuthenticatedApp(viewModel: HarvestViewModel) {
    val navController = rememberNavController()
    val app = LocalContext.current.applicationContext as HarvestPayApplication
    val measurementStore = remember { app.measurementStore }
    val measurement by measurementStore.draft.collectAsStateWithLifecycle()
    val backDispatcher = LocalOnBackPressedDispatcherOwner.current?.onBackPressedDispatcher
    var measureOptions by remember { mutableStateOf(false) }
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val settings by viewModel.settings.collectAsStateWithLifecycle()
    val reminders by viewModel.reminders.collectAsStateWithLifecycle()
    val showHoursUpdate by viewModel.showHoursUpdate.collectAsStateWithLifecycle()
    val pendingArchiveYear by viewModel.pendingArchiveYear.collectAsStateWithLifecycle()
    val snackbar = remember { SnackbarHostState() }
    val entry by navController.currentBackStackEntryAsState()
    val route = entry?.destination?.route ?: Routes.DASHBOARD
    val rootRoutes = setOf(Routes.DASHBOARD, Routes.CUSTOMERS, Routes.NEW_WORK, Routes.PAYMENTS, Routes.MORE)
    var quickActions by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.messages.collect { snackbar.showSnackbar(it) }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(titleFor(route, entry?.arguments?.getString("insight")), fontWeight = FontWeight.SemiBold) },
                navigationIcon = {
                    if (route !in rootRoutes) {
                        IconButton(onClick = { if(route == "measurement") backDispatcher?.onBackPressed() else navController.popBackStack() }) { Icon(Icons.Outlined.ArrowBack, AppText.text(R.string.text_back)) }
                    }
                },
                actions = {
                    if (route in rootRoutes) {
                        IconButton(onClick = { navController.navigate(Routes.SEARCH) }) { Icon(Icons.Outlined.Search, AppText.text(R.string.text_global_search)) }
                    }
                },
            )
        },
        bottomBar = {
            if (route in rootRoutes) {
                val items = listOf(
                    NavItem(Routes.DASHBOARD, AppText.text(R.string.text_dashboard), Icons.Filled.Home),
                    NavItem(Routes.CUSTOMERS, AppText.text(R.string.text_customers), Icons.Filled.People),
                    NavItem(Routes.NEW_WORK, AppText.text(R.string.text_new_work), Icons.Filled.AddCircle),
                    NavItem(Routes.PAYMENTS, AppText.text(R.string.text_payments), Icons.Filled.Payments),
                    NavItem(Routes.MORE, AppText.text(R.string.text_more), Icons.Filled.MoreHoriz),
                )
                NavigationBar {
                    items.forEach { item ->
                        NavigationBarItem(
                            selected = route == item.route,
                            onClick = {
                                navController.navigate(item.route) {
                                    popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            icon = { Icon(item.icon, null) },
                            label = { Text(item.label) },
                        )
                    }
                }
            }
        },
        floatingActionButton = {
            if (route == Routes.DASHBOARD) {
                ExtendedFloatingActionButton(onClick = { quickActions = true }, icon = { Icon(Icons.Filled.Add, null) }, text = { Text(AppText.text(R.string.text_quick_add)) })
            }
        },
        snackbarHost = {
            Column {
                SuccessFeedbackHost(viewModel.successes)
                SnackbarHost(snackbar)
            }
        },
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = Routes.DASHBOARD,
            modifier = Modifier.padding(padding),
            enterTransition = {
                fadeIn(tween(70)) + slideInHorizontally(tween(80)) { fullWidth -> fullWidth / 20 }
            },
            exitTransition = { fadeOut(tween(50)) },
            popEnterTransition = { fadeIn(tween(70)) },
            popExitTransition = {
                fadeOut(tween(50)) + slideOutHorizontally(tween(80)) { fullWidth -> fullWidth / 20 }
            },
        ) {
            composable("measurement") {
                MeasurementScreen(settings, onAssign = { navController.navigate("picker/measured") }, onClose = { navController.popBackStack() })
            }
            composable("land_settings") { LandMeasurementSettings(settings) }
            composable("boundary/{fieldId}", arguments = listOf(navArgument("fieldId") { type = NavType.LongType })) { stack ->
                SavedBoundaryScreen(uiState.fields.firstOrNull { it.id == stack.arguments?.getLong("fieldId") })
            }
            composable(Routes.DASHBOARD) {
                DashboardScreen(
                    uiState,
                    settings,
                    onOpenCustomer = { navController.navigate("customer/$it") },
                    onAddCustomer = { navController.navigate("customer_form/0") },
                    onOpenPayments = { navController.navigate(Routes.PAYMENTS) },
                    onMarkPaid = { viewModel.markCustomerPaid(it) },
                    onAddPayment = { navController.navigate("payment_form/$it") },
                    onOpenInsight = { navController.navigate("insight/${it.name}") },
                    onOpenWork = { navController.navigate("work_edit/$it") },
                    onRetry = viewModel::retryLedger,
                )
            }
            composable(Routes.DASHBOARD_INSIGHT, arguments = listOf(navArgument("insight") { type = NavType.StringType })) { backStack ->
                val insight = DashboardInsight.entries.firstOrNull { it.name == backStack.arguments?.getString("insight") }
                if (insight == null) EmptyState(AppText.text(R.string.text_insight_unavailable), AppText.text(R.string.text_return_to_the_dashboard_and_choose_a_statistic))
                else DashboardInsightScreen(uiState, insight, viewModel::retryLedger) { target ->
                    when (target) {
                        is InsightTarget.Customer -> navController.navigate("customer/${target.id}")
                        is InsightTarget.Field -> navController.navigate("field_form/${target.customerId}/${target.fieldId}")
                        is InsightTarget.Work -> navController.navigate("work_edit/${target.id}")
                        is InsightTarget.Diesel -> navController.navigate("diesel_record/${target.id}")
                        InsightTarget.WorkTypes -> navController.navigate(Routes.WORK_TYPES)
                    }
                }
            }
            composable(Routes.CUSTOMERS) {
                CustomersScreen(
                    uiState,
                    onAddCustomer = { navController.navigate("customer_form/0") },
                    onOpenCustomer = { navController.navigate("customer/$it") },
                    onEditCustomer = { navController.navigate("customer_form/$it") },
                    onDeleteCustomer = viewModel::deleteCustomer,
                )
            }
            composable(Routes.NEW_WORK) {
                WorkFormScreen(uiState, settings, null, onSave = { work, paid, method ->
                    viewModel.saveWork(work, paid, method) { navController.navigate(Routes.DASHBOARD) }
                })
            }
            composable(Routes.PAYMENTS) {
                PaymentsScreen(
                    uiState,
                    reminders,
                    onOpenCustomer = { navController.navigate("customer/$it") },
                    onAddPayment = { navController.navigate("payment_form/${it ?: 0}") },
                    onMarkPaid = { viewModel.markCustomerPaid(it) },
                    onAddReminder = { navController.navigate("reminder_form/$it") },
                    onOpenDieselPayments = { navController.navigate(Routes.DIESEL) },
                    onDeletePayment = viewModel::deletePayment,
                    onDeleteReminder = viewModel::deleteReminder,
                )
            }
            composable(Routes.MORE) {
                MoreScreen(
                    onReports = { navController.navigate(Routes.REPORTS) },
                    onBackup = { navController.navigate(Routes.BACKUP) },
                    onWorkTypes = { navController.navigate(Routes.WORK_TYPES) },
                    onServiceReminder = { navController.navigate(Routes.SERVICE) },
                    onSettings = { navController.navigate(Routes.SETTINGS) },
                    onBiometricLock = { navController.navigate(Routes.BIOMETRIC_LOCK) },
                    onChangePassword = { navController.navigate(Routes.CHANGE_PASSWORD) },
                    onAbout = { navController.navigate(Routes.ABOUT) },
                    onLanguage = { navController.navigate("language") },
                    onLogout = viewModel::logout,
                )
            }
            composable(Routes.SEARCH) { GlobalSearchScreen(uiState) { navController.navigate("customer/$it") } }
            composable(Routes.REPORTS) { ReportsScreen(uiState) { navController.navigate("customer/$it") } }
            composable(Routes.WORK_TYPES) {
                WorkTypeManagementScreen(
                    workTypes = uiState.workTypes,
                    onSave = viewModel::saveWorkType,
                    onDelete = viewModel::deleteWorkType,
                )
            }
            composable(Routes.SERVICE) { ServiceReminderScreen(settings, viewModel::saveServiceSettings, viewModel::markServiceCompleted) }
            composable(Routes.BACKUP) {
                BackupScreen(viewModel::backupJson, viewModel::customersCsv, viewModel::paymentsCsv) { viewModel.restoreBackup(it) }
            }
            composable(Routes.SETTINGS) { SettingsScreen(settings, viewModel::setTheme, viewModel::saveSettings, onLandSettings = { navController.navigate("land_settings") }) }
            composable(Routes.BIOMETRIC_LOCK) { BiometricLockScreen(settings) }
            composable(Routes.CHANGE_PASSWORD) {
                ChangePasswordScreen { currentPassword, newPassword, confirmation ->
                    viewModel.changePassword(currentPassword, newPassword, confirmation) {
                        navController.popBackStack()
                    }
                }
            }
            composable(Routes.ABOUT) { AboutScreen { navController.navigate("version_history") } }
            composable("language") { LanguageScreen() }
            composable("version_history") { VersionHistoryScreen() }
            composable(Routes.DIESEL) {
                DieselEntryScreen(
                    entries = uiState.dieselEntries,
                    totalLitres = uiState.stats.totalDieselLitres,
                    totalAmount = uiState.stats.totalDieselAmount,
                    onSave = viewModel::saveDieselEntry,
                    onDelete = viewModel::deleteDieselEntry,
                )
            }
            composable(Routes.DIESEL_RECORD, arguments = listOf(navArgument("entryId") { type = NavType.LongType })) { backStack ->
                val id = backStack.arguments?.getLong("entryId") ?: 0L
                if (uiState.dieselEntries.none { it.id == id }) EmptyState(AppText.text(R.string.text_diesel_entry_unavailable), AppText.text(R.string.text_this_entry_may_have_been_deleted))
                else DieselEntryScreen(
                    entries = uiState.dieselEntries,
                    totalLitres = uiState.stats.totalDieselLitres,
                    totalAmount = uiState.stats.totalDieselAmount,
                    onSave = viewModel::saveDieselEntry,
                    onDelete = viewModel::deleteDieselEntry,
                    initialEntryId = id,
                )
            }
            composable(
                Routes.CUSTOMER,
                arguments = listOf(navArgument("customerId") { type = NavType.LongType }),
            ) { backStack ->
                val id = backStack.arguments?.getLong("customerId") ?: 0
                CustomerProfileScreen(
                    summary = uiState.customerSummaries.firstOrNull { it.customer.id == id },
                    uiState = uiState,
                    settings = settings,
                    onEdit = { navController.navigate("customer_form/$id") },
                    onAddField = { navController.navigate("field_form/$id/0") },
                    onEditField = { navController.navigate("field_form/$id/$it") },
                    onDeleteField = viewModel::deleteField,
                    onAddWork = { navController.navigate("work_form/$id") },
                    onAddPayment = { navController.navigate("payment_form/$id") },
                    onDeleteWork = viewModel::deleteWork,
                    onEditWork = { navController.navigate("work_edit/$it") },
                    onDeletePayment = viewModel::deletePayment,
                    onOpenArchivedYear = { navController.navigate("archive/$id/$it") },
                )
            }
            composable(
                Routes.ARCHIVED_YEAR,
                arguments = listOf(
                    navArgument("customerId") { type = NavType.LongType },
                    navArgument("year") { type = NavType.IntType },
                ),
            ) { backStack ->
                ArchivedYearScreen(
                    uiState = uiState,
                    customerId = backStack.arguments?.getLong("customerId") ?: 0L,
                    year = backStack.arguments?.getInt("year") ?: 0,
                    settings = settings,
                )
            }
            composable(
                Routes.CUSTOMER_FORM,
                arguments = listOf(navArgument("customerId") { type = NavType.LongType }),
            ) { backStack ->
                val id = backStack.arguments?.getLong("customerId") ?: 0
                CustomerFormScreen(
                    existing = uiState.customers.firstOrNull { it.id == id },
                    onSave = { customer ->
                        viewModel.saveCustomer(customer) { savedId ->
                            navController.navigate("customer/$savedId") { popUpTo("customer_form/$id") { inclusive = true } }
                        }
                    },
                    onCancel = { navController.popBackStack() },
                )
            }
            composable(
                Routes.FIELD_FORM,
                arguments = listOf(
                    navArgument("customerId") { type = NavType.LongType },
                    navArgument("fieldId") { type = NavType.LongType },
                    navArgument("measured") { type = NavType.BoolType; defaultValue = false },
                ),
            ) { backStack ->
                val customerId = backStack.arguments?.getLong("customerId") ?: 0
                val fieldId = backStack.arguments?.getLong("fieldId") ?: 0
                val fromMeasurement = backStack.arguments?.getBoolean("measured") == true
                val field = uiState.fields.firstOrNull { it.id == fieldId && it.customerId == customerId }
                if (fieldId != 0L && field == null) EmptyState(AppText.text(R.string.text_field_unavailable), AppText.text(R.string.text_this_field_may_have_been_deleted))
                else FieldFormScreen(
                    customerId,
                    field,
                    onSave = { field ->
                        viewModel.saveField(field) {
                            if (fromMeasurement && measurement?.assignedCustomerId == customerId && fieldId == 0L) measurementStore.clear()
                            navController.navigate("customer/$customerId") { popUpTo(if (fromMeasurement) "measurement" else Routes.FIELD_FORM) { inclusive = true } }
                        }
                    },
                    onCancel = { navController.popBackStack() },
                    measurement = measurement?.takeIf { fromMeasurement && fieldId == 0L && it.stage == MeasurementStage.ACCEPTED && it.assignedCustomerId == customerId },
                    onViewBoundary = { navController.navigate("boundary/$fieldId") },
                )
            }
            composable(
                Routes.WORK_FORM,
                arguments = listOf(navArgument("customerId") { type = NavType.LongType }),
            ) { backStack ->
                val customerId = backStack.arguments?.getLong("customerId") ?: 0
                WorkFormScreen(uiState, settings, customerId, onSave = { work, paid, method ->
                    viewModel.saveWork(work, paid, method) {
                        navController.navigate("customer/${work.customerId}") { popUpTo("work_form/$customerId") { inclusive = true } }
                    }
                }, onCancel = { navController.popBackStack() })
            }
            composable(Routes.WORK_EDIT, arguments = listOf(navArgument("workId") { type = NavType.LongType })) { backStack ->
                val workId = backStack.arguments?.getLong("workId") ?: 0; val existing = uiState.workEntries.firstOrNull { it.id == workId }
                if (existing == null) EmptyState(AppText.text(R.string.text_work_entry_not_found), AppText.text(R.string.text_this_work_entry_may_have_been_deleted)) else WorkFormScreen(
                    uiState = uiState, settings = settings, preselectedCustomerId = existing.customerId, existingWork = existing,
                    onSave = { work, paid, method -> viewModel.saveWork(work, paid, method) { navController.navigate("customer/${work.customerId}") { popUpTo("work_edit/$workId") { inclusive = true } } } },
                    onCancel = { navController.popBackStack() },
                )
            }
            composable(
                Routes.PAYMENT_FORM,
                arguments = listOf(navArgument("customerId") { type = NavType.LongType }),
            ) { backStack ->
                val customerId = backStack.arguments?.getLong("customerId") ?: 0
                PaymentFormScreen(uiState, customerId.takeIf { it != 0L }, onSave = { customer, amount, date, method, notes ->
                    viewModel.addCustomerPayment(customer.id, amount, date, method, notes) {
                        navController.navigate("customer/${customer.id}") { popUpTo("payment_form/$customerId") { inclusive = true } }
                    }
                }, onCancel = { navController.popBackStack() })
            }
            composable(
                Routes.REMINDER_FORM,
                arguments = listOf(navArgument("customerId") { type = NavType.LongType }),
            ) { backStack ->
                val customerId = backStack.arguments?.getLong("customerId") ?: 0
                val summary = uiState.customerSummaries.firstOrNull { it.customer.id == customerId }
                ReminderFormScreen(summary?.customer, summary?.pending ?: 0.0, onSave = { time, message ->
                    viewModel.createReminder(customerId, time, message) {
                        navController.navigate(Routes.PAYMENTS) { popUpTo("reminder_form/$customerId") { inclusive = true } }
                    }
                }, onCancel = { navController.popBackStack() })
            }
            composable(
                Routes.PICKER,
                arguments = listOf(navArgument("action") { type = NavType.StringType }),
            ) { backStack ->
                val action = backStack.arguments?.getString("action").orEmpty()
                CustomerPickerScreen(uiState.customers, action) { customer ->
                    when (action) {
                        "measured" -> {
                            measurementStore.update { it.copy(assignedCustomerId = customer.id) }
                            navController.navigate("field_form/${customer.id}/0?measured=true")
                        }
                        "field" -> navController.navigate("field_form/${customer.id}/0")
                        "work" -> navController.navigate("work_form/${customer.id}")
                        else -> navController.navigate("payment_form/${customer.id}")
                    }
                }
            }
        }
    }

    if (measureOptions) {
        ModalBottomSheet(onDismissRequest = { measureOptions = false }) {
            Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(AppText.text(R.string.text_measure_field), style = MaterialTheme.typography.titleLarge)
                if (measurement != null) {
                    TextButton(onClick = { measureOptions = false; navController.navigate("measurement") }) { Text(AppText.text(R.string.text_resume_unfinished_measurement)) }
                    Text(AppText.text(R.string.text_resume_or_discard_the_current_draft_before_starting_ano))
                } else {
                    Text(AppText.text(R.string.text_walk_around_the_field_boundary_to_calculate_its_area))
                    QuickAction(Icons.Outlined.MyLocation, AppText.text(R.string.text_measure_by_gps)) { measurementStore.begin(MeasurementMethod.GPS); measureOptions = false; navController.navigate("measurement") }
                    Text(AppText.text(R.string.text_select_field_boundaries_directly_on_a_satellite_map))
                    QuickAction(Icons.Outlined.Map, AppText.text(R.string.text_measure_by_satellite)) { measurementStore.begin(MeasurementMethod.SATELLITE); measureOptions = false; navController.navigate("measurement") }
                }
            }
        }
    }
    if (quickActions) {
        ModalBottomSheet(onDismissRequest = { quickActions = false }) {
            Column(Modifier.fillMaxWidth().padding(horizontal = 18.dp, vertical = 8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(AppText.text(R.string.text_quick_add), style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                QuickAction(Icons.Outlined.Map, AppText.text(R.string.text_measure_field)) { quickActions = false; measureOptions = true }
                QuickAction(Icons.Outlined.PersonAdd, AppText.text(R.string.text_add_customer)) { quickActions = false; navController.navigate("customer_form/0") }
                QuickAction(Icons.Outlined.AddBusiness, AppText.text(R.string.text_add_field)) { quickActions = false; navController.navigate("picker/field") }
                QuickAction(Icons.Outlined.Agriculture, AppText.text(R.string.text_add_work)) { quickActions = false; navController.navigate(Routes.NEW_WORK) }
                QuickAction(Icons.Filled.Payments, AppText.text(R.string.text_add_payment)) { quickActions = false; navController.navigate("payment_form/0") }
                QuickAction(Icons.Outlined.LocalGasStation, AppText.text(R.string.text_diesel_entry)) { quickActions = false; navController.navigate(Routes.DIESEL) }
            }
        }
    }
    if (pendingArchiveYear != null) {
        YearArchivePromptDialog(pendingArchiveYear!!, viewModel::dismissArchivePrompt, viewModel::backupAndArchivePendingYear)
    } else if (showHoursUpdate) {
        CurrentHoursUpdateDialog(settings.currentTractorHours, viewModel::dismissHoursUpdate, viewModel::updateCurrentHours)
    }
}

@Composable
private fun QuickAction(icon: ImageVector, title: String, onClick: () -> Unit) {
    Card(onClick = onClick, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(14.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow)) {
        Row(Modifier.fillMaxWidth().padding(16.dp), horizontalArrangement = Arrangement.spacedBy(14.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, null, tint = MaterialTheme.colorScheme.primary)
            Text(title, fontWeight = FontWeight.SemiBold)
            Spacer(Modifier.weight(1f))
            Icon(Icons.Outlined.ChevronRight, null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
private fun CustomerPickerScreen(customers: List<CustomerEntity>, action: String, onPick: (CustomerEntity) -> Unit) {
    if (customers.isEmpty()) {
        EmptyState(AppText.text(R.string.text_add_a_customer_first), AppText.text(R.string.text_a_customer_is_required_before_you_can_add_1_s, action.ifBlank { AppText.text(R.string.text_this_record) }))
        return
    }
    var search by remember { mutableStateOf("") }
    val filtered = customers.filter { listOf(it.name, it.nickname, it.mobile, it.village, it.address).any { value -> value.contains(search, ignoreCase = true) } }
    LazyColumn(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
        item { OutlinedTextField(search, { search = it }, label = { Text(AppText.text(R.string.text_search_customers)) }, singleLine = true, modifier = Modifier.fillMaxWidth()) }
        item { Text(AppText.text(R.string.text_choose_customer), style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold) }
        if (filtered.isEmpty()) item { Text(AppText.text(R.string.text_no_matching_customers)) }
        items(filtered.sortedBy { it.name }, key = { it.id }) { customer ->
            Card(onClick = { onPick(customer) }, modifier = Modifier.fillMaxWidth()) {
                Column(Modifier.padding(16.dp)) {
                    Text(customer.appDisplayName, fontWeight = FontWeight.Bold)
                    Text(listOf(customer.village, customer.mobile).filter(String::isNotBlank).joinToString(" • "))
                }
            }
        }
    }
}

private fun titleFor(route: String, insight: String? = null): String = when (route) {
    "language" -> AppText.text(R.string.language_title)
    "version_history" -> AppText.text(R.string.text_what_s_new)
    "measurement" -> AppText.text(R.string.text_measure_field)
    "land_settings" -> AppText.text(R.string.text_land_measurement_settings)
    "boundary/{fieldId}" -> AppText.text(R.string.text_field_boundary)
    Routes.DASHBOARD -> "Harvest Pay"
    Routes.DASHBOARD_INSIGHT -> DashboardInsight.entries.firstOrNull { it.name == insight }?.title?.let(AppText::label) ?: AppText.text(R.string.text_business_insight)
    Routes.CUSTOMERS -> AppText.text(R.string.text_customers)
    Routes.NEW_WORK -> AppText.text(R.string.text_new_work)
    Routes.PAYMENTS -> AppText.text(R.string.text_payments)
    Routes.MORE -> AppText.text(R.string.text_more)
    Routes.SEARCH -> AppText.text(R.string.text_search)
    Routes.REPORTS -> AppText.text(R.string.text_reports)
    Routes.WORK_TYPES -> AppText.text(R.string.text_work_types)
    Routes.BACKUP -> AppText.text(R.string.text_backup_export)
    Routes.SETTINGS -> AppText.text(R.string.text_settings)
    Routes.BIOMETRIC_LOCK -> AppText.text(R.string.text_biometric_lock)
    Routes.CHANGE_PASSWORD -> AppText.text(R.string.text_change_password)
    Routes.ABOUT -> AppText.text(R.string.text_about)
    Routes.DIESEL -> AppText.text(R.string.text_diesel_entry)
    Routes.DIESEL_RECORD -> AppText.text(R.string.text_diesel_record)
    Routes.SERVICE -> AppText.text(R.string.text_service_reminder)
    else -> when {
        route.startsWith("customer/") -> AppText.text(R.string.text_customer_profile)
        route.startsWith("archive/") -> AppText.text(R.string.text_archived_history)
        route.startsWith("customer_form") -> AppText.text(R.string.text_customer)
        route.startsWith("field_form") -> AppText.text(R.string.text_field)
        route.startsWith("work_form") -> AppText.text(R.string.text_new_work)
        route.startsWith("work_edit") -> AppText.text(R.string.text_edit_work)
        route.startsWith("payment_form") -> AppText.text(R.string.text_add_payment)
        route.startsWith("reminder_form") -> AppText.text(R.string.text_reminder)
        route.startsWith("picker") -> AppText.text(R.string.text_choose_customer)
        else -> "Harvest Pay"
    }
}
