# Herramienta Censador

## Descripción del Proyecto
Herramienta Censador es una solución tecnológica desarrollada para la plataforma Android, diseñada para optimizar los procesos de recolección de datos comerciales y censos de campo. La aplicación permite a los usuarios registrar visitas detalladas, capturar evidencia fotográfica, obtener coordenadas geográficas de alta precisión y gestionar un historial completo de actividades con capacidades de exportación de datos.

## Arquitectura Técnica
El proyecto está construido bajo un patrón de arquitectura imperativa, utilizando componentes nativos de Android y librerías de terceros de alto rendimiento para garantizar la estabilidad y escalabilidad del sistema.

### Tecnologías y Librerías Principales
*   **Lenguaje de Programación:** Java (JDK 11).
*   **Persistencia de Datos:** SQLite mediante la implementación de un patrón Data Access Object (DAO).
*   **Servicios de Ubicación:** Google Play Services (Fused Location Provider API) para geolocalización de alta precisión.
*   **Mapas e Interfaz Geográfica:** Google Maps SDK for Android.
*   **Gestión de Imágenes:** Glide (v5.0.0+) para la carga asíncrona y optimización de recursos visuales.
*   **Procesamiento de Documentos:** Apache POI para la generación dinámica de reportes en formato Microsoft Excel (.xlsx).
*   **Interfaz de Usuario:** Material Components for Android, asegurando el cumplimiento de las guías de diseño de Google.

## Estructura de Módulos

### 1. Gestión de Visitas (vistas_usuario)
*   **Act_NuevaVisita:** Módulo central para la captura de información. Integra validaciones de negocio, acceso a la cámara mediante FileProvider y lógica de obtención de coordenadas mediante FusedLocationClient.
*   **ListaVisitasActivity:** Provee una vista histórica de los registros. Implementa filtros temporales avanzados (diario, mensual, anual) y orquesta el proceso de exportación a Excel.
*   **MapaVisitasActivity:** Capa de visualización geográfica que proyecta los registros comerciales mediante marcadores interactivos sobre una instancia de Google Maps.

### 2. Capa de Datos (modelos y basedatos)
*   **Visita:** Clase modelo que actúa como DTO (Data Transfer Object), representando la entidad de negocio con todos sus atributos comerciales y técnicos.
*   **VisitaDAO:** Clase responsable de la lógica de persistencia. Implementa las operaciones CRUD y las consultas especializadas para el filtrado de datos.
*   **BaseDatos:** Extensión de SQLiteOpenHelper que define el esquema DDL del sistema y gestiona el ciclo de vida de la base de datos local.
*   **VisitasAdapter:** Adaptador especializado para RecyclerView que gestiona la vinculación de datos y provee un menú de acciones contextuales (WhatsApp, Maps, Compartir).

## Funcionalidades Críticas
*   **Geolocalización Inversa:** Generación automática de enlaces de Google Maps a partir de coordenadas GPS capturadas.
*   **Seguridad de Archivos:** Implementación de FileProvider para el intercambio seguro de URIs de imágenes y documentos exportados con aplicaciones de terceros.
*   **Exportación de Datos:** Generación de reportes .xlsx almacenados en el directorio público de documentos para facilitar la auditoría externa.
*   **Integración de Comunicación:** Acceso directo a servicios de mensajería (WhatsApp) para contacto inmediato con los clientes censados.

## Requisitos de Instalación
*   **Android SDK:** Nivel de API mínimo 35.
*   **Permisos Requeridos:**
    *   `ACCESS_FINE_LOCATION`: Para geolocalización de precisión.
    *   `CAMERA`: Para la captura de evidencia fotográfica.
    *   `WRITE_EXTERNAL_STORAGE`: Para la exportación de reportes Excel (en versiones compatibles).

## Consideraciones de Seguridad
La aplicación utiliza un esquema de permisos en tiempo de ejecución (Runtime Permissions) y cumple con las políticas de almacenamiento con visibilidad limitada (Scoped Storage) para proteger la privacidad del usuario y la integridad de los datos comerciales.
