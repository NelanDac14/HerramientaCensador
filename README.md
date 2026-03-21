# Herramienta Censador - Documentación Técnica Master

## Descripción
Solución corporativa para la gestión de censos y bitácora de visitas comerciales. El sistema permite la transición de un registro estático a un historial dinámico de seguimientos por cliente.

## Arquitectura de Software
*   **Layered Architecture:** Separación clara entre UI, Lógica de Negocio y Persistencia.
*   **Relational Database:** Esquema SQLite v4 con soporte de Integridad Referencial (Cascade Delete).
*   **Local Time Sync:** Sistema de normalización horaria basado en el dispositivo (Local Timezone).

## Características Senior
1.  **Bitácora Maestro-Detalle:** Registro recurrente de visitas por cliente sin duplicidad de datos.
2.  **Búsqueda Dinámica:** Filtro en tiempo real sobre RecyclerView mediante búsqueda textual y temporal.
3.  **Edge-to-Edge UX:** Interfaz adaptativa que respeta los límites físicos de pantallas modernas (Xiaomi/Pixel).
4.  **Secure File Sharing:** Uso de FileProvider para intercambio de evidencias y reportes Excel.

## Requisitos Técnicos
*   **SDK Mínimo:** 35.
*   **Base de Datos:** Versión 4 (Fuerza recreación de bitácora y hora local).
*   **Permisos:** Location (Precisión), Camera, Storage.

## Mantenibilidad
Todo el código fuente cuenta con documentación Javadoc y comentarios técnicos que detallan la responsabilidad de cada método y bloque de lógica.
