package com.enescanpolat.googlemapskotlin.roomdb

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.enescanpolat.googlemapskotlin.model.place
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Flowable


@Dao
interface placeDao {

    @Query("SELECT * FROM place")
    fun getAll(): Flowable<List<place>>

    @Insert
    fun insert(place: place) : Completable

    @Delete
    fun delete(place: place) : Completable



}