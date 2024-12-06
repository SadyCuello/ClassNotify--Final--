package com.example.classnotify.ui_presentation.ui.states

import androidx.lifecycle.LiveData
import com.example.classnotify.domain.models.Anuncio

data class AnuncioStates(
    val isLoading: Boolean = false,
    val listaAnuncios: List<Anuncio> = emptyList(),
    val errorMessage: String? = null
)