package org.hyperledger.identus.walletsdk.db

import androidx.room.Database
import androidx.room.RoomDatabase
import org.hyperledger.identus.walletsdk.sampleapp.db.Message

@Database(entities = [Message::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun messageDao(): MessageDao
}
