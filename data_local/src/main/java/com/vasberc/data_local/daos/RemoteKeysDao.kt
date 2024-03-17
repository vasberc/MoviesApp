package com.vasberc.data_local.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.vasberc.data_local.entities.MovieRemoteKeysEntity

@Dao
interface MovieRemoteKeysDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(remoteKey: List<MovieRemoteKeysEntity>)

    @Query("SELECT * FROM movie_remote_keys WHERE movieId = :movieId")
    suspend fun remoteKeysRepoId(movieId: Int): MovieRemoteKeysEntity?

    @Query("DELETE FROM movie_remote_keys")
    suspend fun clearRemoteKeys()
}