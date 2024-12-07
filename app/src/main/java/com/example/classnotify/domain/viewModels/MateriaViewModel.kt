package com.example.classnotify.domain.viewModels

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.classnotify.data.Remote.Api.MateriaRepository
import com.example.classnotify.data.Repository.FirestoreRepository
import com.example.classnotify.domain.models.Materia
import com.example.classnotify.data.local.MateriaDatabaseDao
import com.example.classnotify.ui_presentation.ui.states.MateriaStates
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


class MateriaViewModel (
    private  val dao: MateriaDatabaseDao,
    private val firestoreRepository: FirestoreRepository
): ViewModel() {
    var state by mutableStateOf(MateriaStates())
        private set
    private val _adjuntoBase64 = MutableStateFlow<String?>(null)
    val adjuntoBase64: StateFlow<String?> = _adjuntoBase64.asStateFlow()

    private val repository = MateriaRepository()

    init {
        viewModelScope.launch {
            dao.obtenerMateria().collectLatest {
                state = state.copy(listaMaterias = it)
            }
        }
    }
    fun registrarMateria(materia: Materia, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) = viewModelScope.launch {
        try {
            dao.registrarMateria(materia)
            firestoreRepository.registrarMateria(materia, onSuccess, onFailure)
        } catch (e: Exception) {
            onFailure(e)
        }
    }
    suspend fun obtenerMaterias(id: Long): Materia? {
        return try {
            val materia = dao.obtenerMaterias(id)
            materia ?: firestoreRepository.obtenerMaterias().find { it.idMateria == id }?.also {
                dao.registrarMateria(it)
            }
        } catch (e: Exception) {
            Log.e("MateriaViewModel", "Error obteniendo materia: ${e.message}")
            null
        }
    }
    fun actualizarMateria(materia: Materia, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) = viewModelScope.launch {
        try {
            dao.actualizarMateria(materia)
            firestoreRepository.actualizarMateria(materia, onSuccess, onFailure)
        } catch (e: Exception) {
            onFailure(e)
        }
    }
    fun borrarMateria(materia: Materia, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) = viewModelScope.launch {
        try {
            dao.borrarMateria(materia)
            firestoreRepository.borrarMateria(materia, onSuccess, onFailure)
        } catch (e: Exception) {
            onFailure(e)
        }
    }

    fun setAdjunto(base64String: String) {
        _adjuntoBase64.value = base64String.trim()
    }

    fun limpiarDatos() {
        _adjuntoBase64.value = null
    }
}