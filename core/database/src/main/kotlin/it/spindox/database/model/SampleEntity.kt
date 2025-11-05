package it.spindox.database.model

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity
data class SampleEntity (
    @PrimaryKey val name: String,
)