package com.example.classnotify.ui_presentation.ui.view

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.pdf.PdfRenderer
import android.os.Build
import android.os.ParcelFileDescriptor
import android.util.Base64
import android.util.Log
import android.view.View
import android.widget.LinearLayout
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavController
import com.example.classnotify.R
import com.example.classnotify.domain.viewModels.MateriaViewModel
import com.example.classnotify.domain.viewModels.AnuncioViewModel
import java.io.File
import java.io.FileOutputStream

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InicioView(navController: NavController, viewModel: MateriaViewModel, anuncioViewModel: AnuncioViewModel) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(text = "ClassNotify", color = Color.White, fontWeight = FontWeight.Bold)
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.error
                ),
                navigationIcon = {
                    Icon(
                        painter = painterResource(id = R.drawable.images),
                        contentDescription = "Logo",
                        modifier = Modifier
                            .padding(start = 8.dp)
                            .size(40.dp),
                        tint = Color.Unspecified
                    )
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navController.navigate("agregarMateria") },
                containerColor = MaterialTheme.colorScheme.error,
                contentColor = Color.White
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Registrar")
            }
        },
        floatingActionButtonPosition = FabPosition.End
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = { navController.navigate("publicarAnuncio") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
            ) {
                Text("Publicar Anuncio", color = Color.White)
            }
            Button(
                onClick = { navController.navigate("verAnuncios") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
            ) {
                Text("Ver Anuncios Publicados",color = Color.White)
            }

            ContentInicioView(paddingValues, navController, viewModel)
        }
    }
}

@Composable
fun ContentInicioView(paddingValues: PaddingValues, navController: NavController, viewModel: MateriaViewModel) {
    val state = viewModel.state

    if (state.listaMaterias.isEmpty()) {
        // Si la lista de materias está vacía, muestra el mensaje
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "No hay materias registradas. Haz clic en el botón + para agregar más.",
                style = MaterialTheme.typography.bodyLarge,
                color = Color.Gray
            )
        }
    } else {
        // Si hay materias, muestra la lista
        Column(modifier = Modifier.padding(paddingValues)) {
            LazyColumn(
                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
            ) {
                items(state.listaMaterias.sortedByDescending { it.nombre }.sortedBy { it.horario }) { materia ->
                    Card(
                        modifier = Modifier
                            .padding(8.dp)
                            .fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp),
                        colors = CardDefaults.cardColors()
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(12.dp)
                        ) {
                            Text(text = materia.nombre, modifier = Modifier.align(Alignment.CenterHorizontally))
                            Text(text = materia.horario, modifier = Modifier.align(Alignment.CenterHorizontally))
                            Text(text = materia.profesor, modifier = Modifier.align(Alignment.CenterHorizontally))
                            Text(text = materia.descripcion, modifier = Modifier.align(Alignment.CenterHorizontally))
                            Text(text = materia.aula, modifier = Modifier.align(Alignment.CenterHorizontally))

                            materia.adjunto?.let { base64String ->
                                val isPdf = base64String.startsWith("JVBERi0xLj") // Comprobación de PDF en Base64

                                if (isPdf) {
                                    PDFViewer(base64String = base64String, modifier = Modifier.fillMaxWidth().height(500.dp))
                                } else {
                                    val byteArray = Base64.decode(base64String, Base64.DEFAULT)
                                    val imageBitmap = byteArrayToImageBitmap(byteArray)
                                    if (imageBitmap != null) {
                                        Image(
                                            bitmap = imageBitmap,
                                            contentDescription = null,
                                            modifier = Modifier
                                                .size(128.dp)
                                                .align(Alignment.CenterHorizontally),
                                            contentScale = ContentScale.Crop
                                        )
                                    } else {
                                        Text(
                                            text = "No se puede mostrar la vista previa",
                                            modifier = Modifier.align(Alignment.CenterHorizontally)
                                        )
                                    }
                                }
                            } ?: run {
                                Text(
                                    text = "No hay archivo adjunto",
                                    modifier = Modifier.align(Alignment.CenterHorizontally)
                                )
                            }

                            Row(
                                modifier = Modifier.fillMaxSize(),
                                horizontalArrangement = Arrangement.SpaceEvenly
                            ) {
                                IconButton(
                                    onClick = {
                                        navController.navigate("editarMateria/${materia.idMateria}")
                                    }
                                ) {
                                    Icon(imageVector = Icons.Default.Edit, contentDescription = "Editar")
                                }
                                IconButton(
                                    onClick = {
                                        viewModel.borrarMateria(materia, onSuccess = {
                                            Log.d("InicioView", "Materia borrada con éxito")
                                        }, onFailure = { error ->
                                            Log.e("InicioView", "Error al borrar materia: ${error.message}")
                                        })
                                    }
                                ) {
                                    Icon(imageVector = Icons.Default.Delete, contentDescription = "Borrar")
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.LOLLIPOP)
@Composable
fun PDFViewer(base64String: String, modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val pdfBytes = Base64.decode(base64String, Base64.DEFAULT)
    val tempFile = File(context.cacheDir, "temp.pdf").apply {
        FileOutputStream(this).use { output ->
            output.write(pdfBytes)
        }
    }

    if (!tempFile.exists() || tempFile.length() == 0L) {
        Log.e("PDFViewer", "No se pudo guardar el archivo PDF o el archivo está vacío.")
        Text(text = "No se pudo guardar el archivo PDF.")
        return
    }

    AndroidView(
        factory = { context ->
            LinearLayout(context).apply {
                orientation = LinearLayout.VERTICAL
                addView(PdfView(context, tempFile))
            }
        },
        modifier = modifier
    )
}

@RequiresApi(Build.VERSION_CODES.LOLLIPOP)
class PdfView(context: Context, pdfFile: File) : View(context) {
    private val pdfRenderer = PdfRenderer(ParcelFileDescriptor.open(pdfFile, ParcelFileDescriptor.MODE_READ_ONLY))
    private val page: PdfRenderer.Page = pdfRenderer.openPage(0)
    private val bitmap: Bitmap = Bitmap.createBitmap(page.width, page.height, Bitmap.Config.ARGB_8888)

    init {
        page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.drawBitmap(bitmap, 0f, 0f, null)
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        page.close()
        pdfRenderer.close()
    }
}
