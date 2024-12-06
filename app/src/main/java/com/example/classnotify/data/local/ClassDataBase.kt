package com.example.classnotify.data.local

import androidx.room.Database
import  androidx.room.RoomDatabase
import com.example.classnotify.domain.models.Materia

@Database(
    entities = [Materia::class],
    version = 1,
    exportSchema = false
)
abstract class ClassDataBase : RoomDatabase(){
    abstract fun materiaDao(): MateriaDatabaseDao
}