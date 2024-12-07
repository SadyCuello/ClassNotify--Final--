package com.example.classnotify.data.Repository

import android.util.Log
import com.example.classnotify.domain.models.Anuncio
import com.example.classnotify.domain.models.Materia
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class FirestoreRepository(private val firestore: FirebaseFirestore) {

    suspend fun registrarMateria(materia: Materia, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        try {
            firestore.collection("materias")
                .add(materia)
                .addOnSuccessListener { onSuccess() }
                .addOnFailureListener { e -> onFailure(e) }
        } catch (e: Exception) {
            onFailure(e)
        }
    }

    fun publicarAnuncio(anuncio: Anuncio, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        firestore.collection("anuncios")
            .add(anuncio)
            .addOnSuccessListener { documentReference ->
                documentReference.update("firestoreId", documentReference.id)
                onSuccess()
            }
            .addOnFailureListener { e -> onFailure(e) }
    }

    fun borrarAnuncio(anuncio: Anuncio, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        firestore.collection("anuncios")
            .document(anuncio.firestoreId)
            .delete()
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { e -> onFailure(e) }
    }
    suspend fun obtenerMateriaPorId(id: Long): Materia? {
        val document = firestore.collection("materias").document(id.toString()).get().await()
        return if (document.exists()) {
            document.toObject(Materia::class.java)
        } else {
            null
        }
    }

    fun actualizarAnuncio(anuncio: Anuncio, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        firestore.collection("anuncios")
            .document(anuncio.firestoreId)
            .set(anuncio)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { e -> onFailure(e) }
    }

    fun borrarMateria(materia: Materia, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        firestore.collection("materias")
            .document(materia.firestoreId)
            .delete()
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { e -> onFailure(e) }
    }

    fun actualizarMateria(materia: Materia, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        firestore.collection("materias")
            .document(materia.firestoreId)
            .set(materia)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { e -> onFailure(e) }
    }
    suspend fun obtenerMaterias(): List<Materia> {
        val materias = mutableListOf<Materia>()
        try {
            val result = firestore.collection("materias").get().await()
            for (document in result) {
                val materia = document.toObject(Materia::class.java)
                materias.add(materia)
            }
        } catch (e: Exception) {
            Log.e("FirestoreRepository", "Error obteniendo las materias: $e")
        }
        return materias
    }
}