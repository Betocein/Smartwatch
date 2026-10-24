package com.relojmundial.wear

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.wear.compose.foundation.lazy.ScalingLazyColumn
import androidx.wear.compose.foundation.lazy.items
import androidx.wear.compose.material.Chip
import androidx.wear.compose.material.ChipDefaults
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.Scaffold
import androidx.wear.compose.material.Text
import androidx.wear.compose.material.TimeText
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val dao = AppDatabase.obtener(this).ciudadDao()
        setContent {
            RelojApp(dao)
        }
    }
}

/** Pantallas de la app del reloj. */
sealed interface Pantalla {
    object Lista : Pantalla
    object Agregar : Pantalla
    data class Detalle(val ciudad: Ciudad) : Pantalla
}

@Composable
fun RelojApp(dao: CiudadDao) {
    val ciudades by dao.observarTodas().collectAsState(initial = emptyList())
    val ahora by rememberAhora()
    val scope = rememberCoroutineScope()
    var pantalla by remember { mutableStateOf<Pantalla>(Pantalla.Lista) }

    // El gesto/boton de atras regresa a la lista
    BackHandler(enabled = pantalla != Pantalla.Lista) { pantalla = Pantalla.Lista }

    MaterialTheme {
        Scaffold(timeText = { TimeText() }) {
            when (val p = pantalla) {
                is Pantalla.Lista -> ScalingLazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(top = 36.dp, bottom = 36.dp, start = 8.dp, end = 8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    item { Text("Reloj mundial", style = MaterialTheme.typography.title3) }
                    if (ciudades.isEmpty()) {
                        item {
                            Text(
                                "Sin ciudades.\nAgrega una o envíalas desde el teléfono.",
                                textAlign = TextAlign.Center,
                                style = MaterialTheme.typography.body2
                            )
                        }
                    }
                    items(ciudades) { ciudad ->
                        Chip(
                            modifier = Modifier.fillMaxWidth(),
                            label = { Text(hora(ciudad.zona, ahora, conSegundos = false)) },
                            secondaryLabel = {
                                Text(
                                    "${ciudad.nombre} · ${diferencia(ciudad.zona, ahora)}",
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            },
                            colors = ChipDefaults.secondaryChipColors(),
                            onClick = { pantalla = Pantalla.Detalle(ciudad) }
                        )
                    }
                    item {
                        Chip(
                            modifier = Modifier.fillMaxWidth(),
                            label = { Text("Agregar ciudad") },
                            colors = ChipDefaults.primaryChipColors(),
                            onClick = { pantalla = Pantalla.Agregar }
                        )
                    }
                }

                is Pantalla.Agregar -> {
                    val disponibles = CATALOGO.filter { cat -> ciudades.none { it.zona == cat.zona } }
                    ScalingLazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(top = 36.dp, bottom = 36.dp, start = 8.dp, end = 8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        item { Text("Elige una ciudad", style = MaterialTheme.typography.title3) }
                        items(disponibles) { cat ->
                            Chip(
                                modifier = Modifier.fillMaxWidth(),
                                label = { Text(cat.nombre, maxLines = 1, overflow = TextOverflow.Ellipsis) },
                                colors = ChipDefaults.secondaryChipColors(),
                                onClick = {
                                    scope.launch { dao.insertar(Ciudad(nombre = cat.nombre, zona = cat.zona)) }
                                    pantalla = Pantalla.Lista
                                }
                            )
                        }
                        item {
                            Chip(
                                modifier = Modifier.fillMaxWidth(),
                                label = { Text("Volver") },
                                colors = ChipDefaults.primaryChipColors(),
                                onClick = { pantalla = Pantalla.Lista }
                            )
                        }
                    }
                }

                is Pantalla.Detalle -> ScalingLazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(top = 36.dp, bottom = 36.dp, start = 8.dp, end = 8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    item { Text(p.ciudad.nombre, style = MaterialTheme.typography.title3) }
                    item { Text(hora(p.ciudad.zona, ahora), style = MaterialTheme.typography.display3) }
                    item { Text(fecha(p.ciudad.zona, ahora), style = MaterialTheme.typography.body2) }
                    item {
                        Text(
                            "Diferencia con tu hora: ${diferencia(p.ciudad.zona, ahora)}",
                            style = MaterialTheme.typography.body2,
                            textAlign = TextAlign.Center
                        )
                    }
                    item {
                        Chip(
                            modifier = Modifier.fillMaxWidth(),
                            label = { Text("Eliminar") },
                            colors = ChipDefaults.secondaryChipColors(),
                            onClick = {
                                scope.launch { dao.eliminar(p.ciudad) }
                                pantalla = Pantalla.Lista
                            }
                        )
                    }
                    item {
                        Chip(
                            modifier = Modifier.fillMaxWidth(),
                            label = { Text("Volver") },
                            colors = ChipDefaults.primaryChipColors(),
                            onClick = { pantalla = Pantalla.Lista }
                        )
                    }
                }
            }
        }
    }
}
