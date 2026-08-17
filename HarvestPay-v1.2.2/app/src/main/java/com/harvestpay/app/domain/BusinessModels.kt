package com.harvestpay.app.domain

import com.harvestpay.app.data.CustomerEntity
import com.harvestpay.app.data.DieselEntryEntity
import com.harvestpay.app.data.FieldEntity
import com.harvestpay.app.data.PaymentEntity
import com.harvestpay.app.data.WorkEntryEntity
import com.harvestpay.app.data.WorkTypeEntity

enum class PaymentStatus { PAID, PARTIALLY_PAID, PENDING }

data class WorkSummary(
    val work: WorkEntryEntity,
    val customer: CustomerEntity?,
    val field: FieldEntity?,
    val paid: Double,
    val pending: Double,
    val status: PaymentStatus,
)

data class CustomerSummary(
    val customer: CustomerEntity,
    val fields: List<FieldEntity>,
    val work: List<WorkSummary>,
    val payments: List<PaymentEntity>,
    val totalBigha: Double,
    val totalBill: Double,
    val totalPaid: Double,
    val pending: Double,
    val advance: Double,
    val lastWorkDate: Long?,
    val registeredBigha: Double = 0.0,
    val workTotal: Double = 0.0,
    val previousDue: Double = 0.0,
    val previousDuePaid: Double = 0.0,
)

data class DashboardStats(
    val totalCustomers: Int = 0,
    val totalFields: Int = 0,
    val totalBigha: Double = 0.0,
    val totalEarned: Double = 0.0,
    val totalReceived: Double = 0.0,
    val totalPending: Double = 0.0,
    val totalAdvance: Double = 0.0,
    val receivedToday: Double = 0.0,
    val pendingCustomers: Int = 0,
    val totalDieselLitres: Double = 0.0,
    val totalDieselAmount: Double = 0.0,
)

data class HarvestUiState(
    val customers: List<CustomerEntity> = emptyList(),
    val fields: List<FieldEntity> = emptyList(),
    val workEntries: List<WorkEntryEntity> = emptyList(),
    val payments: List<PaymentEntity> = emptyList(),
    val workTypes: List<WorkTypeEntity> = emptyList(),
    val dieselEntries: List<DieselEntryEntity> = emptyList(),
    val workSummaries: List<WorkSummary> = emptyList(),
    val customerSummaries: List<CustomerSummary> = emptyList(),
    val stats: DashboardStats = DashboardStats(),
    val loading: Boolean = true,
)
