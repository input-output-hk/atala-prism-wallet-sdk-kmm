package io.iohk.atala.prism.walletsdk.pluto.shared

import com.squareup.sqldelight.db.SqlDriver
import io.iohk.atala.prism.walletsdk.pluto.PrismPlutoDb

fun createPrismPlutoDb(driver: SqlDriver): PrismPlutoDb {
    return PrismPlutoDb(driver)
}
