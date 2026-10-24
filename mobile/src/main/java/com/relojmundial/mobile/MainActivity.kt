package com.relojmundial.mobile

import android.content.Context
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.android.gms.wearable.PutDataMapRequest
import com.google.android.gms.wearable.Wearable
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val dao = AppDatabase.obtener(this).ciudadDao()
        setContent {
            MaterialTheme {
                PantallaPrincipal(dao)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaPrincipal(dao: CiudadDao) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val ciudades by dao.observarTodas().collectAsState(initial = emptyList())
    val ahora by rememberAhora()
    var mostrarDialogo by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Reloj Mundial") },
                actions = {
                    TextButton(onClick = { enviarAlReloj(context, ciudades) }) {
                        Text("Enviar al reloj")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { mostrarDialogo = true }) {
                Text("+", fontSize = 28.sp)
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            if (ciudades.isEmpty()) {
                item {
                    Text("Aún no tienes ciudades. Toca + para agregar una.")
                }
            }
            items(ciudades, key = { it.id }) { ciudad ->
                Card(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(ciudad.nombre, style = MaterialTheme.typography.titleMedium)
                            Text(
                                "${fecha(ciudad.zona, ahora)} · ${diferencia(ciudad.zona, ahora)}",
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                        Text(
                            hora(ciudad.zona, ahora),
                            style = MaterialTheme.typography.headlineSmall
                        )
                        TextButton(onClick = { scope.launch { dao.eliminar(ciudad) } }) {
                            Text("Quitar")
                        }
                    }
                }
            }
        }
    }

    if (mostrarDialogo) {
        val disponibles = CATALOGO.filter { cat -> ciudades.none { it.zona == cat.zona } }
        AlertDialog(
            onDismissRequest = { mostrarDialogo = false },
            title = { Text("Agregar ciudad") },
            text = {
                LazyColumn(modifier = Modifier.heightIn(max = 400.dp)) {
                    items(disponibles) { cat ->
                        TextButton(
                            onClick = {
                                scope.launch {
                                    dao.insertar(Ciudad(nombre = cat.nombre, zona = cat.zona))
                                }
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(cat.nombre)
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { mostrarDialogo = false }) { Text("Listo") }
            }
        )
    }
}

/** Envia la lista de ciudades al reloj usando la Data Layer de Wear OS. */
fun enviarAlReloj(context: Context, ciudades: List<Ciudad>) {
    try {
        val peticion = PutDataMapRequest.create("/ciudades").apply {
            dataMap.putStringArray("nombres", ciudades.map { it.nombre }.toTypedArray())
            dataMap.putStringArray("zonas", ciudades.map { it.zona }.toTypedArray())
            // La marca de tiempo fuerza que el reloj detecte el cambio aunque la lista sea igual
            dataMap.putLong("marca", System.currentTimeMillis())
        }.asPutDataRequest().setUrgent()

        Wearable.getDataClient(context).putDataItem(peticion)
            .addOnSuccessListener {
                Toast.makeText(context, "Ciudades enviadas al reloj", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                Toast.makeText(context, "No se pudo enviar: ¿el reloj está vinculado?", Toast.LENGTH_LONG).show()
            }
    } catch (e: Exception) {
        Toast.makeText(context, "Este dispositivo no soporta la conexión con el reloj", Toast.LENGTH_LONG).show()
    }
}
