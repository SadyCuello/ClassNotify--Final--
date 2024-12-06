package com.example.classnotify.data.Remote.Api

import android.util.Log
import com.example.classnotify.domain.models.Materia
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class MateriaRepository {
    private val db = FirebaseFirestore.getInstance()
    private val materiasCollection = db.collection("Materia")

    suspend fun registrarMateria(materia: Materia) {
        try {
            val documentReference = materiasCollection.add(materia).await()
            Log.d("Firestore", "DocumentSnapshot added with ID: ${documentReference.id}")
        } catch (e: Exception) {
            Log.w("Firestore", "Error adding document", e)
        }
    }

    suspend fun actualizarMateria(materia: Materia) {
        val doc = materiasCollection.document(materia.idMateria.toString())
        doc.set(materia).await()
    }

    suspend fun borrarMateria(materia: Materia) {
        val doc = materiasCollection.document(materia.idMateria.toString())
        doc.delete().await()
    }

    suspend fun obtenerMateria(): List<Materia> {
        return materiasCollection.get().await().toObjects(Materia::class.java)
    }

    suspend fun obtenerMateriaPorId(id: Long): Materia? {
        val doc = materiasCollection.document(id.toString()).get().await()
        return doc.toObject(Materia::class.java)
    }
}