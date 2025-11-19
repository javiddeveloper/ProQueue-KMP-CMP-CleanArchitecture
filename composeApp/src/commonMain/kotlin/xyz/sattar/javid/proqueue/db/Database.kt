package xyz.sattar.javid.proqueue.db

/**
 * Small helper to create the SQLDelight-generated AppDatabase using a
 * platform-specific DatabaseDriverFactory.
 */
fun createDatabase(driverFactory: DatabaseDriverFactory): AppDatabase {
    val driver = driverFactory.createDriver()
    return AppDatabase(driver)
}
