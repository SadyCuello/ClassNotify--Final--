package com.example.classnotify.ui_presentation.ui.view

import android.net.Uri
import android.util.Base64
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.classnotify.domain.models.Materia
import com.example.classnotify.domain.viewModels.MateriaViewModel
import java.io.BufferedInputStream
import java.io.ByteArrayOutputStream
import java.io.InputStream

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditarMateriaView(
    navController: NavController,
    viewModel: MateriaViewModel,
    idMateria: Long
) {
    val context = LocalContext.current
    var materia by remember { mutableStateOf<Materia?>(null) }

    LaunchedEffect(idMateria) {

        materia = viewModel.getMateriaById(idMateria)
    }

    val openFilePicker: ActivityResultLauncher<Array<String>> = rememberLauncherForActivityResult(
        ActivityResultContracts.OpenDocument()
    ) { uri: Uri? ->
        uri?.let {
            try {
                val contentResolver = context.contentResolver
                val inputStream: InputStream? = contentResolver.openInputStream(uri)

                inputStream?.use { stream ->
                    val buffer = ByteArray(8192)
                    val outputStream = ByteArrayOutputStream()

                    BufferedInputStream(stream).use { bufferedStream ->
                        var bytesRead: Int
                        while (bufferedStream.read(buffer).also { bytesRead = it } != -1) {
                            outputStream.write(buffer, 0, bytesRead)
                        }
                    }

                    val fileBytes = outputStream.toByteArray()
                    val base64String = Base64.encodeToString(fileBytes, Base64.DEFAULT)
                    viewModel.setAdjunto(base64String)
                }
            } catch (e: Exception) {
                Log.e("EditarMateriaView", "Error al procesar el archivo: ${e.message}")
            }
        }
    }

    materia?.let {
        ContentEditarMateriaView(
            paddingValues = PaddingValues(),
            navController = navController,
            viewModel = viewModel,
            materia = it,
            openFilePicker = openFilePicker
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContentEditarMateriaView(
    paddingValues: PaddingValues,
    navController: NavController,
    viewModel: MateriaViewModel,
    materia: Materia,
    openFilePicker: ActivityResultLauncher<Array<String>>
) {
    val context = LocalContext.current
    val keyboardController = LocalSoftwareKeyboardController.current


    var adjuntoBase64 by remember { mutableStateOf(materia.adjunto ?: "") }
    var nombre by remember { mutableStateOf(materia.nombre) }
    var profesor by remember { mutableStateOf(materia.profesor) }
    var descripcion by remember { mutableStateOf(materia.descripcion) }
    var aula by remember { mutableStateOf(materia.aula) }
    var horarioSeleccionado by remember { mutableStateOf(materia.horario) }

    val horarios = listOf(
        "Selecciona un horario",
        "Lunes (6:00PM-8:00PM)",
        "Miércoles (8:00PM/10:00PM)",
        "Viernes (6:00PM/8:00PM)"
    )
    var showHorario by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .padding(paddingValues)
            .padding(top = 30.dp)
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        OutlinedTextField(
            value = nombre,
            onValueChange = { nombre = it },
            label = { Text(text = "Nombre de la materia") },
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Text),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 30.dp)
                .padding(bottom = 15.dp)
        )
        OutlinedTextField(
            value = profesor,
            onValueChange = { profesor = it },
            label = { Text(text = "Profesor") },
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Text),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 30.dp)
                .padding(bottom = 15.dp)
        )
        OutlinedTextField(
            value = descripcion,
            onValueChange = { descripcion = it },
            label = { Text(text = "Descripción de la materia") },
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Text),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 30.dp)
                .padding(bottom = 15.dp)
        )
        OutlinedTextField(
            value = aula,
            onValueChange = { aula = it },
            label = { Text(text = "Aula asignada") },
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Text),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 30.dp)
                .padding(bottom = 15.dp)
        )
        Button(
            onClick = { openFilePicker.launch(arrayOf("*/*")) },
            colors = ButtonDefaults.buttonColors(containerColor = Color.Red) ,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 30.dp)
                .padding(bottom = 15.dp)
        ) {
            Text(text = "Cargar archivo adjunto")
        }
        Text(
            text = if (adjuntoBase64.isNotEmpty()) "Archivo cargado" else "No se ha cargado ningún archivo",
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 30.dp)
                .padding(bottom = 15.dp)
        )
        ExposedDropdownMenuBox(
            expanded = showHorario,
            onExpandedChange = { showHorario = !showHorario },
            modifier = Modifier
                .padding(horizontal = 30.dp)
                .padding(bottom = 15.dp)
        ) {
          //  keyboardController?.hide()
            TextField(
                modifier = Modifier.menuAnchor(),
                value = horarioSeleccionado,
                onValueChange = { },
                readOnly = true,
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = showHorario) },
                colors = ExposedDropdownMenuDefaults.textFieldColors()
            )
            ExposedDropdownMenu(
                expanded = showHorario,
                onDismissRequest = { showHorario = false }
            ) {
                horarios.forEach { s ->
                    DropdownMenuItem(
                        text = { Text(text = s) },
                        onClick = {
                            if (s != horarios[0]) {
                                horarioSeleccionado = s
                            }
                            showHorario = false
                        },
                        contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding
                    )
                }
            }
        }
        Button(
            onClick = {
                val updatedMateria = materia.copy(
                    nombre = nombre,
                    profesor = profesor,
                    aula = aula,
                    horario = horarioSeleccionado,
                    descripcion = descripcion,
                    adjunto = adjuntoBase64
                )
                viewModel.actualizarMateria(updatedMateria, onSuccess = {
                    navController.popBackStack()
                }, onFailure = {
                    Log.e("EditarMateriaView", "Error al actualizar la materia: ${it.message}")
                })
                navController.popBackStack()
            },
            colors = ButtonDefaults.buttonColors(containerColor = Color.Red) ,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 30.dp)
                .padding(bottom = 15.dp)
        ) {
            Text(text = "Actualizar materia")
        }
    }
}
