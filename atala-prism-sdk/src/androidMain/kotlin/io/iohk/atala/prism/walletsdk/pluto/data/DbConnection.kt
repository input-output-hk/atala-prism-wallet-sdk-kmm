package io.iohk.atala.prism.walletsdk.pluto.data

import android.content.Context
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import io.iohk.atala.prism.walletsdk.PrismPlutoDb
import io.iohk.atala.prism.walletsdk.domain.models.PlutoError

/**
 * DbConnection class represents a connection to the database.
 */
actual class DbConnection actual constructor() {
    actual var driver: SqlDriver? = null
    actual suspend fun connectDb(context: Any?): SqlDriver {
        val androidContext: Context = (context as? Context) ?: throw PlutoError.DatabaseContextError()
        val driver = AndroidSqliteDriver(PrismPlutoDb.Schema, androidContext, "prism.db")
        this.driver = driver
        return driver
    }
}

/**
 * Represents the connection status of an SQL driver.
 */
actual val SqlDriver.isConnected: Boolean
    get() {
        try {
            return this.execute(null, "SELECT 1", 0).value == 1L
            // return this.executeQuery(null, "SELECT 1", 0).next()
        } catch (ex: Exception) {
            return false
        }
    }
