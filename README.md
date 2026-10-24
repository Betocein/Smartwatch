# Reloj Mundial (Wear OS + Android)

App de reloj mundial: muestra la hora actual de las ciudades que elijas.

- **wear/**: app del reloj (Wear OS). Funciona sola, con su propia base de datos.
- **mobile/**: app del teléfono. Administra las ciudades y las envía al reloj.
- **Base de datos:** Room (SQLite), tabla `ciudades` (id, nombre, zona). Cada app tiene la suya.
- **Sincronización:** el teléfono envía la lista al reloj con la Data Layer de Wear OS
  (`PutDataMapRequest` en `/ciudades`) y el reloj la recibe en `SincronizacionService`.

## Cómo correrlo

1. Abre la carpeta `RelojMundial` con **Android Studio** (File > Open) y espera a que termine el Gradle Sync (necesita internet la primera vez).
2. En **Device Manager** crea los emuladores:
   - Un **Wear OS** (por ejemplo "Wear OS Large Round", API 30 o superior).
   - Un **teléfono con Google Play** (por ejemplo Pixel 6, icono de Play Store) solo si quieres probar la sincronización.
3. Elige la configuración **wear**, selecciona el emulador del reloj y presiona Run. Ya puedes agregar ciudades desde el reloj.
4. Para probar el teléfono, elige la configuración **mobile** y el emulador del teléfono.

### Vincular teléfono y reloj (opcional, para "Enviar al reloj")
En Device Manager, en el emulador Wear OS abre el menú de opciones y usa **Pair Wearable** (o desde el emulador del teléfono, Wear OS > Pair). Después, en la app del teléfono toca **Enviar al reloj**.

## Estructura
```
RelojMundial/
├── mobile/  (Compose Material3 + Room + play-services-wearable)
└── wear/    (Compose for Wear OS + Room + servicio de sincronización)
```
Ambos módulos usan el mismo `applicationId` (`com.relojmundial.app`); es requisito para que se comuniquen.
