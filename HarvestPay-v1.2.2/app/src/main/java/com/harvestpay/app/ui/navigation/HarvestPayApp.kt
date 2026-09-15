package com.harvestpay.app.ui.navigation

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
import com.harvestpay.app.ui.components.EmptyState
import com.harvestpay.app.ui.screens.AboutScreen
import com.harvestpay.app.ui.screens.BackupScreen
import com.harvestpay.app.ui.screens.ChangePasswordScreen
import com.harvestpay.app.ui.screens.CustomerFormScreen
import com.harvestpay.app.ui.screens.CustomerProfileScreen
import com.harvestpay.app.ui.screens.CustomersScreen
import com.harvestpay.app.ui.screens.DashboardScreen
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

private object Routes {
    const val DASHBOARD = "dashboard"
    const val CUSTOMERS = "customers"
    const val NEW_WORK = "new_work"
    const val PAYMENTS = "payments"
    const val MORE = "more"
    const val SEARCH = "search"
    const val REPORTS = "reports"
    const val WORK_TYPES = "work_types"
    const val BACKUP = "backup"
    const val SETTINGS = "settings"
    const val CHANGE_PASSWORD = "change_password"
    const val ABOUT = "about"
    const val DIESEL = "diesel"
    const val CUSTOMER = "customer/{customerId}"
    const val CUSTOMER_FORM = "customer_form/{customerId}"
    const val FIELD_FORM = "field_form/{customerId}/{fieldId}"
    const val WORK_FORM = "work_form/{customerId}"
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
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val settings by viewModel.settings.collectAsStateWithLifecycle()
    val reminders by viewModel.reminders.collectAsStateWithLifecycle()
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
                title = { Text(titleFor(route), fontWeight = FontWeight.SemiBold) },
                navigationIcon = {
                    if (route !in rootRoutes) {
                        IconButton(onClick = { navController.popBackStack() }) { Icon(Icons.Outlined.ArrowBack, "Back") }
                    }
                },
                actions = {
                    if (route in rootRoutes) {
                        IconButton(onClick = { navController.navigate(Routes.SEARCH) }) { Icon(Icons.Outlined.Search, "Global search") }
                    }
                },
            )
        },
        bottomBar = {
            if (route in rootRoutes) {
                val items = listOf(
                    NavItem(Routes.DASHBOARD, "Dashboard", Icons.Filled.Home),
                    NavItem(Routes.CUSTOMERS, "Customers", Icons.Filled.People),
                    NavItem(Routes.NEW_WORK, "New Work", Icons.Filled.AddCircle),
                    NavItem(Routes.PAYMENTS, "Payments", Icons.Filled.Payments),
                    NavItem(Routes.MORE, "More", Icons.Filled.MoreHoriz),
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
                ExtendedFloatingActionButton(onClick = { quickActions = true }, icon = { Icon(Icons.Filled.Add, null) }, text = { Text("Quick add") })
            }
        },
        snackbarHost = { SnackbarHost(snackbar) },
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
            composable(Routes.DASHBOARD) {
                DashboardScreen(
                    uiState,
                    settings,
                    onOpenCustomer = { navController.navigate("customer/$it") },
                    onAddCustomer = { navController.navigate("customer_form/0") },
                    onOpenPayments = { navController.navigate(Routes.PAYMENTS) },
                    onMarkPaid = { viewModel.markCustomerPaid(it) },
                    onAddPayment = { navController.navigate("payment_form/$it") },
                )
            }
            composable(Routes.CUSTOMERS) {
                CustomersScreen(
                    uiState,
                    onAddCustomer = { navController.navigate("customer_form/0") },
                    onOpenCustomer = { navController.navigate("customer/$it") },
                    onEditCustomer = { navController.navigate("customer_form/$it") },
                    onDeleteCustomer = { viewModel.deleteCustomer(it) },
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
                    onSettings = { navController.navigate(Routes.SETTINGS) },
                    onChangePassword = { navController.navigate(Routes.CHANGE_PASSWORD) },
                    onAbout = { navController.navigate(Routes.ABOUT) },
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
            composable(Routes.BACKUP) {
                BackupScreen(viewModel::backupJson, viewModel::customersCsv, viewModel::paymentsCsv) { viewModel.restoreBackup(it) }
            }
            composable(Routes.SETTINGS) { SettingsScreen(settings, viewModel::setTheme, viewModel::saveSettings) }
            composable(Routes.CHANGE_PASSWORD) {
                ChangePasswordScreen { currentPassword, newPassword, confirmation ->
                    viewModel.changePassword(currentPassword, newPassword, confirmation) {
                        navController.popBackStack()
                    }
                }
            }
            composable(Routes.ABOUT) { AboutScreen() }
            composable(Routes.DIESEL) {
                DieselEntryScreen(
                    entries = uiState.dieselEntries,
                    totalLitres = uiState.stats.totalDieselLitres,
                    totalAmount = uiState.stats.totalDieselAmount,
                    onSave = viewModel::saveDieselEntry,
                    onDelete = viewModel::deleteDieselEntry,
                )
            }
            composable(
                Routes.CUSTOMER,
                arguments = listOf(navArgument("customerId") { type = NavType.LongType }),
            ) { backStack ->
                val id = backStack.arguments?.getLong("customerId") ?: 0
                CustomerProfileScreen(
                    summary = uiState.customerSummaries.firstOrNull { it.customer.id == id },
                    settings = settings,
                    onEdit = { navController.navigate("customer_form/$id") },
                    onAddField = { navController.navigate("field_form/$id/0") },
                    onEditField = { navController.navigate("field_form/$id/$it") },
                    onDeleteField = viewModel::deleteField,
                    onAddWork = { navController.navigate("work_form/$id") },
                    onAddPayment = { navController.navigate("payment_form/$id") },
                    onDeleteWork = viewModel::deleteWork,
                    onDeletePayment = viewModel::deletePayment,
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
                ),
            ) { backStack ->
                val customerId = backStack.arguments?.getLong("customerId") ?: 0
                val fieldId = backStack.arguments?.getLong("fieldId") ?: 0
                FieldFormScreen(
                    customerId,
                    uiState.fields.firstOrNull { it.id == fieldId },
                    onSave = { field ->
                        viewModel.saveField(field) {
                            navController.navigate("customer/$customerId") { popUpTo("field_form/$customerId/$fieldId") { inclusive = true } }
                        }
                    },
                    onCancel = { navController.popBackStack() },
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
                        "field" -> navController.navigate("field_form/${customer.id}/0")
                        "work" -> navController.navigate("work_form/${customer.id}")
                        else -> navController.navigate("payment_form/${customer.id}")
                    }
                }
            }
        }
    }

    if (quickActions) {
        ModalBottomSheet(onDismissRequest = { quickActions = false }) {
            Column(Modifier.fillMaxWidth().padding(horizontal = 18.dp, vertical = 8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Quick add", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                QuickAction(Icons.Outlined.PersonAdd, "Add customer") { quickActions = false; navController.navigate("customer_form/0") }
                QuickAction(Icons.Outlined.AddBusiness, "Add field") { quickActions = false; navController.navigate("picker/field") }
                QuickAction(Icons.Outlined.Agriculture, "Add work") { quickActions = false; navController.navigate(Routes.NEW_WORK) }
                QuickAction(Icons.Filled.Payments, "Add payment") { quickActions = false; navController.navigate("payment_form/0") }
                QuickAction(Icons.Outlined.LocalGasStation, "Diesel entry") { quickActions = false; navController.navigate(Routes.DIESEL) }
            }
        }
    }
}

