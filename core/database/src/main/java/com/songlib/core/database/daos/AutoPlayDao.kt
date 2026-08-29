package com.songlib.core.database.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.songlib.core.database.model.AutoPlayEntity

@Dao
interface AutoPlayDao {
    @Query("SELECT * FROM auto_play WHERE songId = :songId")
    suspend fun getBySongId(songId: Int): AutoPlayEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(entity: AutoPlayEntity)

    @Query("DELETE FROM auto_play WHERE songId = :songId")
    suspend fun deleteBySongId(songId: Int)
}
