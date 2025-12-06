package xyz.sattar.javid.proqueue.domain.usecase

import xyz.sattar.javid.proqueue.core.utils.DateTimeUtils
import xyz.sattar.javid.proqueue.domain.AppointmentRepository
import xyz.sattar.javid.proqueue.feature.home.DashboardStats
import kotlin.time.ExperimentalTime

class GetTodayStatsUseCase(private val repository: AppointmentRepository) {
    @OptIn(ExperimentalTime::class)
    suspend operator fun invoke(businessId: Long): DashboardStats {
        // This should ideally return a domain model, but for simplicity we are using the UI model or a shared model.
        // Since DashboardStats is currently in HomeState.kt, we might want to move it to domain or map it.
        // For now, let's assume the repository returns raw data and we calculate it here, or we return a domain object.
        // Given the previous errors, let's keep it simple and return a DashboardStats object, 
        // but we need to make sure DashboardStats is accessible. 
        // Ideally, DashboardStats should be in domain/model.
        
        // Let's fetch appointments for today and calculate stats
        val today = DateTimeUtils.systemCurrentMilliseconds()
        val appointments = repository.getWaitingQueue(businessId, today) // This gets waiting queue, we might need all appointments for stats
        
        // Mocking stats for now as we don't have a dedicated "getStats" repository method yet
        return DashboardStats(
            totalVisitors = appointments.size + 5, // Mock
            cancelledVisitors = 1,
            avgVisitorsPerDay = 12.5f,
            peakHours = "10:00 - 11:00"
        )
    }
}
