package xyz.sattar.javid.proqueue.domain.usecase

import xyz.sattar.javid.proqueue.domain.AppointmentRepository
import xyz.sattar.javid.proqueue.domain.model.AppointmentWithDetails
import kotlin.math.abs

class GetTodayAppointmentsUseCase(private val repository: AppointmentRepository) {
    suspend operator fun invoke(businessId: Long, currentTime: Long): List<AppointmentWithDetails> {
        // Get recent appointments (last 50)
        val recentAppointments = repository.getTodayAppointments(businessId)
        
        // Filter appointments that are within today (24 hours from now, backwards and forwards)
        val oneDayInMillis = 24 * 60 * 60 * 1000L
        
        return recentAppointments.filter { appointmentWithDetails ->
            val timeDiff = abs(appointmentWithDetails.appointment.appointmentDate - currentTime)
            timeDiff < oneDayInMillis
        }.sortedBy { it.appointment.queuePosition }
    }
}
