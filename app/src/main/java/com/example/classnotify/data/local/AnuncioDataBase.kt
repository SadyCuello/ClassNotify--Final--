package com.example.classnotify.data.local


import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.classnotify.domain.models.Anuncio

@Database(
    entities = [Anuncio::class],
    version = 1,
    exportSchema = false)

abstract class AnuncioDataBase : RoomDatabase() {
    abstract fun anuncioDao(): AnuncioDao
}
