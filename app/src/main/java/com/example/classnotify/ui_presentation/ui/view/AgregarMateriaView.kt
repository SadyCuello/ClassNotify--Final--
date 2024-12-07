import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Base64
import android.util.Log
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.classnotify.domain.models.Materia
import com.example.classnotify.ui_presentation.ui.view.PDFViewer
import com.example.classnotify.domain.viewModels.MateriaViewModel
import com.example.classnotify.ui_presentation.ui.theme.RedColor
import com.example.classnotify.ui_presentation.ui.theme.WhiteColor
import java.io.BufferedInputStream
import java.io.ByteArrayOutputStream

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AgregarMateriaView(
    navController: NavController,
    viewModel: MateriaViewModel
) {
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }
    val openFilePicker = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri: Uri? ->
        uri?.let {
            try {
                val contentResolver = context.contentResolver
                val inputStream = contentResolver.openInputStream(uri)

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
                Log.e("AgregarMateriaView", "Error al procesar el archivo: ${e.message}")
            }
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Registrar materia",
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.error
                ),
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = "Regresar",
                            tint = Color.White
                        )
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        ContentAgregarMateriaView(
            paddingValues = paddingValues,
            navController = navController,
            viewModel = viewModel,
            openFilePicker = openFilePicker,
            snackbarHostState = snackbarHostState
        )
    }
}

@OptIn(ExperimentalComposeUiApi::class, ExperimentalMaterial3Api::class)
@Composable
fun ContentAgregarMateriaView(
    paddingValues: PaddingValues,
    navController: NavController,
    viewModel: MateriaViewModel,
    openFilePicker: ManagedActivityResultLauncher<Array<String>, Uri?>,
    snackbarHostState: SnackbarHostState
) {
    val context = LocalContext.current
    val keyboardController = LocalSoftwareKeyboardController.current

    var nombre by remember { mutableStateOf("") }
    var profesor by remember { mutableStateOf("") }
    var descripcion by remember { mutableStateOf("") }
    var aula by remember { mutableStateOf("") }
    val adjuntoBase64 by viewModel.adjuntoBase64.collectAsState()

    val horarios = listOf(
        "Selecciona un horario",
        "Lunes (6:00PM-8:00PM)",
        "Miércoles (8:00PM-10:00PM)",
        "Viernes (6:00PM-8:00PM)"
    )
    var showHorario by remember { mutableStateOf(false) }
    var horarioSeleccionado by remember { mutableStateOf(horarios[0]) }
    var snackbarMessage by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(snackbarMessage) {
        snackbarMessage?.let {
            snackbarHostState.showSnackbar(it)
            snackbarMessage = null
        }
    }

    Column(
        modifier = Modifier
            .padding(paddingValues)
            .padding(top = 30.dp)
            .verticalScroll(rememberScrollState())
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
        Button(onClick = {
            openFilePicker.launch(arrayOf("image/*", "application/pdf"))
        },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 30.dp)
                .padding(bottom = 15.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color.Red)

        ) {
            Text("Cargar archivo adjunto")
        }


        adjuntoBase64?.let { base64String ->
            val mimeType = when {
                base64String.startsWith("/9j/") -> "image/jpeg"
                base64String.contains("JVBERi0xL") -> "application/pdf"
                else -> "application/octet-stream"
            }


            when {
                mimeType.startsWith("image/") -> {
                    val imageBytes = Base64.decode(base64String.trim(), Base64.DEFAULT)
                    val imageBitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)?.asImageBitmap()
                    if (imageBitmap != null) {
                        Image(
                            bitmap = imageBitmap,
                            contentDescription = "Vista previa de imagen",
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 30.dp)
                                .padding(bottom = 15.dp)
                                .size(100.dp)
                        )
                    } else {
                        Text(text = "Error al decodificar la imagen.")
                    }
                }
                mimeType == "application/pdf" -> {
                    PDFViewer(
                        base64String = base64String,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(500.dp)
                            .padding(horizontal = 30.dp)
                    )
                }
                else -> {
                    Text(text = "Archivo adjunto no compatible.")
                }
            }
        }
        ExposedDropdownMenuBox(
            expanded = showHorario,
            onExpandedChange = { showHorario = !showHorario },
            modifier = Modifier
                .padding(horizontal = 30.dp)
                .padding(bottom = 15.dp)
                .background(Color.White),
        ) {
            TextField(
                modifier = Modifier
                    .menuAnchor()
                    .background(Color.White),
                readOnly = true,
                value = horarioSeleccionado,
                onValueChange = {},
                label = { Text("Seleccionar horario") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = showHorario) },
                colors = ExposedDropdownMenuDefaults.textFieldColors()
            )
            ExposedDropdownMenu(
                expanded = showHorario,
                onDismissRequest = { showHorario = false }
            ) {
                horarios.forEach { selectionOption ->
                    DropdownMenuItem(
                        text = { Text(selectionOption) },
                        modifier = Modifier.background(Color.White), // Set background color to white
                        onClick = {
                            horarioSeleccionado = selectionOption
                            showHorario = false
                        }
                    )
                }
            }
        }

        Button(
            onClick = {
                if (nombre.isNotEmpty() && profesor.isNotEmpty() && descripcion.isNotEmpty() && aula.isNotEmpty() && horarioSeleccionado != horarios[0]) {
                    val materia = Materia(
                        nombre = nombre,
                        profesor = profesor,
                        descripcion = descripcion,
                        aula = aula,
                        horario = horarioSeleccionado,
                        adjunto = adjuntoBase64
                    )
                    viewModel.registrarMateria(
                        materia,
                        onSuccess = {
                            viewModel.limpiarDatos()
                            navController.popBackStack()
                        },
                        onFailure = { error ->
                            snackbarMessage = "Error al registrar la materia: ${error.message}"
                        }
                    )
                } else {
                    snackbarMessage = "Por favor, complete todos los campos."
                }
            },
            colors = ButtonDefaults.buttonColors(containerColor = Color.Red) ,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 30.dp)
                .padding(bottom = 15.dp)
        ) {
            Text("Guardar")
        }
    }
}