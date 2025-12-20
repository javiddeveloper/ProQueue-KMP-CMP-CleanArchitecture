package xyz.sattar.javid.proqueue.domain

import xyz.sattar.javid.proqueue.domain.model.Appointment
import xyz.sattar.javid.proqueue.domain.model.AppointmentWithDetails
import xyz.sattar.javid.proqueue.domain.model.DashboardStats

interface AppointmentRepository {
    suspend fun createAppointment(appointment: Appointment): Long
    suspend fun getWaitingQueue(businessId: Long, date: Long): List<AppointmentWithDetails>
    suspend fun getTodayAppointments(businessId: Long): List<AppointmentWithDetails>
    suspend fun getAllAppointmentsForBusiness(businessId: Long): List<AppointmentWithDetails>
    suspend fun updateAppointmentStatus(appointmentId: Long, status: String): Boolean
    suspend fun updateAppointment(appointmentId: Long, date: Long, duration: Int?, description: String?): Boolean
    suspend fun removeAppointment(appointmentId: Long): Boolean
    suspend fun getVisitorHistory(visitorId: Long): List<AppointmentWithDetails>
    suspend fun getVisitorHistoryForBusiness(visitorId: Long, businessId: Long): List<AppointmentWithDetails>
    suspend fun getTodayStats(businessId: Long, date: Long): DashboardStats
    suspend fun getAppointmentById(appointmentId: Long): Appointment?
    suspend fun getAllWaitingAppointments(businessId: Long): List<AppointmentWithDetails>
    suspend fun deleteAppointmentsByVisitorId(visitorId: Long)
    suspend fun getConflictingAppointments(businessId: Long, startTime: Long, endTime: Long, defaultDuration: Int): List<AppointmentWithDetails>
}
