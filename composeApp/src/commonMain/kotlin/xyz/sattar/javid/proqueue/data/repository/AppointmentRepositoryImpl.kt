package xyz.sattar.javid.proqueue.data.repository

import xyz.sattar.javid.proqueue.data.localDataSource.dao.AppointmentDao
import xyz.sattar.javid.proqueue.data.localDataSource.dao.BusinessDao
import xyz.sattar.javid.proqueue.data.localDataSource.dao.VisitorDao
import xyz.sattar.javid.proqueue.data.localDataSource.mapper.toDomain
import xyz.sattar.javid.proqueue.data.localDataSource.mapper.toEntity
import xyz.sattar.javid.proqueue.domain.AppointmentRepository
import xyz.sattar.javid.proqueue.domain.model.Appointment
import xyz.sattar.javid.proqueue.domain.model.AppointmentWithDetails
import xyz.sattar.javid.proqueue.domain.model.DashboardStats
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

class AppointmentRepositoryImpl(
    private val appointmentDao: AppointmentDao,
    private val businessDao: BusinessDao,
    private val visitorDao: VisitorDao
) : AppointmentRepository {
    override suspend fun createAppointment(appointment: Appointment): Boolean {
        return try {
            val hasActive = appointmentDao.hasActiveAppointment(
                appointment.businessId,
                appointment.visitorId,
                appointment.appointmentDate
            )
            if (hasActive) return false

            val lastPosition = appointmentDao.getLastQueuePosition(
                appointment.businessId,
                appointment.appointmentDate
            ) ?: 0

            appointmentDao.upsertAppointment(
                appointment.toEntity().copy(queuePosition = lastPosition + 1)
            )
            true
        } catch (e: Exception) {
            false
        }
    }

    override suspend fun getWaitingQueue(businessId: Long, date: Long): List<AppointmentWithDetails> {
        return try {
            val appointments = appointmentDao.getWaitingQueue(businessId, date)

            appointments.map { appointment ->
                val visitor = visitorDao.getVisitorById(appointment.visitor.id)?.toDomain()
                val business = businessDao.getBusinessById(appointment.business.id)?.toDomain()

                AppointmentWithDetails(
                    appointment = appointment.appointment.toDomain(),
                    visitor = visitor ?: throw Exception("Visitor not found"),
                    business = business ?: throw Exception("Business not found")
                )
            }
        } catch (e: Exception) {
            emptyList()
        }
    }

    @OptIn(ExperimentalTime::class)
    override suspend fun updateAppointmentStatus(appointmentId: Long, status: String): Boolean {
        return try {
            appointmentDao.updateAppointmentStatus(
                appointmentId,
                status,
                Clock.System.now().toEpochMilliseconds()
            )
            true
        } catch (e: Exception) {
            false
        }
    }

    @OptIn(ExperimentalTime::class)
    override suspend fun updateAppointment(appointmentId: Long, date: Long, duration: Int?): Boolean {
        return try {
            appointmentDao.updateAppointment(
                appointmentId,
                date,
                duration,
                Clock.System.now().toEpochMilliseconds()
            )
            true
        } catch (e: Exception) {
            false
        }
    }

    override suspend fun removeAppointment(appointmentId: Long): Boolean {
        return try {
            appointmentDao.removeAppointmentAndReorder(appointmentId)
            true
        } catch (e: Exception) {
            false
        }
    }

    override suspend fun getVisitorHistory(visitorId: Long): List<AppointmentWithDetails> {
        return try {
            appointmentDao.getVisitorHistory(visitorId).map {
                it.toDomain()
            }
        } catch (e: Exception) {
            emptyList()
        }
    }

    override suspend fun getTodayStats(businessId: Long, date: Long): DashboardStats {
        return try {
            DashboardStats(
                totalAppointments = appointmentDao.getTodayAppointmentsCount(businessId, date),
                noShowCount = appointmentDao.getTodayNoShowCount(businessId, date),
                cancelledCount = appointmentDao.getTodayCancelledCount(businessId, date)
            )
        } catch (e: Exception) {
            DashboardStats(0, 0, 0)
        }
    }
    override suspend fun getAppointmentById(appointmentId: Long): Appointment? {
        return try {
            appointmentDao.getAppointmentById(appointmentId)?.toDomain()
        } catch (e: Exception) {
            null
        }
    }

    override suspend fun getAllWaitingAppointments(businessId: Long): List<AppointmentWithDetails> {
        return try {
            appointmentDao.getAllWaitingAppointments(businessId).map {
                it.toDomain()
            }
        } catch (e: Exception) {
            emptyList()
        }
    }

    override suspend fun getTodayAppointments(businessId: Long): List<AppointmentWithDetails> {
        return try {
            appointmentDao.getTodayAppointments(businessId).map {
                it.toDomain()
            }
        } catch (e: Exception) {
            emptyList()
        }
    }

    override suspend fun getAllAppointmentsForBusiness(businessId: Long): List<AppointmentWithDetails> {
        return try {
            appointmentDao.getAllAppointmentsForBusiness(businessId).map {
                it.toDomain()
            }
        } catch (e: Exception) {
            emptyList()
        }
    }

    override suspend fun deleteAppointmentsByVisitorId(visitorId: Long) {
        try {
            appointmentDao.deleteAppointmentsByVisitorId(visitorId)
        } catch (e: Exception) {
            // Log error
        }
    }
}