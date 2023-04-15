package com.enescanpolat.googlemapskotlin.roomdb

import androidx.room.Database
import androidx.room.RoomDatabase
import com.enescanpolat.googlemapskotlin.model.place


@Database(entities = [place::class], version = 1)
abstract class placedatabase :RoomDatabase() {
    abstract fun placedao():placeDao
}