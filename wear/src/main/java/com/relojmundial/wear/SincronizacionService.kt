package com.relojmundial.wear

import androidx.room.withTransaction
import com.google.android.gms.wearable.DataEvent
import com.google.android.gms.wearable.DataEventBuffer
import com.google.android.gms.wearable.DataMapItem
import com.google.android.gms.wearable.WearableListenerService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.ZoneId

/** Recibe las ciudades del telefono y reemplaza la base de datos local del reloj. */
class SincronizacionService : WearableListenerService() {

    override fun onDataChanged(dataEvents: DataEventBuffer) {
        for (evento in dataEvents) {
            if (evento.type == DataEvent.TYPE_CHANGED && evento.dataItem.uri.path == "/ciudades") {
                // Se leen los datos AHORA, porque el buffer se libera al terminar este metodo
                val mapa = DataMapItem.fromDataItem(evento.dataItem).dataMap
                val nombres = mapa.getStringArray("nombres") ?: emptyArray()
                val zonas = mapa.getStringArray("zonas") ?: emptyArray()

                val lista = nombres.zip(zonas)
                    .filter { (_, zona) -> runCatching { ZoneId.of(zona) }.isSuccess }
                    .map { (nombre, zona) -> Ciudad(nombre = nombre, zona = zona) }

                val db = AppDatabase.obtener(this)
                CoroutineScope(Dispatchers.IO).launch {
                    db.withTransaction {
                        db.ciudadDao().borrarTodas()
                        db.ciudadDao().insertarVarias(lista)
                    }
                }
            }
        }
    }
}
