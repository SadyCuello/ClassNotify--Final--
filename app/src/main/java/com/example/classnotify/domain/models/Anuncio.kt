package com.example.classnotify.domain.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.IgnoreExtraProperties

@Entity(tableName = "Anuncio")
@IgnoreExtraProperties
data class Anuncio(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @DocumentId val firestoreId: String = "",

    val titulo: String,
    val descripcion: String
)