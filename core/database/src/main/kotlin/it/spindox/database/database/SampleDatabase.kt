package it.spindox.database.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import it.spindox.database.converters.Converters
import it.spindox.database.dao.SampleDao
import it.spindox.database.model.SampleEntity


@TypeConverters(Converters::class)
@Database(
    entities = [
        SampleEntity::class,
    ],
    version = 1,
    exportSchema = false
)

abstract class SampleDatabase: RoomDatabase() {

    abstract fun sampleDao(): SampleDao

    companion object{
        const val DATABASE_NAME: String = "sample_db"
    }
}