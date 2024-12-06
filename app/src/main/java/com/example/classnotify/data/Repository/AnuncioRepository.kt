package com.example.classnotify.data.Repository

import android.util.Log
import androidx.lifecycle.LiveData
import com.example.classnotify.data.local.AnuncioDao
import com.example.classnotify.domain.models.Anuncio
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class AnuncioRepository(private val anuncioDao: AnuncioDao) {

    private val db = FirebaseFirestore.getInstance()
    private val anunciosCollection = db.collection("anuncios")

    fun getAllAnuncios(): LiveData<List<Anuncio>> {
        return anuncioDao.getAllAnuncios()
    }

    suspend fun insertAnuncio(anuncio: Anuncio) {
        try {
            val documentRef = anunciosCollection.add(anuncio).await()
            anuncioDao.insertAnuncio(anuncio.copy(firestoreId = documentRef.id))
        } catch (e: Exception) {
            Log.e("AnuncioRepository", "Error insertando anuncio: ${e.message}")
        }
    }

    suspend fun deleteAnuncio(anuncio: Anuncio) {
        try {
            if (anuncio.firestoreId.isNotEmpty()) {
                anunciosCollection.document(anuncio.firestoreId).delete().await()
                anuncioDao.deleteAnuncio(anuncio)
            } else {
                Log.e("AnuncioRepository", "Error eliminando anuncio: Firestore ID is empty")
            }
        } catch (e: Exception) {
            Log.e("AnuncioRepository", "Error eliminando anuncio: ${e.message}")
        }
    }

    suspend fun updateAnuncio(anuncio: Anuncio) {
        try {
            if (anuncio.firestoreId.isNotEmpty()) {
                anunciosCollection.document(anuncio.firestoreId).set(anuncio).await()
                anuncioDao.updateAnuncio(anuncio)
            } else {
                Log.e("AnuncioRepository", "Error actualizando anuncio: Firestore ID is empty")
            }
        } catch (e: Exception) {
            Log.e("AnuncioRepository", "Error actualizando anuncio: ${e.message}")
        }
    }
}