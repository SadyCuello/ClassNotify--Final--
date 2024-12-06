package com.example.classnotify.data.local

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Delete
import androidx.room.OnConflictStrategy
import androidx.room.Update
import com.example.classnotify.domain.models.Anuncio

@Dao
interface AnuncioDao {
    @Query("SELECT * FROM Anuncio")
    fun getAllAnuncios(): LiveData<List<Anuncio>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAnuncio(anuncio: Anuncio)

    @Delete
    suspend fun deleteAnuncio(anuncio: Anuncio)

    @Update
    suspend fun updateAnuncio(anuncio: Anuncio)
}