@Composable
private fun QuickAction(icon: ImageVector, title: String, onClick: () -> Unit) {
    Card(onClick = onClick, modifier = Modifier.fillMaxWidth()) {
        Row(Modifier.fillMaxWidth().padding(16.dp), horizontalArrangement = Arrangement.spacedBy(14.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, null, tint = MaterialTheme.colorScheme.primary)
            Text(title, fontWeight = FontWeight.SemiBold)
        }
    }
}

@Composable
private fun CustomerPickerScreen(customers: List<CustomerEntity>, action: String, onPick: (CustomerEntity) -> Unit) {
    if (customers.isEmpty()) {
        EmptyState("Add a customer first", "A customer is required before you can add ${action.ifBlank { "this record" }}.")
        return
    }
    LazyColumn(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
        item { Text("Choose customer", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold) }
        items(customers.sortedBy { it.name }, key = { it.id }) { customer ->
            Card(onClick = { onPick(customer) }, modifier = Modifier.fillMaxWidth()) {
                Column(Modifier.padding(16.dp)) {
                    Text(customer.appDisplayName, fontWeight = FontWeight.Bold)
                    Text(listOf(customer.village, customer.mobile).filter(String::isNotBlank).joinToString(" • "))
                }
            }
        }
    }
}

private fun titleFor(route: String): String = when (route) {
    Routes.DASHBOARD -> "Harvest Pay"
    Routes.CUSTOMERS -> "Customers"
    Routes.NEW_WORK -> "New Work"
    Routes.PAYMENTS -> "Payments"
    Routes.MORE -> "More"
    Routes.SEARCH -> "Search"
    Routes.REPORTS -> "Reports"
    Routes.WORK_TYPES -> "Work Types"
    Routes.BACKUP -> "Backup & Export"
    Routes.SETTINGS -> "Settings"
    Routes.CHANGE_PASSWORD -> "Change Password"
    Routes.ABOUT -> "About"
    Routes.DIESEL -> "Diesel Entry"
    else -> when {
        route.startsWith("customer/") -> "Customer Profile"
        route.startsWith("customer_form") -> "Customer"
        route.startsWith("field_form") -> "Field"
        route.startsWith("work_form") -> "New Work"
        route.startsWith("payment_form") -> "Add Payment"
        route.startsWith("reminder_form") -> "Reminder"
        route.startsWith("picker") -> "Choose Customer"
        else -> "Harvest Pay"
    }
}
