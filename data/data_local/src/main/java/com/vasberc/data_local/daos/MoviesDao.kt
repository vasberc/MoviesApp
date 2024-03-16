package com.vasberc.data_local.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import com.vasberc.data_local.entities.FavouriteEntity

@Dao
interface MoviesDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertFavourite(favouriteEntity: FavouriteEntity)

}